import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    static int serverPort;
	static String serverAddress;
    private static Scanner console;
    
    private static Socket socket;
    private static DataInputStream in;
    private static DataOutputStream out;

    public static void main(String[] args) throws Exception {
        console = new Scanner(System.in);
        System.out.println("Bonjour !");

        while(true) {
            try {
                connectToServeur();
                socket = new Socket(serverAddress, serverPort);
                out = new DataOutputStream(socket.getOutputStream());
                in = new DataInputStream(socket.getInputStream());
                String helloMessageFromServer = in.readUTF();
                System.out.format("Connecté au serveur lancé sur [%s:%d] ", serverAddress, serverPort);
                System.out.println(helloMessageFromServer);
                break;
            } catch (Exception e) {
                System.out.format("La communication n'a pas pu être établie sur [%s:%d]. ", serverAddress, serverPort);
                continue;
            }
        }

        communication();
        socket.close();
        console.close();
    }

	private static void connectToServeur() {
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
			System.out.println("Veuillez rentrer le port de connexion du serveur : ");
			String port = console.nextLine();
			if (isValidPort(port)) {
				serverPort = Integer.parseInt(port);
				break;
			} else {
				System.out.println("Le port n'est pas valide.");
			}
		}
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
                } else if (command.startsWith("download")) {
					int bytes = 0;
					String fileName = command.split(" ")[command.split(" ").length - 1];
					FileOutputStream fileOutputStream = new FileOutputStream(fileName);
					
					long size = in.readLong();
					byte[] buffer = new byte[1024];
					while (size > 0 && (bytes = in.read(buffer)) != -1) {
						fileOutputStream.write(buffer, 0, bytes);
						size -= bytes;
					}
					fileOutputStream.close();
				} else if (command.startsWith("upload")) {
					int bytes = 0;
					String fileName = command.split(" ")[command.split(" ").length - 1];
					File file = new File(System.getProperty("user-dir"), fileName);
					FileInputStream fileInputStream = new FileInputStream(file);
			
					out.writeLong(file.length());
					byte[] buffer = new byte[1024];
					while ((bytes=fileInputStream.read(buffer)) != -1){
						out.write(buffer, 0, bytes);
						out.flush();
					}
					fileInputStream.close();
				} else {
					System.out.println(in.readUTF());
				}
            } catch (IOException e) {
                System.out.println("Error while sending request to server");
                break;
            }
        }
    }
}