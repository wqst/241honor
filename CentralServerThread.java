import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
/*
 * Receive packets at central server and put them into delay channels
 */
public class CentralServerThread extends Thread {
	private int[] ports;
	private int index;
	private Queue<Packet> messageQueue;
	private RequestHandlerThread centralHandler;

	/**
	 * Constructor 
	 * @param server 
	 * @param queue Queue of packets at central server
	 */
	public CentralServerThread(ServerNode server, Queue<Packet> queue) {
		ports = server.serverPorts;
		index = server.index;
		messageQueue = queue;
		centralHandler = server.getServerHandler();
	}

	public void run() {
		try (ServerSocket welcomeSocket = new ServerSocket(ports[index])) {
			System.out.println("Central Server (" + this.index + "): Listening to message on port "
					+ ports[index]);
			while (true) {
				// Accept communication request from client
				Socket connectionSocket = welcomeSocket.accept();
				ObjectInputStream inFromClient = new ObjectInputStream(
						connectionSocket.getInputStream());
				ObjectOutputStream outToClient = new ObjectOutputStream(
						connectionSocket.getOutputStream());
				Packet p = (Packet) inFromClient.readObject();

				//Check destination
				if (p.getDest() != this.index && p.getOperation() != "send") {
					System.out.println("Received wrong message! Destination = "
							+ p.getDest() + ",Current Index = " + index);
					continue;
				}
				
				// Put the received packet into the delay channel
				p.setReturnChannel(outToClient);
				MessageChannelThread channel = new MessageChannelThread(
						messageQueue, p, centralHandler);
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
