import java.util.UUID;



public class SSShareClient 
{
	private final static String IP = "malow.mooo.com";
	private final static int port = 9255;
	
	public static long lastTime = System.nanoTime();
	private static float GetDiff()
	{
		long currentTime = System.nanoTime();
		float diff = (currentTime - lastTime) / 1000000000.0f;
		lastTime = currentTime;
		return diff;
	}	
	

	public static void main(String[] args) 
	{
		MaloWFrame frame = new MaloWFrame();
		
		SSFolder ssFolder = new SSFolder();
		
		ServerConnection server = new ServerConnection(IP, port);
		server.Start();
		
		float diffAccu = 0.0f;
		float pingDiffAccu = 0.0f;
		
		while(true)
		{
			float diff = GetDiff();
			diffAccu += diff;
			pingDiffAccu += diff;
			
			if(diffAccu > 1.0f)
			{
				frame.ResizeImageIfNeeded();
				server.ReconnectIfNeeded();
				diffAccu = 0.0f;
			}
			
			if(pingDiffAccu > 30.0f)
			{
				server.CheckPing();
				pingDiffAccu = 0.0f;
			}
			
			String file = ssFolder.GetNewFilePath();
			if(file != "")
			{
				frame.DisplayImage(file);
				server.SendFile(file, "DownloadedImages\\" + UUID.randomUUID().toString() + ".jpg");
			}
			
			String networkImg = server.GetNewImage();
			if(networkImg != "")
			{
				frame.DisplayImage(networkImg);
			}
			
			try 
			{
				Thread.sleep(100);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
		/* Unreachable since it's closed by pressing the X
		server.Close();
		server.WaitUntillDone();
		server = null;
		*/
	}
}