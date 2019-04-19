package io.xol.engine.base;

import io.xol.dop.game.client.Client;
import io.xol.dop.game.client.scenes.IntroScene;
import io.xol.dop.game.common.VersionInfo;
import io.xol.engine.scene.Scene;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

//(c) 2014 XolioWare Interactive

public class XolioWindow {
	
	Scene currentScene = null;
	
	public static int frameW = 800;
	public static int frameH = 608;
	public static boolean resized = false;
	public static boolean forceResize = false;
	
	public static int targetFPS = 60;

	public static String engineVersion = "1.0a - indev";
	
	static boolean closeRequest = false;
	
	static String[] modes;
	
	public XolioWindow(String name, int fw, int fh) {
		try {
			if(fw != -1)
				frameW = fw;
			if(fh != -1)
				frameH = fh;
			System.out.println("Initializing XolioEngine v"+engineVersion+" [Game:"+name+",Width:"+frameW+",Height:"+frameH+"]");
			
			computeDisplayModes();
			
			targetFPS = Client.getConfig().getIntProp("targetFPS", "60");
			
			Display.setDisplayMode(new DisplayMode(frameW, frameH));
			Display.setTitle(name+" | "+VersionInfo.devPhase + " " + VersionInfo.version);

			Display.setResizable(true);
			Display.create();
			
			glInfo();
			
			switchResolution();
		} catch (Exception e) {
			System.out
					.println("A Fatal error occured ! If you see the dev, show him this message !");
			e.printStackTrace();
		}
	}
	
	private void glInfo() {
		// Will print some debug information on the openGL context
		System.out.println("Render device : "+GL11.glGetString(GL11.GL_RENDERER ) + " made by " + GL11.glGetString(GL11.GL_VENDOR) + " driver version " + GL11.glGetString(GL11.GL_VERSION));
	}

	public void run(){
		try {
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0, frameW, 0, frameH, 1, -1);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);

			currentScene = new IntroScene(this);
			
			Keyboard.enableRepeatEvents(true);
			
			Client.onStart();
			while (!Display.isCloseRequested() && !closeRequest) {
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT
						| GL11.GL_DEPTH_BUFFER_BIT);

				if(resized)
					resized = false;
				if(Display.wasResized() || forceResize)
				{
					if(forceResize)
						forceResize = false;
			       XolioWindow.frameW = Display.getWidth();
			       XolioWindow.frameH = Display.getHeight();
			       
			       GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
			       GL11.glLoadIdentity();
			       GL11.glMatrixMode(GL11.GL_PROJECTION);
			       GL11.glLoadIdentity();
			       GL11.glOrtho(0.0f, Display.getWidth(), 0.0f, Display.getHeight(), 1.0f, -1.0f);
			       GL11.glMatrixMode(GL11.GL_MODELVIEW);
			       GL11.glLoadIdentity();
			       if (currentScene != null)
			       	currentScene.resized =true;
			       resized = true;
				}
				
				if (currentScene != null)
				{
					//update inputs first
					InputAbstractor.update(this);
					//then do the game logic
					currentScene.update();
				}

				Display.sync(targetFPS);

				Display.update();
			}
			System.out.println("Copyright 2014 Xol.io");
			Client.onClose();
			Display.destroy();
		} catch (Exception e) {
			System.out
					.println("A Fatal error occured ! If you see the dev, show him this message !");
			e.printStackTrace();
		}
	}

	public static void computeDisplayModes() {
		try{
			DisplayMode[] dms = Display.getAvailableDisplayModes();
			modes = new String[dms.length];
			for(int i = 0; i < dms.length ; i++)
			{
				modes[i] = dms[i].getWidth()+"x"+dms[i].getHeight();
			}
			System.out.println(modes.length+" display modes avaible.");
		}
		catch(Exception e)
		{
			
		}
	}
	
	public static String[] getDisplayModes() {
		return modes;
	}
	
	public void switchResolution() {
		try{
			if(Client.getConfig().getBooleanProp("fullScreen", false))
			{
				String str[] = Client.getConfig().getProp("fullScreenResolution","800x600").split("x");
				int w = Integer.parseInt(str[0]);
				int h = Integer.parseInt(str[1]);
				
				DisplayMode displayMode = null;
		        DisplayMode[] modes = Display.getAvailableDisplayModes();

		         for (int i = 0; i < modes.length; i++)
		         {
		             if (modes[i].getWidth() == w
		             && modes[i].getHeight() == h
		             && modes[i].isFullscreenCapable())
		               {
		                    displayMode = modes[i];
		               }
		         }
				
				Display.setDisplayMode(displayMode);
				Display.setFullscreen(true);
				XolioWindow.forceResize = true;
			}
			else
			{
				Display.setFullscreen(false);
				XolioWindow.forceResize = true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static long lastTimeMS = 0;
	private static int framesSinceLS = 0;
	private static int lastFPS = 0;

	public static void tick() {
		framesSinceLS++;
		if (lastTimeMS + 1000 < System.currentTimeMillis()) {
			lastFPS = framesSinceLS;
			lastTimeMS = System.currentTimeMillis();
			framesSinceLS = 0;
		}
	}

	public static int getFPS() {
		return lastFPS;
	}

	public void changeScene(Scene s) {
		currentScene = s;
	}
	
	public void close()
	{
		closeRequest = true;
	}

	public Scene getCurrentScene() {
		return currentScene;
	}

	public void handleSpecialKey(int k) {
		if(k == 61)
		{
			frameW = 800;
			frameH = 608;
			try {
				Display.setDisplayMode(new DisplayMode(frameW, frameH));
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
		}
	}
}
