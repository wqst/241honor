import java.net.*;
import java.io.*;

class ClientThread extends Thread{
	private Socket socket;
	private BufferedReader br1 = null, br2 = null;
    private PrintWriter out = null;
	private Thread t1, t2;
	String str1 = "", str2 = "";
	String name = "";

	public ClientThread(String hostName, int port) {
		try {
			t1 = new Thread(this);
			t2 = new Thread(this);
			socket = new Socket(hostName, port);
			System.out.println("client: connecting to " + hostName);
			name = hostName;
			t1.start();
			t2.start();
		} catch (Exception e){
		}
	}

	public void run(){
		try {
			if (Thread.currentThread() == t2) {
				do {
					br1 = new BufferedReader(new InputStreamReader(System.in));
					out = new PrintWriter(socket.getOutputStream(), true);
					//System.out.println("Enter String:");
					str1 = br1.readLine();
					out.println(str1);
				} while (!str1.equals("END"));
			}
			else {
				do {
					br2 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					str2 = br2.readLine();
					System.out.println("Received a message of length " + str2.length() + " from Server: " + str2);
				} while (!str2.equals("END"));
			}
		}
		catch (UnknownHostException e) {
            System.err.println("Don't know about host " + name);
            System.exit(1);
        } 
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                name);
            System.exit(1);
        } 
        finally {
        	try {
        		socket.close();
        	}
        	catch (IOException e) {
        		System.out.println("Close failure");
        	}
        }
	}
}

public class client {

	public static void main(String[] args) {
		if (args.length != 2) {
            System.err.println(
                "Usage: java FileClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        new ClientThread(hostName, portNumber);
	}
	
}

/*next step:
1. set chat between server and client
2. several clients connect to the server
*/