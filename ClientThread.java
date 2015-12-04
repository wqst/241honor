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
		String errorMessage = "Usage: [Operation(delete,get,insert,update)] [Key] [Value] [Model]\n>";
		while (true) {
			// Read message from command line
			try {
				String command = commandReader.readLine();
				String[] s = command.split(" ");
				int key = 0, value = 0, model = 0, dest = index;
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
					model = 1;
					op = "delete";
					break;
				case "get":
					key = Integer.valueOf(s[1]);
					model = Integer.valueOf(s[2]);
					op = "get";
					break;
				case "insert":
					key = Integer.valueOf(s[1]);
					value = Integer.valueOf(s[2]);
					model = Integer.valueOf(s[3]);
					op = "insert";
					break;
				case "update":
					key = Integer.valueOf(s[1]);
					value = Integer.valueOf(s[2]);
					model = Integer.valueOf(s[3]);
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
				sendPacket(p, index);

			} catch (IOException e) {
				System.out.println("Cannot read from console.");
				e.printStackTrace();
			} catch (NumberFormatException e) {
				System.out.print(errorMessage);
			}
			System.out.print(">");
		}
	}

	// Send the packet to servers and wait for acks
	private void sendPacket(Packet p, int dest) {
		try {
			Socket clientSocket = new Socket(ip[dest], serverPorts[dest]);
			ObjectOutputStream outToServer = new ObjectOutputStream(
					clientSocket.getOutputStream());
			outToServer.writeObject(p);
			System.out.println("Sent from client: " + p.toString());
			ObjectInputStream inFromServer = new ObjectInputStream(
					clientSocket.getInputStream());
			try {
				Packet ack = (Packet) inFromServer.readObject();
				handleAck(ack);
			} catch (ClassNotFoundException e) {
				System.out.println("Received unrecoginized packet.");
				e.printStackTrace();
			}
			clientSocket.close();
		} catch (UnknownHostException e) {
			System.out.println("Cannot find host for node " + dest);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Cannot send messages to server.");
			e.printStackTrace();
		}
	}
	
	// Print result based on ack packets
	private void handleAck(Packet p) {
		switch (p.getOperation()) {
		case ("delete"):
			System.out.println("Entries associated with key " + p.getKey()
					+ " have been successfully deleted on server "
					+ p.getSource());
			break;
		case ("get"):
			if (p.getValueTime() == null)
				System.out.println("Cannot get value for key = " + p.getKey()
						+ ", the key is not in the key value store.");
			else
				System.out.println("Result: key = " + p.getKey() + ", value = "
						+ p.getValueTime().getValue());
			break;
		case ("insert"):
			System.out.println("Key = " + p.getKey() + ", value = "
					+ p.getValueTime().getValue()
					+ " has been successfully inserted.");
			break;
		case ("update"):
			if (p.getValueTime() == null)
				System.out.println("Update failed. Key = " + p.getKey()
						+ " is not in the key value store.");
			else
				System.out.println("Key = " + p.getKey() + ", value = "
						+ p.getValueTime().getValue()
						+ " has been successfully updated.");
			break;
		case ("search"):
			boolean[] result = p.getSearchResult();
			System.out.print("Search Result: ");
			for (int i = 0; i < result.length; i++) {
				if (result[i])
					System.out.print((i+1) + ", ");
			}
			System.out.println();
			break;
		default:
			// Other operations do not require client to print anything.
		}
	}

}
