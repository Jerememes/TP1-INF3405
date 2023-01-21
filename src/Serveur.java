import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
public class Serveur {
	private static ServerSocket Listener;
	private static int clientNumber = 0;

	public static void main(String[] args) throws Exception {
		int serverPort = 5000;
		String serverAddress = "127.0.0.1";

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
}