import java.net.*;
import java.io.*;
public class client {

	public static void main(String[] args) {
		if (args.length != 2) {
            System.err.println(
                "Usage: java FileClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        

        try 
        {
        	socket = new Socket(hostName, portNumber);
        	System.out.println("client: connecting to " + hostName);
        	
			in = new BufferedReader( new InputStreamReader(
			    socket.getInputStream()));
			out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(
				socket.getOutputStream())), true);
			
			while (true) {
			String msg = in.readLine();
			String length = null, content = null;
			
			if (msg == null || msg == "")
				continue;
			if (msg.equals("LENGTH"))
				length = in.readLine();
			else {
				System.out.println("Unknown message format.");
				out.println("END");
			}
			int len = Integer.parseInt(length);

			msg = in.readLine();
			if (msg.equals("CONTENT")) {

				StringBuilder sb = new StringBuilder();
		        String line = in.readLine();

		        while (!line.equals("END")) {
					sb.append(line);
					sb.append(System.getProperty("line.separator"));
					line = in.readLine();
		        }
				content = sb.deleteCharAt(sb.length()-1).toString(); //delete the last empty line
			}
			else {
				System.out.println("Unknown message format.");
				out.println("END");
			}
			
			System.out.println("client: received " + len + " bytes" + "\n");
			System.out.print(content);
			out.println("END");
		}
			//}
	    }
	    catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } 
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
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

/*next step:
1. set chat between server and client
2. several clients connect to the server
*/