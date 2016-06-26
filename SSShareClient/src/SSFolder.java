import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;


public class SSFolder {
	private String ssFolder = "";
	private File folder;
	private ArrayList<File> listOfFiles;
	
	public SSFolder()
	{
		this.GetSSFolderFromConfig();
		this.folder = new File(this.ssFolder);
		this.LoadExistingFiles();
	}
		
	private void GetSSFolderFromConfig()
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader("config.txt"));
			ssFolder = br.readLine();
			SSShareDebug.Log("ssFolder located: " + this.ssFolder);
			br.close();
	    } catch (Exception e) {
	    	SSShareDebug.Log("FAILED TO LOAD SSFOLDER FROM CONFIG-FILE!");
			e.printStackTrace();
		}		
	}
	
	private void LoadExistingFiles()
	{
		this.listOfFiles = new ArrayList<File>();
		File[] newFiles = this.folder.listFiles();
		for(int u = 0; u < newFiles.length; u++)
		{
			this.listOfFiles.add(newFiles[u]);
		}
	}
	
	public String GetNewFilePath()
	{
		File[] newFiles = this.folder.listFiles();
		if(newFiles.length == this.listOfFiles.size())
			return "";
		
		if(newFiles.length < this.listOfFiles.size())
		{
			this.LoadExistingFiles();
			return "";
		}
		
		File newFile = null;

		for(int u = 0; u < newFiles.length; u++)
		{
			if(!this.listOfFiles.contains(newFiles[u]))
			{
				newFile = newFiles[u];
				u = newFiles.length + 1;
			}
		}
		
		this.listOfFiles.add(newFile);
		
		SSShareDebug.Log("New file detected: " + newFile.getPath());
		
		return newFile.getPath();
	}
}
