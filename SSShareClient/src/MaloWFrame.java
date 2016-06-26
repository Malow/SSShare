import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.imgscalr.Scalr;


public class MaloWFrame 
{	
	public class Panel extends JPanel 
	{
		private static final long serialVersionUID = 1L;
		public BufferedImage img;

		public Panel(BufferedImage img) 
		{
			this.img = img;
		}

		@Override
		protected void paintComponent(Graphics g) 
		{
			super.paintComponent(g);
			if (img != null) 
			{
				g.drawImage(img, 0, 0, this);
			}
		}

		@Override
		public Dimension getPreferredSize() 
		{
			if (img != null) 
			{
				return new Dimension(img.getWidth(), img.getHeight());
			}
			return super.getPreferredSize();
		}
	}

	public JFrame frame;	
	public Panel panel;
	public String curImg = "";
	
	public MaloWFrame() 
	{
		this.frame = new JFrame("MaloWScreenShotShare");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setBounds(200, 200, 400, 400);
		this.frame.setLocationRelativeTo(null);
		this.frame.setVisible(true);
		this.frame.setAutoRequestFocus(false);
	}

	public void DisplayImage(String path) 
	{
		if(path == "")
			return;
		
		this.curImg = path;
		try 
		{
			BufferedImage img = ImageIO.read(new File(path));
			img = Scalr.resize(img, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_EXACT, this.frame.getContentPane().getWidth(), this.frame.getContentPane().getHeight(), Scalr.OP_ANTIALIAS);
			this.frame.getContentPane().removeAll();
			if(this.panel != null)
			{
				this.panel.img = null;
				this.panel = null;
			}
			this.panel = new Panel(img);
			this.frame.getContentPane().add(this.panel);
			this.frame.setVisible(true);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void ResizeImageIfNeeded()
	{
		if(this.panel != null && this.panel.img != null)
		{
			if(this.panel.img.getWidth() != this.frame.getContentPane().getWidth() || this.panel.img.getHeight() != this.frame.getContentPane().getHeight())
			{
				this.DisplayImage(this.curImg);
			}
		}
	}
}
