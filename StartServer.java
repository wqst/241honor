import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class StartServer {
	public static final int centralServerID = 0;

	public static void main(String[] args) {

		// Check input arguments
		if (args.length != 2) {
			System.out.println("Usage: java StartServer [Index(1~4)] [Config File]");
			System.exit(1);
		}
		String file = args[1];
		int index = Integer.valueOf(args[0]);

		ServerNode node = new ServerNode(index);
		try {
			// Read configuration file
			BufferedReader r = new BufferedReader(new FileReader(file));
			String nextLine;
			while ((nextLine = r.readLine()) != null) {
				String[] s = nextLine.split(" = ");
				switch (s[0]) {
				case "TotalNodes":
					node.totalNodes = Integer.valueOf(s[1]);
					break;

				case "ServerPorts":
					node.serverPorts = new int[node.totalNodes+1];
					String[] portString = s[1].split(", ");
					for (int i = 0; i < (node.totalNodes+1); i++) {
						node.serverPorts[i] = Integer.valueOf(portString[i]);
					}
					break;

				case "IPAddresses":
					node.ip = s[1].split(", ");
					break;

				case "MaxDelay":
					node.maxDelay = Integer.valueOf(s[1]);
					break;

				default:
					System.out.println("Unrecognized configuration: " + s);
				}
			}
			r.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		node.startThreads();
	}

}
