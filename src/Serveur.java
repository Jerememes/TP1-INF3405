import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
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

		console.close();
	}

	private static boolean isValidPort(String port) {
		try {
			int number = Integer.parseInt(port);
			if (number >= 5000 || number <= 5050) {
				return true;
			}
		} catch(NumberFormatException e) {
			return false;
		}
		return false;
	}

	private static boolean isValidIp(String ip) {
		String[] ipNumbers = ip.split("\\.");
		if (ipNumbers.length == 4) {
			for(String nb : ipNumbers) {
				try {
					int number = Integer.parseInt(nb);
					if (number <= 255 || number >= 0) {
						return true;
					}
				} catch(NumberFormatException e) {
					return false;
				}
			}
		}
		return false;
	}
}