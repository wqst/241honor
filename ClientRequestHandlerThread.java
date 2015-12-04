import java.io.ObjectOutputStream;
import java.util.Queue;
/*
 * Handle requests from client
 */
public class ClientRequestHandlerThread extends RequestHandlerThread {
	private ObjectOutputStream outChannel;
	private int source;
	private boolean ready; // Ready to handle new packets
	private int ackCount; // Count of acks to be handled
	private Packet currentPacket;
	private boolean[] hasKey; // Store results from search command

	/**
	 * Constructor
	 * @param server
	 * @param queue Queue of packets waiting to be handled
	 */
	public ClientRequestHandlerThread(ServerNode server, Queue<Packet> queue) {
		super(server, queue);
		ready = true;
		ackCount = 0;
		hasKey = new boolean[server.totalNodes];
		for (int i = 0; i < server.totalNodes; i++) {
			hasKey[i] = false;
		}
	}

	public void run() {
		while (true) {
			Packet p;
			if (ready) {
				// Handle new packets
				if ((p = queue.poll()) != null) {
					if (server.delay > 0) {
						try {
							// server response delay
							sleep((long) server.delay);
						} catch (InterruptedException e) {
							System.out.println("Sleep interupted.");
							e.printStackTrace();
						}
					}
					handleRequest(p);
				} else {
					synchronized (this) {
						try {
							this.wait();
							// Sleep the thread if there is no new packet available
						} catch (InterruptedException e) {
							System.out.println("Sleep interupted.");
							e.printStackTrace();
						}
					}
				}
			} else {
				// Handle acks
				if ((p = ackQueue.poll()) != null) {
					handleAck(p);
				} else {
					synchronized (this) {
						try {
							// Sleep the thread if no ack to be handled
							this.wait();
						} catch (InterruptedException e) {
							System.out.println("Sleep interupted.");
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	/**
	 * Forward request to corresponding servers
	 */
	public void handleRequest(Packet p) {
		System.out.println(index + ": Received from client: " + p.toString());
		outChannel = p.getReturnChannel();
		source = p.getSource();
		Packet p1;
		String op = p.getOperation();
		switch (op) {
		case "send":
			replyToSender(p, outChannel);
			break;
		case "show-all":
			server.printKeyValueStore();
			replyToSender(p, outChannel);
			break;
		case "search":
			for (int i = 0; i < server.totalNodes; i++) {
				int dest = i + 1;
				p1 = new Packet(index, dest, p, false);
				sendPacket(p1, dest);
				ackCount++;
			}
			ready = false;
			break;
		case "delay":
			server.delay = p.getKey();
			replyToSender(p, outChannel);
			break;
		default:
			// Key-value operations
			switch (p.getModel()) {
			case 1:
				p1 = new Packet(index, StartServer.centralServerID, p, false);
				sendPacket(p1, StartServer.centralServerID);
				break;
			case 2:
				if (p.getOperation().equals("get")) {
					server.keyValueOperation(p);
					replyToSender(p, outChannel);
				} else {
					p1 = new Packet(index, StartServer.centralServerID, p,
							false);
					sendPacket(p1, StartServer.centralServerID);
				}
				break;
			default:
				System.out.println("Unrecognized model " + p.getModel());

			}
			ready = false;
		}
	}

	/**
	 * Reply to client
	 * @param p
	 */
	public void handleAck(Packet p) {
		if (p.getOperation().equals("search")) {
			// return search results
			searchKey(p);
			ackCount--;
			if (ackCount <= 0) {
				Packet p1 = new Packet(index, source, p, true, hasKey);
				replyToSender(p1, outChannel);
				ready = true;
			}
			return;
		}

		if (p.getModel() == 1 || p.getModel() == 2) {
			// return key-value operation results
			Packet p1 = new Packet(index, source, p, true);
			replyToSender(p1, outChannel);
			ready = true;
		} 
	}


	// update the search result
	private void searchKey(Packet p) {
		if (p.getValueTime() != null) {
			hasKey[p.getSource()-1] = true;
		}
	}

	/**
	 * Update the value and time in current packet
	 * @param p Received packet
	 */
	public void updateCurrentPacket(Packet p) {
		if (p.getValueTime() == null)
			return;
		if (p.getValueTime().getTime()
				.compareTo(currentPacket.getValueTime().getTime()) > 0) {
			currentPacket = p;
		}
	}

}
