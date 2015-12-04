import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
/*
 * Start threads at central server
 */
public class CentralServerNode extends ServerNode{
	private Queue<Packet> serverQueue = new LinkedBlockingQueue<Packet>();
	private Thread centralThread;
	private CentralServerRequestHandler centralHandler;

	/**
	 * Constructor
	 */
	public CentralServerNode() {
		super(StartServer.centralServerID);
	}
	
	/**
	 * Start threads
	 */
	public void startThreads(){
		centralHandler = new CentralServerRequestHandler(this, serverQueue);
		centralHandler.start();
		
		centralThread = new CentralServerThread(this,serverQueue);
		centralThread.start();
	}
	
	public RequestHandlerThread getServerHandler(){
		return centralHandler;
	}


}
