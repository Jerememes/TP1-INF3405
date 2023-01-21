import java.io.DataInputStream;
import java.net.Socket;

public class Client {
    private static Socket socket;
    public static void main(String[] args) throws Exception {
        int port = 5000;
        String serverAddress = "127.0.0.1";
        socket = new Socket(serverAddress, port);
        System.out.format("Serveur lancé sur [%s:%d]", serverAddress, port);

        DataInputStream in = new DataInputStream(socket.getInputStream());
        String helloMessageFromServer = in.readUTF();
        System.out.println(helloMessageFromServer);

        socket.close();
    }
}