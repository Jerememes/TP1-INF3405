import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static Socket socket;
    private static DataInputStream in;
    private static DataOutputStream out;

    public static void main(String[] args) throws Exception {
        int port = 5000;
        String serverAddress = "127.0.0.1";
        socket = new Socket(serverAddress, port);

        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
        String helloMessageFromServer = in.readUTF();

        System.out.format("Serveur lancé sur [%s:%d] ", serverAddress, port);
        System.out.println(helloMessageFromServer);

        communication();

        socket.close();
    }

    public static void communication() {
        Scanner console = new Scanner(System.in);
        while (true) {
            try {
                String command = console.nextLine();
                out.writeUTF(command);
                System.out.println(in.readUTF());
            } catch (IOException e) {
                System.out.println("Error while sending request to server");
                break;
            }
        }
    }
}