
public class SSShareServer {
	public static void main(String [] args)
	{		
		SSShareDebug.Log("SSShareServer started");
		
		int port = 9255;
		
		Server server = new Server();
		ConnectionListener cl = new ConnectionListener(port, server);

		server.Start();
		cl.Start();
		
		cl.WaitUntillDone();
		server.WaitUntillDone();
		cl = null;
		server = null;
	}
}
