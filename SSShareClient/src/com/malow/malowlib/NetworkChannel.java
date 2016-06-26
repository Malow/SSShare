package com.malow.malowlib;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkChannel extends Process
{
	
	private Socket sock = null;
	private ObjectOutputStream outStream;
	private ObjectInputStream inStream;
	private Process notifier = null;	
	private static long nextCID = 0;
	private long id;
	
	public NetworkChannel(Socket socket)
	{
		this.id = NetworkChannel.nextCID;
		NetworkChannel.nextCID++;
		
		this.sock = socket;
		try
        {
			this.outStream = new ObjectOutputStream(this.sock.getOutputStream());
			this.inStream = new ObjectInputStream(this.sock.getInputStream());
		}
        catch (Exception e)
        {
			this.Close();
			System.out.println("Error creating in and out streams for socket. Channel: " + this.id);
		}
	}
	
	public NetworkChannel(String ip, int port)
	{
		this.id = NetworkChannel.nextCID;
		NetworkChannel.nextCID++;
		
		try
        {
			this.sock = new Socket(ip, port);
			this.outStream = new ObjectOutputStream(this.sock.getOutputStream());
			this.inStream = new ObjectInputStream(this.sock.getInputStream());
		}
        catch (Exception e)
        {
			this.Close();
			System.out.println("Error creating socket: " + ip + ":" + port + ". Channel: " + this.id);
		}
	}
		
	public void SendData(String msg)
	{
		try
        {
			this.outStream.writeObject(msg);
        }
		catch (IOException e1)
        {
            this.Close();
            System.out.println("Error sending data. Channel: " + this.id);
        }
	}

	public void Life()
	{
		while(this.stayAlive)
		{
			String msg = this.ReceiveData();
			if(msg != "")
			{
				if(this.notifier != null && this.stayAlive)
				{
					NetworkPacket np = new NetworkPacket(msg, this.id);
					this.notifier.PutEvent(np);
				}
			}
		}
	}
		
	public void SetNotifier(Process notifier) 
	{ 
		this.notifier = notifier; 
	}

	public long GetChannelID() 
	{ 
		return this.id; 
	}
	
	
	public void CloseSpecific()
	{
        if(this.sock == null)
            return;

		try { this.sock.shutdownInput(); } 
		catch (IOException e1) { System.out.println("Error trying to perform shutdown on socket from a ->Close() call. Channel: " + this.id); }
		try { this.sock.shutdownOutput(); } 
		catch (IOException e1) { System.out.println("Error trying to perform shutdown on socket from a ->Close() call. Channel: " + this.id); }
		
		try { this.sock.close();} 
		catch (IOException e) { System.out.println("Failed to close socket in channel: " + this.id); }
	}
	
	private String ReceiveData()
	{
		String msg = "";
		try {
			msg = (String) this.inStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
            this.Close();
            System.out.println("Error receiving data. Channel: " + this.id);
		}

		return msg;
	}
}





























