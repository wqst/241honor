import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
/*
 * Receive packets and put them into delay channels
 */
public class ServerThread extends Thread {
	private int[] ports;
	private int index;
	private Queue<Packet> serverMessageQueue;
	private Thread sh;

	/**
	 * Constructor
	 * @param server
	 * @param qs Queue for server packets
	 */
	public ServerThread(ServerNode server, Queue<Packet> qs) {
		this.ports = server.serverPorts;
		this.index = server.index;
		this.serverMessageQueue = qs;
		this.sh = server.getServerHandler();
	}

	public void run() {
		try (ServerSocket welcomeSocket = new ServerSocket(ports[index])) {
			System.out.println(this.index + ": Listening to message on port "
					+ ports[index]);
			while (true) {
				// Accept communication request from client
				Socket connectionSocket = welcomeSocket.accept();
				ObjectInputStream inFromClient = new ObjectInputStream(
						connectionSocket.getInputStream());
				Packet p = (Packet) inFromClient.readObject();

				// Check destination
				if (p.getDest() != this.index) {
					System.out.println("Received wrong message! Destination = "
							+ p.getDest() + ",Current Index = " + index);
					continue;
				}
				
				// Put the received packet into the delay channel
					MessageChannelThread channel = new MessageChannelThread(
							serverMessageQueue, p, sh);
					channel.start();
			}
		} catch (IOException e) {
			System.out.println("Cannot get message from port "
					+ this.ports[index]);
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Received unrecoginized packet.");
			e.printStackTrace();
		}
	}

}
