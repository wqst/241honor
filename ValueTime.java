import java.io.Serializable;
import java.util.Date;
/*
 * The value and update time pair
 */
public class ValueTime implements Serializable{
	private static final long serialVersionUID = 1L;
	private int value;
	private Date time;
	
	/**
	 * Constructor 
	 * @param value
	 * @param time Update time
	 */
	public ValueTime(int value, Date time){
		this.value = value;
		this.time = time;
	}
	
	/**
	 * Constructor. Update time is set to current time
	 * @param value
	 */
	public ValueTime(int value){
		this.value = value;
		this.time = new Date();
	}
	
	/**
	 * 
	 * @return value
	 */
	public int getValue(){
		return value;
	}
	
	/**
	 * 
	 * @return update time
	 */
	public Date getTime(){
		return time;
	}
}
