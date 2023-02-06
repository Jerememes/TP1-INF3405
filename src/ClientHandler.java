import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.io.File;

public class ClientHandler extends Thread { // Pour traiter la demande de chaque client sur un socket particulier
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private int clientNumber;

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
                    if (commandFromClient[0].startsWith("exit"))
                        return;
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
                case "cd":
                    System.out.println("Handling command cd");
                    result = handleCommandCd(commandName, parameter);
                    break;
                case "ls":
                    System.out.println("Handling command ls");
                    result = handleCommandLs(commandName, parameter);
                    break;
                case "mkdir":
                    System.out.println("Handling command mkdir");
                    result = handleCommandMkdir(commandName, parameter);
                    break;
                case "upload":
                    System.out.println("Handling command upload");
                    result = handleCommandUpload(commandName, parameter);
                    break;
                case "download":
                    System.out.println("Handling command download");
                    result = handleCommandDownload(commandName, parameter);
                    break;
                case "exit":
                    System.out.println("Handling command exit");
                    handleCommandExit(commandName, parameter);
                    return;
                default:
                    System.out.println("Can't handle the request");
                    result = "Either you made a mistake writing the command or an error occurred.";
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
        File folder = new File(directory, parameter);

        if (folder.mkdir()) {
            result = "Le dossier " + parameter + " a été créé.";
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
