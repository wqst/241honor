import java.util.Queue;
/*
 * Handle requests from server
 */
public class ServerRequestHandlerThread extends RequestHandlerThread {
	/**
	 * Constructor
	 * @param server
	 * @param queue Queue of packets waiting to be handled
	 */
	public ServerRequestHandlerThread(ServerNode server, Queue<Packet> queue) {
		super(server, queue);
	}

	public void run() {
		while (true) {
			Packet p;
			// Check if there are available packets in the queue
			if ((p = queue.poll()) != null) {
				handleRequest(p);
			} else {
				synchronized (this) {
					try {
						// Sleep if there is no available packet
						this.wait();
					} catch (InterruptedException e) {
						System.out.println("Sleep interupted.");
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Perform key-value operation and reply to source server
	 */
	public void handleRequest(Packet p) {
		System.out.println(index + ": Received from server: " + p.toString());
		server.keyValueOperation(p);
		if (p.getOperation() != "send"){
			Packet p1 = new Packet(index, p.getSource(), p, true);
			replyToSender(p1, p.getReturnChannel());
		}
	}

}
