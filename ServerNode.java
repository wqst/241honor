import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
/*
 * Start server and client threads
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
	private HashMap<Integer, ValueTime> keyValueStore = new HashMap<Integer, ValueTime>();
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

	/**
	 * Print all the key-value pairs stored in this server
	 */
	public void printKeyValueStore() {
		DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		for (int key : keyValueStore.keySet()) {
			ValueTime vt = keyValueStore.get(key);
			System.out.println("Key: " + key + ", Value: " + vt.getValue()
					+ ", Time: " + df.format(vt.getTime()));
		}
	}

	/**
	 * Perform key-value operation
	 * @param p The packet containing the operation request
	 */
	public void keyValueOperation(Packet p) {
		ValueTime vt, vt_old;
		switch (p.getOperation()) {
		case "delete":
			if (keyValueStore.remove(p.getKey()) == null)
				System.out.println(index + ": " + p.getKey()
						+ " is not in the store");
			else
				System.out.println(index + ": " + p.getKey()
						+ "has been deleted.");
			break;
		case "search":
			if ((vt = keyValueStore.get(p.getKey())) == null) {
				System.out.println(index + ": " + p.getKey()
						+ " is not in the store");
				p.setValueTime(null);
			} else {
				p.setValueTime(vt);
				System.out.println(index + ": " + p.getKey()
						+ " has already been put in the store");
			}
			break;
		case "get":
			if ((vt = keyValueStore.get(p.getKey())) == null) {
				System.out.println(index + ": " + p.getKey()
						+ " is not in the store");
				p.setValueTime(null);
			} else {
				p.setValueTime(vt);
				System.out.println(index + ": Returned key = " + p.getKey()
						+ ", value = " + vt.getValue());
			}
			break;
		case "insert":
			vt = p.getValueTime();
			if ((vt_old = keyValueStore.get(p.getKey())) == null) {
				keyValueStore.put(p.getKey(), vt);
				System.out.println(index + ": Inserted " + p.getKey() + ", "
						+ vt.getValue());
			} else {
				System.out.println(index + ": Key = " + p.getKey()
						+ " already exists.");
			}
			break;
		case "update":
			vt = p.getValueTime();
			if ((vt_old = keyValueStore.get(p.getKey())) == null) {
				p.setValueTime(null);
				System.out.println(index + ": " + p.getKey()
						+ " is not in the store");
			} else  {
				keyValueStore.put(p.getKey(), vt);
				System.out.println(index + ": Updated " + p.getKey() + ", "
						+ vt.getValue());
			}
			break;
		case "show-all":
			printKeyValueStore();
			break;
		default:
			System.out.println("Unrecognized operation: " + p.getOperation());
		}
	}

}
