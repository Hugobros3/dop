package io.xol.dop.game.client.renderer;

//(c) 2014 XolioWare Interactive

import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.lwjgl.opengl.GL11;

public class TileRenderer {
	
	static int texId = -1;
	
	static int texW = 16;
	
	static int w = 16;
	static int h = 16;
	
	static Map<String,Integer> texMap = new  HashMap<String,Integer>();
	
	public static float[] oldColor = new float[3];
	public static float[] tileColor = new float[3];
	
	public static void generateTexture()
	{
		File tex = new File("./res/textures/cache/tiles_temp.png");
		if(tex.exists())
			tex.delete();
		System.out.print("Generating tiles texture map...");
		try{
		//making texture
		BufferedImage texture = new BufferedImage(texW*w,texW*h,Transparency.TRANSLUCENT);
		//
		int count = 0;
		File folder = new File("./res/textures/tiles");
		for(File f : folder.listFiles())
		{
			if(f.isFile()){
				//System.out.println("omg :"+f.getName().replaceAll(".png", ""));
				BufferedImage pasteme = ImageIO.read(f);
				if(texW != pasteme.getWidth())
					System.out.println("WARNING : TextureWidth in codebase and in picture are different ! Weird things might happen !");
				
				for(int x = 0;x < texW;x++)
				{
					for(int y = 0;y < texW;y++)
					{
						texture.setRGB((count%w)*16+x, (count/h)*16+y, pasteme.getRGB(x, y));
					}
				}
				texMap.put(f.getName().replaceAll(".png", ""), count);
				count++;
			}
		}
		ImageIO.write(texture, "PNG", tex);
		System.out.print(" done ! "+count+" sub-images merged into the file. \n");
		}
		catch(Exception e)
		{
			System.out.println("Something went wrong ! File could not be created.");
			e.printStackTrace();
		}
	}
	
	public static void loadTexture()
	{
		System.out.println("Loading tiles texture map ...");
		try{
		FileInputStream in = new FileInputStream(new File("./res/textures/cache/tiles_temp.png"));
		Texture tex = TextureLoader.getTexture("PNG",in);
		texId = tex.getTextureID();
		System.out.println(" done !");
		}
		catch(Exception e)
		{
			System.out.println("Something went wrong ! File ./res/textures/cache/tiles_temp.png does not exist or is corrupted !");
		}
	}
	
	public static void bindTexture()
	{
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,GL11.GL_NEAREST);
		
	}

	public static void renderTileWithName(float xpos, float ypos, float w, float h,  String tex) {
		try{
		renderTileWithId(xpos,ypos,w,h,texMap.get(tex));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println(tex);
		}
	}

	public static void renderTileWithNameAndColor(float xpos, float ypos,float w, float h, String tex, float[] color) {
		try{
			renderTileWithIdAndColor(xpos,ypos,w,h,texMap.get(tex+"_c"), color);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println(tex);
		}
	}
	
	public static void renderTileWithId(float xpos, float ypos,
			float tw, float th,  int id) {
		//GL11.glColor3f(1f, 1f, 1.0f);
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,GL11.GL_NEAREST);
		//GL11.glEnable(GL11.GL_TEXTURE_2D);
		//GL11.glPushMatrix();
		// draw quad
		//GL11.glTranslated(xpos, ypos, 0);
		//GL11.glBegin(GL11.GL_TRIANGLES);
		if(tileColor != oldColor)
		{
			GL11.glColor3f(tileColor[0],tileColor[1],tileColor[2]);
		}
		oldColor = tileColor;
		
		GL11.glTexCoord2d((id%w)/(1.0*texW), ((id)/h+1)/(1.0*texW));
		GL11.glVertex2f(0+xpos, 0+ypos);
		GL11.glTexCoord2d((id%w+1)/(1.0*texW), ((id)/h+1)/(1.0*texW));
		GL11.glVertex2f(tw+xpos, 0+ypos);
		GL11.glTexCoord2d((id%w)/(1.0*texW), (id/h)/(1.0*texW));
		GL11.glVertex2f(0+xpos, th+ypos);

		GL11.glTexCoord2d((id%w+1)/(1.0*texW), ((id)/h+1)/(1.0*texW));
		GL11.glVertex2f(tw+xpos, 0+ypos);
		GL11.glTexCoord2d((id%w)/(1.0*texW), (id/h)/(1.0*texW));
		GL11.glVertex2f(0+xpos, th+ypos);
		GL11.glTexCoord2d((id%w+1)/(1.0*texW), (id/h)/(1.0*texW));
		GL11.glVertex2f(tw+xpos, th+ypos);
		
		// Translate-needing code
		
		/*GL11.glTexCoord2d((id%w)/(1.0*texW), ((id)/h+1)/(1.0*texW));
		GL11.glVertex2f(0, 0);
		GL11.glTexCoord2d((id%w+1)/(1.0*texW), ((id)/h+1)/(1.0*texW));
		GL11.glVertex2f(tw, 0);
		GL11.glTexCoord2d((id%w)/(1.0*texW), (id/h)/(1.0*texW));
		GL11.glVertex2f(0, th);

		GL11.glTexCoord2d((id%w+1)/(1.0*texW), ((id)/h+1)/(1.0*texW));
		GL11.glVertex2f(tw, 0);
		GL11.glTexCoord2d((id%w)/(1.0*texW), (id/h)/(1.0*texW));
		GL11.glVertex2f(0, th);
		GL11.glTexCoord2d((id%w+1)/(1.0*texW), (id/h)/(1.0*texW));
		GL11.glVertex2f(tw, th);*/
		
		//GL11.glEnd();
		//GL11.glTranslated(-xpos, -ypos, 0);
		//GL11.glPopMatrix();
		WorldRenderer.rectCount++;
	}
	
	public static void renderTileWithIdAndColor(float xpos, float ypos,
			float tw, float th,  int id, float[] color) {
		GL11.glColor3f(color[0],color[1],color[2]);
		
		GL11.glTexCoord2d((id%w)/(1.0*texW), ((id)/h+1)/(1.0*texW));
		GL11.glVertex2f(0+xpos, 0+ypos);
		GL11.glTexCoord2d((id%w+1)/(1.0*texW), ((id)/h+1)/(1.0*texW));
		GL11.glVertex2f(tw+xpos, 0+ypos);
		GL11.glTexCoord2d((id%w)/(1.0*texW), (id/h)/(1.0*texW));
		GL11.glVertex2f(0+xpos, th+ypos);

		GL11.glTexCoord2d((id%w+1)/(1.0*texW), ((id)/h+1)/(1.0*texW));
		GL11.glVertex2f(tw+xpos, 0+ypos);
		GL11.glTexCoord2d((id%w)/(1.0*texW), (id/h)/(1.0*texW));
		GL11.glVertex2f(0+xpos, th+ypos);
		GL11.glTexCoord2d((id%w+1)/(1.0*texW), (id/h)/(1.0*texW));
		GL11.glVertex2f(tw+xpos, th+ypos);
		
		GL11.glColor3f(1f, 1f, 1f);
		WorldRenderer.rectCount++;
	}
}
