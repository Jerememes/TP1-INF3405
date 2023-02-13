import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static Socket socket;
    private static DataInputStream in;
    private static DataOutputStream out;
    private static int serverPort;
    private static String serverAddress;
    public static void main(String[] args) throws Exception {
    	initClient();
        
        socket = new Socket(serverAddress, serverPort);
        
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
        String helloMessageFromServer = in.readUTF();

        System.out.format("Serveur lancé sur [%s:%d] ", serverAddress, serverPort);
        System.out.println(helloMessageFromServer);

        communication();

        socket.close();
    }

    private static void initClient() {
    	System.out.println("Bonjour !");
    	Scanner console = new Scanner(System.in);
        

	        while(true) {
	            System.out.println("Veuillez rentrer l'adresse IP du serveur : ");
	            String ip = console.nextLine();
	            if (isValidIp(ip)) {
	                serverAddress = ip;
	                break;
	            } else {
	                System.out.println("L'adresse n'est pas valide.");
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
	            	System.out.println("Le port n'est pas valide.");
	            }
	        }
        
        	//console.close();
        
    }

    private static boolean isValidPort(String port) {
        try {
            int number = Integer.parseInt(port);
            if (number >= 5000 && number <= 5050) {
                return true;
            }
        } catch(NumberFormatException e) {
            return false;
        }
        return false;
    }

    private static boolean isValidIp(String ip) {
        boolean result = false;
        String[] ipNumbers = ip.split("\\.");
        if (ipNumbers.length == 4) {
            for(String nb : ipNumbers) {
                try {
                    int number = Integer.parseInt(nb);
                    if (number > 255 || number < 0) {
                        return false;
                    }
                    result = true;
                } catch(NumberFormatException e) {
                    return false;
                }
            }
        }
        return result;
    }
		
	

	public static void communication() {
        Scanner console = new Scanner(System.in);
        while (true) {
            try {
                String command = console.nextLine();
                out.writeUTF(command);

                if(command.equals("exit")) {
                    console.close();
                    break;
                }
                System.out.println(in.readUTF());

            } catch (IOException e) {
                System.out.println("Error while sending request to server");
                break;
            }
        }
    }
}