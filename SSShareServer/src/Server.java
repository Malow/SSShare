import java.util.ArrayList;
import java.util.List;

import com.malow.malowlib.NetworkChannel;
import com.malow.malowlib.NetworkPacket;
import com.malow.malowlib.Process;
import com.malow.malowlib.ProcessEvent;


public class Server extends Process
{
	public class Client
	{
		public NetworkChannel nc;
		public long pingTime;
		public boolean isWaitingForPing;
		
		public Client(NetworkChannel nc)
		{
			this.nc = nc;
			this.pingTime = System.currentTimeMillis();
			this.isWaitingForPing = false;
		}
	}
	private List<Client> ccs;
	
	public Server()
	{
		this.ccs = new ArrayList<Client>();
	}
	
	public void CloseSpecific()
	{
		for(int i = 0; i < this.ccs.size(); i++)
		{
			this.ccs.get(i).nc.Close();
		}
		for(int i = 0; i < this.ccs.size(); i++)
		{
			this.ccs.get(i).nc.WaitUntillDone();
		}
		this.ccs = null;
	}
	
	public void clientConnected(NetworkChannel cc)
	{
		synchronized(this.ccs)
		{
			this.ccs.add(new Client(cc));
		}
		cc.SetNotifier(this);
		cc.Start();
		cc.SendData("Hello, this is server. Your ClientID is: " + cc.GetChannelID());
		SSShareDebug.Log("Client " + cc.GetChannelID() + " connected.");
	}
	
	@Override
	public void Life() 
	{
		while(this.stayAlive)
		{
			ProcessEvent ev = this.WaitEvent();
			
			synchronized(this.ccs)
			{
				List<Client> deadCCs = new ArrayList<Client>();
				for(Client cc : this.ccs)
				{
					if(cc.nc.GetState() != Process.RUNNING)
					{
						deadCCs.add(cc);
						SSShareDebug.Log("Client " + cc.nc.GetChannelID() + " dead, removing it.");
					}
				}
				for(Client cc : deadCCs)
				{
					this.ccs.remove(cc);
				}
			}
			
			
			if(ev instanceof NetworkPacket)
			{
				final String msg = ((NetworkPacket) ev).GetMessage();
				long senderId = ((NetworkPacket) ev).GetSenderID();
				if(msg.equals("PING"))
				{
					Client c = this.getClientWithId(senderId);
					if(c.isWaitingForPing)
					{
						SSShareDebug.Log("Ping response received from client " + senderId + ". Ping: " + (System.currentTimeMillis() - c.pingTime));
						c.isWaitingForPing = false;
					}
					else
					{
						SSShareDebug.Log("Ping request received from client " + senderId);
						c.isWaitingForPing = true;
						c.pingTime = System.currentTimeMillis();
						c.nc.SendData("PING");
					}
				}
				else
				{
					SSShareDebug.Log("Received image packet from client " + senderId + ", sending it out.");
					synchronized(this.ccs)
					{
						for(Client cc : this.ccs)
						{
							if(cc.nc.GetChannelID() != senderId)
							{ 
								final Client client = cc;
								new Thread( new Runnable() {
								    @Override
								    public void run() {
								    	SSShareDebug.Log("Starting to send image to client " + client.nc.GetChannelID());
								    	client.nc.SendData(msg);
								    	SSShareDebug.Log("Image finished sending to client " + client.nc.GetChannelID());
								    }
								}).start();
							}
						}
					}
				}
			}
		}
	}
	
	public Client getClientWithId(long id)
	{
		for(Client c: this.ccs)
		{
			if(c.nc.GetChannelID() == id)
			{
				return c;
			}
		}
		SSShareDebug.Log("CRITICAL ERROR, CANNOT FIND CLIENT AT WorldServer getClientWithId: " + id);
		SSShareDebug.Log("this.ccs.size: " + this.ccs.size());
		return null;
	}
}
