package io.xol.engine.base;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

//(c) 2014 XolioWare Interactive

public class TexturesHandler {

	static Map<String, Integer> loadedTextures = new HashMap<String, Integer>();

	static String alreadyBound = "";
	
	public static void bindTexture(String name) {
		if(alreadyBound.equals(name))
			return;
		
		
		if (loadedTextures.containsKey(name)) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, loadedTextures.get(name));
			// loadedTextures.get(name).bind();
		} else {
			File file = new File(name);
			if (file.exists()) {
				try {
					Texture tex = TextureLoader.getTexture("PNG",
							new FileInputStream(new File(name)));//ResourceLoader.getResourceAsStream(name));
					//tex.bind();
					loadedTextures.put(name, tex.getTextureID());
					//tex.release();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Something went wrong ! File " + name
						+ " does not exist !");
			}
		}
		//alreadyBound = name;
	}
	
	public static void freeTexture(String name)
	{
		name = "./res/textures/"+name+".png";
		//System.out.println("Asking for deletion of "+name);
		if(loadedTextures.containsKey(name))
		{
			GL11.glDeleteTextures(loadedTextures.get(name));
			loadedTextures.remove(name);
			//System.out.println(name+"deleted");
		}
	}
}
