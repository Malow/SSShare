import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import org.apache.commons.codec.binary.Base64;

import com.malow.malowlib.NetworkChannel;
import com.malow.malowlib.NetworkPacket;
import com.malow.malowlib.Process;
import com.malow.malowlib.ProcessEvent;


public class ServerConnection extends Process
{
	String ip;
	int port;
	private NetworkChannel nc;
	private long pingTime;
	private boolean isWaitingForPing;
	
	private LinkedList<String> newFiles = new LinkedList<String>();
	
	public ServerConnection(String ip, int port)
	{
		this.ip = ip;
		this.port = port;
		this.nc = new NetworkChannel(ip, port);
		this.nc.SetNotifier(this);
		this.nc.Start();
		
		this.pingTime = System.currentTimeMillis();
		this.isWaitingForPing = false;
	}
	
	public void ReconnectIfNeeded()
	{
		if(this.nc.GetState() != Process.RUNNING)
		{
			SSShareDebug.Log("Server connection is dead, reconnecting.");
			this.nc = new NetworkChannel(this.ip, this.port);
			this.nc.SetNotifier(this);
			this.nc.Start();
			SSShareDebug.Log("Connected to Server.");
		}
	}

	public void Life() 
	{
		while(this.stayAlive)
		{
			ProcessEvent ev = this.WaitEvent();
			if(ev instanceof NetworkPacket)
			{
				String msg = ((NetworkPacket) ev).GetMessage();
				if(msg.equals("PING"))
				{
					if(this.isWaitingForPing)
					{
						SSShareDebug.Log("Ping is " + (System.currentTimeMillis() - this.pingTime) + " ms.");
						this.isWaitingForPing = false;
						this.nc.SendData("PING");
					}
					else
					{
						SSShareDebug.Log("Unknown ping message from server.");
					}
				}
				else
				{
					ImagePacket imgPack = ImagePacket.toModel(msg);
					if(imgPack != null)
					{
						SSShareDebug.Log("Image received");
						this.CreateImage(imgPack.uniqueFileName, imgPack.dataString);
					}
					else
					{
						SSShareDebug.Log("Msg received from server: " + msg);
					}
				}
			}
		}
	}
	
	public void CloseSpecific()
	{
		this.nc.Close();
		this.nc.WaitUntillDone();
		this.nc = null;
	}
	

	public void CheckPing()
	{
		SSShareDebug.Log("Checking ping.");
		this.nc.SendData("PING");
		this.isWaitingForPing = true;
		this.pingTime = System.currentTimeMillis();
	}
	
	public void SendFile(String path, String uniqueFileName)
	{
        String totalString = "";
		try {
			File file = new File(path);
	        FileInputStream imageInFile = new FileInputStream(file);
	        byte imageData[] = new byte[(int) file.length()];
	        imageInFile.read(imageData);
	        
	        totalString = Base64.encodeBase64URLSafeString(imageData);
	        SSShareDebug.Log("Image successfully encoded!");
	        imageInFile.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
        
		this.nc.SendData(new ImagePacket(totalString, uniqueFileName).toNetworkString());
		SSShareDebug.Log("Image sent!");
	}
	
	private void CreateImage(String uniqueFileName, String totString)
	{
		try {
			byte[] imageByteArray = Base64.decodeBase64(totString);
			FileOutputStream imageOutFile = new FileOutputStream(uniqueFileName);
			imageOutFile.write(imageByteArray);
			SSShareDebug.Log("Image sucessfully created on disk");
			imageOutFile.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		synchronized(this.newFiles) 
		{
			this.newFiles.add(uniqueFileName);
		}
	}
	
	public String GetNewImage()
	{
		synchronized(this.newFiles) 
		{
			if(this.newFiles.size() > 0)
				return this.newFiles.pop();
			else
				return "";
		}
	}
}