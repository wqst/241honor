import java.io.ObjectOutputStream;
import java.util.Queue;
/*
 * Handle requests at central server
 */
public class CentralServerRequestHandler extends RequestHandlerThread {
	private int ackCount; // Count of acks to be handled
	private ObjectOutputStream outChannel;
	private int source;
	private boolean ready; // Ready to handle new packets

	/**
	 * Constructor
	 * @param server
	 * @param queue Queue of packets at central server
	 */
	public CentralServerRequestHandler(ServerNode server, Queue<Packet> queue) {
		super(server, queue);
		ready = true;
		ackCount = 0;
	}

	public void run() {
		while (true) { 
			Packet p;
			if (ready) { //ready: see whether start broadcasting or count the #ack
				// Handle new packets
				if ((p = queue.poll()) != null) {
					handleRequest(p);
				} else {
					synchronized (this) {
						try {
							// Sleep the thread if there is no new packet
							this.wait();
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
							// Sleep the thread if there is no ack packet
							this.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	private void handleAck(Packet p) {
		ackCount--;
		if (ackCount <= 0) {
			Packet p1 = new Packet(index, source, p, true);
			replyToSender(p1, outChannel);
			ready = true;
		}
	}

	/**
	 * Forward the request to other servers
	 */
	public void handleRequest(Packet p) {
		System.out.println(index + ": Received at central server: " + p.toString());
		outChannel = p.getReturnChannel();
		source = p.getSource();
		for (int i = 0; i < server.totalNodes; i++) {
			Packet p1 = new Packet(index, i + 1, p, false);
			sendPacket(p1, i + 1);
			ackCount++;
		}
		ready = false;
	}

}
