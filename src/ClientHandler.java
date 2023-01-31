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
        String result = "";
        File repertoireCourant;
        try {
            switch (commandName) {
                case "cd":
                    System.out.println("Handling command cd");
                    break;
                case "ls":
                    System.out.println("Handling command ls");
                    repertoireCourant = new File(System.getProperty("user.dir"));
                    File[] listFile = repertoireCourant.listFiles();
                    if(listFile == null) {
                    	result = "Ce dossier est vide";
                    }
                    for(File file : listFile) {
                        if (file.isDirectory()) {
                            result += "[Folder] " + file.getName() + "\n";
                        } else if (file.isFile()){
                            result += "[File] " + file.getName() + "\n";
                        }
                    }
                    break;
                case "mkdir":
                    System.out.println("Handling command mkdir");
                    repertoireCourant = new File(System.getProperty("user.dir"));
				    File Dossier = new File(repertoireCourant, parameter);
				    boolean res = Dossier.mkdir();
				
				if (res) {
					result = "Le dossier " + parameter + " a été créé.";
				} else {
					result = "Le dossier " + parameter + " n'a pas été créé.";
				}
                    break;
                case "upload":
                    System.out.println("Handling command upload");
                    break;
                case "download":
                    System.out.println("Handling command download");
                    break;
                case "exit":
                    try {
                        socket.close();
                    } catch (IOException e) {
                        System.out.println("Couldn't close a socket, what's going on?");
                    }
                    System.out.println("Connection with client# " + clientNumber + " closed");
                    break;
                default:
                    System.out.println("Can't handle the request");
                    result = "Either you made a mistake writing the command or an error occurred";
            }
            out.writeUTF(result);
        } catch (IOException e) {
            System.out.println("Error handling client#" + clientNumber + ": " + e);
        }
    }
}