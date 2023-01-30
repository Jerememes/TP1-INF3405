import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;
// Application client
public class Client {
	private static Socket socket;
	public static void main(String[] args) throws Exception {
		// Adresse et port du serveur
		String serverAddress = "127.0.0.1";
		int port = 5000;
		// Création d'une nouvelle connexion aves le serveur
		socket = new Socket(serverAddress, port);
		System.out.format("Serveur lancé sur [%s:%d]", serverAddress, port);
		// Céatien d'un canal entrant pour recevoir les messages envoyés, par le serveur
		DataInputStream in = new DataInputStream(socket.getInputStream());
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		// Attente de la réception d'un message envoyé par le, server sur le canal
		String helloMessageFromServer = in.readUTF();
		System.out.println(helloMessageFromServer);
		
		Scanner scanner = new Scanner(System.in);
		while(true) {
			System.out.print("Enter a command: ");
            String command = scanner.nextLine();
            if(command.equals("exit")) {
            	break;
            }
            out.writeUTF(command);

            String response = in.readUTF();
            System.out.println(response);
		}
		// fermeture de La connexion avec le serveur
		socket.close();
	}
}