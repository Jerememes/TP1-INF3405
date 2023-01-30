
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.io.File;
public class ClientHandler extends Thread { // pour traiter la demande de chaque client sur un socket particulier
	private Socket socket;
	private int clientNumber;
	public ClientHandler(Socket socket, int clientNumber) {
		this.socket = socket;
		this.clientNumber = clientNumber;
		System.out.println("New connection with client#" + clientNumber + " at" + socket);
	}
	public void run() { // Création de thread qui envoi un message à un client
		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // création de canal d’envoi
			DataInputStream input = new DataInputStream(socket.getInputStream());
			
			out.writeUTF("Hello from server - you are client#" + clientNumber); // envoi de message
			
			// Traitement des commandes
			String command = input.readUTF();
			String[] splitCommand = command.split(" ");
			String result = "";

			// Commande ls
			if (splitCommand[0].equals("ls")) { 
				File repertoireCourant = new File(System.getProperty("user.dir"));
				File[] listFile = repertoireCourant.listFiles();
				
				for(File file : listFile) {
					if (file.isDirectory()) {
						result += "[Folder] " + file.getName() + "\n";
					} else if (file.isFile()){
						result += "[File] " + file.getName() + "\n";
					}
					
				}
				
			}
			
			// Commande mkdir
			else if (splitCommand[0].equals("mkdir")) {
				File repertoireCourant = new File(System.getProperty("user.dir"));
				String dossierName = splitCommand[1];
				File Dossier = new File(repertoireCourant, dossierName);
				boolean res = Dossier.mkdir();
				
				if (res) {
					result = "Le dossier " + dossierName + " a été créé.";
				} else {
					result = "Le dossier " + dossierName + " n'a pas été créé.";
				}
			}	
			
			else {
				result = "Unknow Command";
				
			}
			
			out.writeUTF(result);

			
			
		} catch (IOException e) {
			System.out.println("Error handling client# " + clientNumber + ": " + e);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("Couldn't close a socket, what's going on?");
			}
			System.out.println("Connection with client# " + clientNumber + " closed");
		}
	}
}