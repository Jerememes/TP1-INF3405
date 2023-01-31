import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Scanner;
public class Serveur {
	private static ServerSocket Listener;
	private static int clientNumber = 0;
	static int serverPort = 5000;
	static String serverAddress = "127.0.0.1";
	public static void main(String[] args) throws Exception {		
		initServeur();
		Listener = new ServerSocket();
		Listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(serverAddress);
		Listener.bind(new InetSocketAddress(serverIP, serverPort));
		System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);

		try {
			// À chaque fois qu'un nouveau client se connecte, la méthode run() de ClientHandler est exécuté (thread)
			while (true) {
				// Important : la fonction accept() est bloquante: attend qu'un prochain client se connecte
				new ClientHandler(Listener.accept(), clientNumber++).start();
			}
		} finally {
			// Fermeture de la connexion
			Listener.close();
		}
	}

	private static void initServeur() {
		Scanner console = new Scanner(System.in);
		System.out.println("Bonjour !");

		while(true) {
			System.out.println("Veuillez rentrer l'adresse IP : ");
			String ip = console.nextLine();
			if (isValidIp(ip)) {
				serverAddress = ip;
				break;
			} else {
				System.out.println("L'adresse n'est pas valides");

			}
		}

		while(true) {
			System.out.println("Veuillez rentrer le port de connexion : ");
			String port = console.nextLine();
			if (isValidPort(port)) {
				int p = Integer.parseInt(port);
				serverPort = p;
				break;
			} else {
				System.out.println("Le port n'est pas valide");
			}
		}
		
		
	}

	private static boolean isValidPort(String port) {
		try {
			int nombre = Integer.parseInt(port);
			if (nombre < 5000 || nombre > 5050) {
				return false;
			}
		} catch(NumberFormatException e) {
			return false;
			
		}
		
		return true;
	}

	private static boolean isValidIp(String ip) {
		String[] Ip = ip.split("\\.");
		if (Ip.length != 4) {
			return false;
		}
		for(String nb : Ip) {
			try {
				int nombre = Integer.parseInt(nb);
				if (nombre > 255 || nombre < 0) {
					return false;
				}
			} catch(NumberFormatException e) {
				return false;
				
			}
		}
		return true;
	}
}