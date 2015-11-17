import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
/*
 * Read commands from command line and send requests to servers
 */
public class ClientThread extends Thread {
	private String[] ip;
	private int[] serverPorts;
	private int index;
	private int maxDelay;

	/**
	 * Constructor
	 * @param server
	 */
	public ClientThread(ServerNode server) {
		this.ip = server.ip;
		this.serverPorts = server.serverPorts;
		this.index = server.index;
		this.maxDelay = server.maxDelay;
	}

	public void run() {
		BufferedReader commandReader = new BufferedReader(
				new InputStreamReader(System.in));
		String errorMessage = "Usage: [Operation(delete,get,insert,update)] [Key] [Value]\n>";
		while (true) {
			// Read message from command line
			try {
				String command = commandReader.readLine();
				String[] s = command.split(" ");
				int key = 0, value = 0, model = 1, dest = index;
				String op = "", message = "";
				int len = s.length;
				switch (s[0].toLowerCase()) {
				case "send":
					op = "send";
					for (int i = 1; i < len-1; i++)
						message += s[i] + " ";
					dest = Integer.valueOf(s[len-1]);
					break;
				case "delete":
					key = Integer.valueOf(s[1]);
					op = "delete";
					break;
				case "get":
					key = Integer.valueOf(s[1]);
					op = "get";
					break;
				case "insert":
					key = Integer.valueOf(s[1]);
					value = Integer.valueOf(s[2]);
					op = "insert";
					break;
				case "update":
					key = Integer.valueOf(s[1]);
					value = Integer.valueOf(s[2]);
					op = "update";
					break;
				case "show-all":
					op = "show-all";
					break;
				case "search":
					key = Integer.valueOf(s[1]);
					op = "search";
					break;
				default:
					System.out.println(errorMessage);
					continue;
				}
				Packet p = new Packet(index, dest, maxDelay, op, key, value,
						model, message);
				sendPacket(p, dest);

			} catch (IOException e) {
				System.out.println("Cannot read from console.");
				e.printStackTrace();
			} catch (NumberFormatException e) {
				System.out.print(errorMessage);
			}
			System.out.print(">");
		}
	}

	// Send the packet to servers
	private void sendPacket(Packet p, int dest) {
		try {
			Socket clientSocket = new Socket(ip[dest], serverPorts[dest]);
			ObjectOutputStream outToServer = new ObjectOutputStream(
					clientSocket.getOutputStream());
			outToServer.writeObject(p);
			System.out.println("Sent from client: " + p.toString());
			clientSocket.close();
		} catch (UnknownHostException e) {
			System.out.println("Cannot find host for node " + dest);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Cannot send messages to server.");
			e.printStackTrace();
		}
	}
}
