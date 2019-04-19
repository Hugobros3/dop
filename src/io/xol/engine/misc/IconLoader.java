package io.xol.engine.misc;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

//(c) 2014 XolioWare Interactive

public class IconLoader {

	public static void load()
	{
		Display.setIcon(getIconsData());
	}

	public static ByteBuffer getByteBufferData(String name) {
		File file = new File(name);
		if (file.exists()) {
			try {
				Texture tex = TextureLoader.getTexture("PNG",new FileInputStream(new File(name)));//ResourceLoader.getResourceAsStream(name));
				return ByteBuffer.wrap(tex.getTextureData());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static ByteBuffer[] getIconsData() {
		
		ByteBuffer[] returnme;
		if(OSHelper.isWindows())
		{
			returnme =  new ByteBuffer[2];
			returnme[0]= getByteBufferData("./res/textures/icon16.png");
			returnme[1]= getByteBufferData("./res/textures/icon.png");
		}
		else
		{
			returnme =  new ByteBuffer[1];
			returnme[0]= getByteBufferData("./res/textures/icon.png");
		}
		return returnme;
	}
}
