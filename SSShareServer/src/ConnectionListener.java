

import com.malow.malowlib.NetworkChannel;
import com.malow.malowlib.NetworkServer;

public class ConnectionListener extends NetworkServer
{
	private Server server = null;
	
	public ConnectionListener(int port, Server server)
	{
		super(port);
		this.server = server;
	}
	
	public void ClientConnected(NetworkChannel cc)
	{
		this.server.clientConnected(cc);
	}
}