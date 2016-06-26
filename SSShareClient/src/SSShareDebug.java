import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Calendar;

public class SSShareDebug {
	private static Object lock = new Object();
	private static boolean clear = true;
	
	public static void Log(String msg)
	{
		if(clear)
		{
			try {
				PrintWriter writer = new PrintWriter("log.txt", "UTF-8");
				writer.println("");
				writer.close();
				clear = false;
			} catch (Exception e) {
				System.out.println(Calendar.getInstance().getTime() + " - " + "FAILED TO SAVE TO FILE!");
				e.printStackTrace();
			}
		}
			
		String s = Calendar.getInstance().getTime() + " - " + msg;
		System.out.println(s);
		LogFile(s);
	}
	
	private static void LogFile(String msg)
	{
		synchronized(lock)
		{
			try
			{
				PrintWriter writer = new PrintWriter(new FileOutputStream(new File("log.txt"), true));
				writer.println(msg);
				writer.close();
			}
			catch (Exception e) {
				System.out.println(Calendar.getInstance().getTime() + " - " + "FAILED TO SAVE TO FILE!");
				e.printStackTrace();
			}
		}
	}
}
