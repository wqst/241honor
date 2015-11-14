import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Packet containing the message
 *
 */
public class Packet implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int source, dest, maxDelay;
	private String message;
	private boolean fromClient;
	private Date sentTime;

	/**
	 * Generate new packet at client
	 * 
	 * @param source Source index
	 * @param dest Destination index
	 * @param maxDelay Maximum delay time (second)
	 */
	public Packet(int source, int dest, int maxDelay, String message) {
		this.source = source;
		this.dest = dest;
		this.maxDelay = maxDelay;
		this.sentTime = new Date();
		this.fromClient = true;
		this.message = message;
	}

	/*
	
	 * Generate new packet at server
	 * 
	 * @param source Source index
	 * @param dest Destination index
	 * @param p Old packet 
	public Packet(int source, int dest, Packet p) {
		this.source = source;
		this.dest = dest;
		this.maxDelay = p.getMaxDelay();
		this.sentTime = new Date();
		this.fromClient = false;
	}
	*/
	/**
	 * 
	 */
	public String toString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		String s = "Source: " + source + ", Destination: " + dest
				+ ", MaxDelay: " + maxDelay + ", SentTime: "
				+ df.format(sentTime) + ", Content: " + message;
		if (fromClient)
			s += ", sent from client";
		return s;
	}

	/**
	 * 
	 * @return source index
	 */
	public int getSource() {
		return source;
	}

	/**
	 * 
	 * @return true if the packet comes from client
	 */
	public boolean fromClient() {
		return fromClient;
	}

	/**
	 * 
	 * @return destination index
	 */
	public int getDest() {
		return dest;
	}

	/**
	 * 
	 * @return maximum delay time (second)
	 */
	public int getMaxDelay() {
		return maxDelay;
	}

	/**
	 * 
	 * @return time the packet is sent
	 */
	public Date getSentTime() {
		return sentTime;
	}
}
