import java.io.*;
import java.net.*;

class ServerThread extends Thread {
	private Socket socket;
	private BufferedReader br1, br2;
	private PrintWriter out;
	private int connection;
	private String str1 = "", str2 = "";
	private Thread t1, t2;

	public ServerThread(Socket s, int c) throws IOException {
		t1 = new Thread(this);
		t2 = new Thread(this);
		socket = s;
		connection = c;
		System.out.println("Client " + connection + " connected");
		t1.start();
		t2.start(); 
	}

	public void run() {
		try {
			if (Thread.currentThread() == t1) {
				do {
					br1 = new BufferedReader(new InputStreamReader(System.in));
					out = new PrintWriter(socket.getOutputStream(), true);
					System.out.println("Enter String:");
					str1 = br1.readLine();
					out.println(str1);
				} while (!str1.equals("END"));
			}
			else {
				do {
					br2 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					str2 = br2.readLine();
					System.out.println("Received a message of length " + str2.length() + " from Client: " + str2);
				} while (!str2.equals("END"));
			}
		}
		catch(IOException e) {
			System.err.println("IO Exception");
		} 
		finally {
			try {
				socket.close();
			} catch(IOException e) {
				System.err.println("Socket not closed");
			}
		}
	}
}

public class server {
	public static void main(String[] args) throws IOException{
		if (args.length != 1) {
            System.err.println(
                "Usage: java FileServer <port number>");
            System.exit(1);
        }

		int port = Integer.parseInt(args[0]);
		ServerSocket s = new ServerSocket(port);
		String serverPort = s.getLocalPort()+"\n";
		System.out.println("Server Started at Port: " + serverPort);

		int connection = 0;
		try {
			while (true) {
				Socket socket = s.accept();
				connection++;
				
				try {
					new ServerThread (socket, connection);
				} catch(IOException e) {
					socket.close();
				} 
				
			} 
		}
		finally {
			s.close();
		}
	}
}
/*
design my own UDP/TCP
not stop by END(read the entire )
p2p mapreduce nodejs(event loop) flask
*/