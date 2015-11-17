import java.util.Queue;
/*
 * Delay the message and then put it into a queue
 */
public class MessageChannelThread extends Thread {
	private Queue<Packet> queue;
	private Packet p;
	private Thread handler;

	/**
	 * Constructor
	 * @param queue The queue that holds the packet
	 * @param p The packet to be delayed
	 * @param handler Thread that handles the packet
	 */
	public MessageChannelThread(Queue<Packet> queue, Packet p, Thread handler) {
		this.queue = queue;
		this.p = p;
		this.handler = handler;
	}

	public void run() {
		double delay = calDelay();
		try {
			// delay the packet in the channel
			sleep((long) delay * 1000);
		} catch (InterruptedException e) {
			System.out.println("Sleep interrupted");
			e.printStackTrace();
		}
		queue.add(p);

		// Wake up the handler thread
		synchronized (handler) {
			handler.notify();
		}

	}

	/**
	 * Calculate delay time of the packet
	 * 
	 * @return delayTime (second)
	 */
	private double calDelay() {
		if (p.getSource() == p.getDest())
			return 0.0;
		else
			return Math.random() * (double) p.getMaxDelay();

	}

}
