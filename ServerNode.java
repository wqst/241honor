import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * Start server and client threads
 *
 */
public class ServerNode {
	public int totalNodes;
	public int index;
	public int[] serverPorts;
	public String[] ip;
	public int maxDelay;
	private ClientThread client;
	private ServerThread server;
	private ServerRequestHandlerThread sh;
	private Queue<Packet> serverMessageQueue = new LinkedBlockingQueue<Packet>();
	public int delay; // delay in ms

	/**
	 * Constructor
	 * @param index Index of the server
	 */
	public ServerNode(int index) {
		this.index = index;
		this.delay = 0;
	}

	/**
	 * 
	 * @return Server request handling thread of this server
	 */
	public RequestHandlerThread getServerHandler() {
		return sh;
	}

	/**
	 * Start client thread and server thread
	 */
	public void startThreads() {
		sh = new ServerRequestHandlerThread(this, serverMessageQueue);
		sh.start();
		client = new ClientThread(this);
		server = new ServerThread(this, serverMessageQueue);
		client.start();
		server.start();
		System.out.println("Server Started... Id: " + index + ", IP Address: "
				+ ip[index] + ", Port: " + serverPorts[index]);
		System.out.print(">");
	}
}
