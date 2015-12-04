import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
/*
 * Handle requests
 */
public abstract class RequestHandlerThread extends Thread {

	public Queue<Packet> queue;
	public String[] ip;
	public int[] ports;
	public ServerNode server;
	public int index;
	public Queue<Packet> ackQueue;

	/**
	 * Constructor
	 * @param server
	 * @param queue Queue of packets to be handled
	 */
	public RequestHandlerThread(ServerNode server, Queue<Packet> queue) {
		this.queue = queue;
		this.ip = server.ip;
		this.ports = server.serverPorts;
		this.server = server;
		this.index = server.index;
		this.ackQueue = new LinkedBlockingQueue<Packet>();
	}
	
	/**
	 * Send out the packet
	 * @param p Packet to be sent
	 * @param dest Destination node index
	 */
	public void sendPacket(Packet p, int dest){
		sendPacket(p,dest,true);
	}
	
	// send out the packet
	private void sendPacket(Packet p, int dest, boolean getAck) {
		try {
			Socket serverOutSocket = new Socket(ip[dest], ports[dest]);
			ObjectOutputStream outToServer = new ObjectOutputStream(
					serverOutSocket.getOutputStream());
			outToServer.writeObject(p);
			System.out.println(index + ": Sent from server: " + p.toString());
			ObjectInputStream inFromServer = new ObjectInputStream(
					serverOutSocket.getInputStream());
			try {
				Packet ack = (Packet) inFromServer.readObject();
				// Put ack packet into delay channel if source is waiting for ack packets
				if (getAck)
					delayAck(ack);
			} catch (ClassNotFoundException e) {
				System.out.println("Received unrecoginized packet.");
				e.printStackTrace();
			}
			serverOutSocket.close();
		} catch (UnknownHostException e) {
			System.out.println("Cannot find host for node " + dest);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Cannot send messages to server.");
			e.printStackTrace();
		}
	}

	/**
	 * Reply to the source node
	 * @param p Packet to be sent
	 * @param outToServer OutputStream to send the reply
	 */
	public void replyToSender(Packet p, ObjectOutputStream outToServer) {
		try {
			p.setReturnChannel(null);
			outToServer.writeObject(p);
			outToServer.close();
		} catch (IOException e) {
			System.out.println("Cannot reply to sender!");
			e.printStackTrace();
		}
	}

	/**
	 * Put the ack packet into a delay channel
	 * @param p Packet to be delayed
	 */
	public void delayAck(Packet p) {
		MessageChannelThread channel = new MessageChannelThread(ackQueue, p,
				this);
		channel.start();
	}

	/**
	 * 
	 * @param p Packet to be handled
	 */
	abstract public void handleRequest(Packet p);

}
