import java.io.*;
import java.net.Socket;
import java.io.FileInputStream;

public class ClientHandler extends Thread { // Pour traiter la demande de chaque client sur un socket particulier
    final private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private final int clientNumber;

    public ClientHandler(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
        System.out.println("New connection with client#" + clientNumber + " at " + socket);
    }

    public void run() { // Création de thread qui communique avec un client
        try {
            in = new DataInputStream(socket.getInputStream()); // Création de canal de réception
            out = new DataOutputStream(socket.getOutputStream()); // Création de canal d’envoi
            out.writeUTF("Hello from server - you are client#" + clientNumber);
        } catch (IOException e) {
            System.out.println("Error handling client#" + clientNumber + ": " + e);
        } finally {
            communication();
        }
    }

    private void communication() {
        while (true) {
            try {
                String[] commandFromClient = in.readUTF().split(" ");
                if (commandFromClient.length == 2) {
                    reducer(commandFromClient[0], commandFromClient[1]);
                } else if (commandFromClient.length == 1) {
                    reducer(commandFromClient[0], null);
                    if (commandFromClient[0].startsWith("exit")) return;
                } else {
                    System.out.println("Can't handle the request");
                    out.writeUTF("Either you made a mistake writing the command or an error occurred.");
                }

            } catch (IOException e) {
                System.out.println("Error handling client#" + clientNumber + ": " + e);
                break;
            }
        }
    }

    private void reducer(String commandName, String parameter) {
        String result;
        try {
            switch (commandName) {
                case "cd" -> {
                    System.out.println("Handling command cd");
                    result = handleCommandCd(commandName, parameter);
                }
                case "ls" -> {
                    System.out.println("Handling command ls");
                    result = handleCommandLs(commandName, parameter);
                }
                case "mkdir" -> {
                    System.out.println("Handling command mkdir");
                    result = handleCommandMkdir(commandName, parameter);
                }
                case "upload" -> {
                    System.out.println("Handling command upload");
                    result = handleCommandUpload(commandName, parameter);
                }
                case "download" -> {
                    System.out.println("Handling command download");
                    result = handleCommandDownload(commandName, parameter);
                }
                case "exit" -> {
                    System.out.println("Handling command exit");
                    handleCommandExit(commandName, parameter);
                    return;
                }
                default -> {
                    System.out.println("Can't handle the request");
                    result = "Either you made a mistake writing the command or an error occurred.";
                }
            }
            out.writeUTF(result);
        } catch (IOException e) {
            System.out.println("Error handling client#" + clientNumber + ": " + e);
        }
    }

    private String handleCommandCd(String commandName, String parameter) {
        File newDirectory = new File(parameter).getAbsoluteFile();
        if (newDirectory.exists() || newDirectory.mkdirs()) {
            String path = System.setProperty("user.dir", newDirectory.getAbsolutePath());
        }
        return "Vous êtes dans le dossier " + System.getProperty("user.dir");
    }

    private String handleCommandLs(String commandName, String parameter) {
        String result = "";
        File directory = new File(System.getProperty("user.dir"));
        File[] fileList = directory.listFiles();

        for(File file : fileList) {
            if (file.isDirectory()) {
                result += "[Folder] " + file.getName() + "\n";
            } else if (file.isFile()){
                result += "[File] " + file.getName() + "\n";
            }
        }

        return result;
    }

    private String handleCommandMkdir(String commandName, String parameter) {
        String result = "";
        File directory = new File(System.getProperty("user.dir"));

        if (parameter != null) {
            File folder = new File(directory, parameter);
            if (folder.mkdir()) {
                result = "Le dossier " + parameter + " a été créé.";
            }
        } else {
            result = "Le dossier " + null + " n'a pas été créé.";
        }

        return result;
    }

    private String handleCommandUpload(String commandName, String parameter) throws IOException {
        // TODO handles the file from the client to the server
        String s = "File not found";
        if(parameter != null){
            File fileToSend = new File(parameter);
            if(fileToSend.exists()){
                try {
                    Socket socket = new Socket("localhost", 5000);
                    DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                return s;
            }
        } else {
            return s;
        }
    }

    private String handleCommandDownload(String commandName, String parameter) throws IOException {
        // TODO downloads the file from the server to the client
        if(parameter != null) {
            File fileToReceive = new File(parameter);
            if(fileToReceive != null){
                try {
                    // Connect to the server's socket
                    Socket socket = new Socket("localhost", 5000);
                    // Create an input stream to receive data from the server
                    DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                    // Create an output stream to send data to the server
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    // Create a file output stream to write the file to the hard drive
                    FileOutputStream fileOutputStream = new FileOutputStream(fileToReceive);
                    // Create a byte array to store the file's or picture's data
                    byte[] buffer = new byte[1024];
                    // Create an integer to store the number of bytes read
                    int bytesRead;
                    // Read the file's or picture's data into the byte array
                    while ((bytesRead = dataInputStream.read(buffer)) > 0) {
                        // Write the file's or picture's data to the hard drive
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                    // Close the data input stream
                    dataInputStream.close();
                    // Close the data output stream
                    dataOutputStream.close();
                    // Close the file output stream
                    fileOutputStream.close();
                    // Close the socket
                    socket.close();
                } catch (IOException error) {
                    error.printStackTrace();
                    if (error instanceof FileNotFoundException) {
                        return "File not found";
                    } else {
                        return "Error receiving file";
                    }
                }
                return "File received";
            } else {
                return "File not found";
            }
        } else {
            return "No file specified";
        }
    }


    private void handleCommandExit(String commandName, String parameter) {
        if (parameter == null) {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Couldn't close a socket, what's going on?");
            }
            System.out.println("Connection with client# " + clientNumber + " closed");
        }
    }
}
