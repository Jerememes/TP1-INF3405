import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;

public class ClientHandler extends Thread { // Pour traiter la demande de chaque client sur un socket particulier
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    private int clientNumber;
    private int clientPort;
    private String clientAddress;

    public ClientHandler(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
        this.clientPort = socket.getPort();
        this.clientAddress = socket.getRemoteSocketAddress().toString();
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
                    if (commandFromClient[0].startsWith("exit"))
                        return;
                } else {
                    System.out.println("Can't handle the request");
                    out.writeUTF("Either you made a mistake writing the command or an error occurred");
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
                case "cd":
                    result = handleCommandCd(commandName, parameter);
                    break;
                case "ls":
                    result = handleCommandLs();
                    break;
                case "mkdir":
                    result = handleCommandMkdir(parameter);
                    break;
                case "upload":
                    result = handleCommandUpload(commandName, parameter);
                    break;
                case "download":
                    result = handleCommandDownload(commandName, parameter);
                    break;
                case "exit":
                    if (parameter == null) handleCommandExit();
                    return;
                default:
                    commandName = "Error";
                    parameter = "";
                    result = "Either you made a mistake writing the command or an error occurred";
            }
            out.writeUTF(result);
            printCommandHandled(commandName, parameter);
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

    private String handleCommandLs() {
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

    private String handleCommandMkdir(String parameter) {
        String result = "";
        File directory = new File(System.getProperty("user.dir"));

        if (parameter != null) {
            File folder = new File(directory, parameter);
            if (folder.mkdir()) {
                result = "Le dossier " + parameter + " a été créé.";
            }
        } else {
            result = "Le dossier " + parameter + " n'a pas été créé.";
        }

        return result;
    }

    private String handleCommandUpload(String commandName, String parameter) {
        // TODO
        return "TODO";
    }

    private String handleCommandDownload(String commandName, String parameter) {
        // TODO
        return "TODO";
    }

    
    private void handleCommandExit() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Couldn't close a socket, what's going on?");
        }
        System.out.println("Connection with client# " + clientNumber + " closed");
    }

    private void printCommandHandled(String commandName, String parameter) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd@HH:mm:ss");
        String status = "[" + clientAddress.replaceAll("/", "") + "." +
            clientPort + " - " + formatter.format(new Date()) + "] : ";
        if (parameter != null) {
            System.out.println(status + commandName + " " + parameter);
        }
        else {
            System.out.println(status + commandName);
        }
    }
}
