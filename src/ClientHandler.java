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
    private File currentPath;

    public ClientHandler(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
        this.clientPort = socket.getPort();
        this.clientAddress = socket.getRemoteSocketAddress().toString();
        System.out.println("New connection with client#" + clientNumber + " at " + socket);
    }

    public void run() { // Création de thread qui communique avec un client
        try {
            currentPath = new File(System.getProperty("user.dir"));
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
                    result = handleCommandCd(parameter);
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
                    if (parameter == null) {
                        handleCommandExit();
                        printCommandHandled(commandName, parameter);
                    }
                    return;
                default:
                    commandName = "Error"; parameter = "";
                    result = "Either you made a mistake writing the command or an error occurred";
            }
            out.writeUTF(result);
            printCommandHandled(commandName, parameter);
        } catch (IOException e) {
            System.out.println("Error handling client#" + clientNumber + ": " + e);
        }
    }

    private String handleCommandCd(String parameter) throws IOException {
        File wantedPath = new File(currentPath, parameter).getCanonicalFile();
        if (!wantedPath.exists()) {
            return "Impossible puisque le path n'existe pas";
        } else if (wantedPath.isFile()) {
            return "Impossible de se déplacer dans un fichier";
        }
        currentPath = wantedPath;
        return "Vous êtes dans le dossier " + currentPath.getAbsolutePath();
    }

    private String handleCommandLs() {
        String result = "";
        File[] fileList = currentPath.listFiles();
        for(File file : fileList) {
            if (file.isDirectory()) {
                result += "\n" + "[Folder] " + file.getName();
            } else if (file.isFile()){
                result +=  "\n" + "[File] " + file.getName();
            }
        }
        return result.replaceFirst("\n", "");
    }

    private String handleCommandMkdir(String parameter) {
        if (parameter != null) {
            File folder = new File(currentPath, parameter);
            if (folder.mkdir()) {
                return "Le dossier " + parameter + " a été créé.";
            }
        }
        return "Le dossier " + parameter + " n'a pas été créé.";
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
