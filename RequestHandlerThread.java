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
	}
	
	/**
	 * 
	 * @param p Packet to be handled
	 */
	abstract public void handleRequest(Packet p);

}
