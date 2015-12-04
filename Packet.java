import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
/*
 * Packet containing the message
 */
public class Packet implements Serializable {
	
	/*
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int source, dest, maxDelay, key, model;
	private ValueTime vt;
	private String op, message;
	private boolean isAck, fromClient;
	private Date sentTime;
	private ObjectOutputStream returnChannel;
	private boolean[] hasKey;

	/**
	 * Generate new packet at client
	 * 
	 * @param source Source index
	 * @param dest Destination index
	 * @param maxDelay Maximum delay time (second)
	 * @param op Operation
	 * @param key
	 * @param value
	 * @param model
	 */
	public Packet(int source, int dest, int maxDelay, String op, int key,
			int value, int model, String message) {
		this.source = source;
		this.dest = dest;
		this.maxDelay = maxDelay;
		this.op = op;
		this.key = key;
		this.model = model;
		this.isAck = false;
		this.sentTime = new Date();
		this.fromClient = true;
		this.message = message;
		if (op.equals("insert") || op.equals("update"))
			this.vt = new ValueTime(value);
		else
			this.vt = null;
	}

	/**
	 * Generate new packet at server
	 * 
	 * @param source Source index
	 * @param dest Destination index
	 * @param p Old packet 
	 * @param ack Whether this packet is an acknowledgement
	 */
	public Packet(int source, int dest, Packet p, boolean ack) {
		this.source = source;
		this.dest = dest;
		this.maxDelay = p.getMaxDelay();
		this.op = new String(p.getOperation());
		this.key = p.getKey();
		this.vt = p.getValueTime();
		this.model = p.getModel();
		this.isAck = ack;
		this.sentTime = new Date();
		this.fromClient = false;
	}

	/**
	 * Generate packet containing search result
	 * 
	 * @param source Source index
	 * @param dest Destination index
	 * @param p Old packet
	 * @param ack Whether this packet is an acknowledgement
	 * @param hasKey Search result
	 */
	public Packet(int source, int dest, Packet p, boolean ack, boolean[] hasKey) {
		this(source, dest, p, ack);
		this.hasKey = hasKey;

	}

	/**
	 * 
	 */
	public String toString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		String s = "Source: " + source + ", Destination: " + dest
				+ ", MaxDelay: " + maxDelay + ", SentTime: "
				+ df.format(sentTime) + ", Content: ";

		switch (op) {
		case ("send"):
			s += message;
			break;
		case ("show-all"):
			s += "show-all";
			break;
		default:
			s = s + op + " " + key;

		}
		if (vt != null) {
			s += (" " + vt.getValue());
		}
		if (model != 0)
			s += (" " + model);
		if (fromClient)
			s += ", sent from client";
		return s;
	}

	/**
	 * 
	 * @return search result
	 */
	public boolean[] getSearchResult() {
		return hasKey;
	}

	/**
	 * 
	 * @return operation string
	 */
	public String getOperation() {
		return op;
	}

	/**
	 * 
	 * @return key
	 */
	public int getKey() {
		return key;
	}
	
	/**
	 * 
	 * @return value and update time
	 */
	public ValueTime getValueTime() {
		return vt;
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
	 * @return model
	 */
	public int getModel() {
		return model;
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

	/**
	 * 
	 * @return reply channel
	 */
	public ObjectOutputStream getReturnChannel() {
		return returnChannel;
	}

	/**
	 * 
	 * @return true if the packet is an acknowledgement
	 */
	public boolean isAck() {
		return isAck;
	}

	/**
	 * 
	 * @param c The reply channel
	 */
	public void setReturnChannel(ObjectOutputStream c) {
		returnChannel = c;
	}

	/**
	 * 
	 * @param vt Value and update time pair
	 */
	public void setValueTime(ValueTime vt) {
		this.vt = vt;
	}

}
