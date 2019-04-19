package io.xol.dop.game.client.scenes;

import org.lwjgl.input.Keyboard;

import io.xol.dop.game.client.Client;
import io.xol.dop.game.client.FastConfig;
import io.xol.dop.game.common.VersionInfo;
import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.base.XolioWindow;
import io.xol.engine.base.font.BitmapFont;
import io.xol.engine.base.font.FontRenderer;
import io.xol.engine.misc.HttpRequestThread;
import io.xol.engine.misc.HttpRequester;
import io.xol.engine.scene.Scene;
//import io.xol.engine.sound.SoundManager;

//(c) 2014 XolioWare Interactive

public class MainMenuScene extends Scene implements HttpRequester{

	public MainMenuScene(XolioWindow XolioWindow,int bg) {
		super(XolioWindow);
		new HttpRequestThread(this, "version", "http://dop.xol.io/api/updater/current/version.txt", "").start();
		bgscroll = bg;
	}

	boolean pressstart = false;
	int bgscroll = 0;
	int waitForInput = 0;
	int depoping = 150;
	String[] menuitems = {"Play on last server","Select a server","Level editor","Options","Credits","Quit","Disconnect and Quit"};
	int currentSelection = 0;
	int selectionWait = 0;
	int validated = 0;
	
	boolean mayUpdate = false;
	String onlineVersion = VersionInfo.version;
	
	public void update() {
		//Dirty fix for unproper disconnects
		if(Client.connect != null)
		{
			Client.connect.close();
			Client.connect = null;
			System.out.println("Killing old connection");
		}
		bgscroll++;
		waitForInput++;
		if(bgscroll >= 256)
			bgscroll = 0;
		//bg
		ObjectRenderer.renderTexturedRect(XolioWindow.frameW / 2+bgscroll-128,XolioWindow.frameH / 2+bgscroll-128, XolioWindow.frameW*2f, XolioWindow.frameH*2f,0f, 0f, XolioWindow.frameW / 2 *2f, XolioWindow.frameH / 2 *2f, 128f,"gui/menubg");
		//logo
		ObjectRenderer.renderTexturedRect(XolioWindow.frameW / 2,XolioWindow.frameH / 1.5f, 256*(getScreenSizeMultiplier()+1),256*(getScreenSizeMultiplier()+1),"despotism");
		//ask for start
		if(!pressstart || depoping > 0)
		{
			String pstxt = "Hit "+Keyboard.getKeyName(FastConfig.keyStart)+" to start.";
			FontRenderer.drawTextUsingSpecificFont(XolioWindow.frameW / 2 - FontRenderer.getTextLengthUsingFont(16*getScreenSizeMultiplier(), pstxt, BitmapFont.EDITUNDO)/2,XolioWindow.frameH / 2 -20+(150-depoping)/3+(float)Math.sin(bgscroll/128f*3.14f*2)*4f, 0, 16*getScreenSizeMultiplier(),pstxt,BitmapFont.EDITUNDO,depoping/150f);
		
			if(mayUpdate)
			{
				String versionTXT = "A new version is avaible : "+onlineVersion;
				FontRenderer.drawTextUsingSpecificFontHex(XolioWindow.frameW / 2 - FontRenderer.getTextLengthUsingFont(16+16*getScreenSizeMultiplier(), versionTXT, BitmapFont.EDITUNDO)/2,XolioWindow.frameH / 2+50, 0,16+16*getScreenSizeMultiplier(),versionTXT,BitmapFont.EDITUNDO,"FF4040",1f);
				versionTXT = "Select \"Update\" in menu to update";
				FontRenderer.drawTextUsingSpecificFontHex(XolioWindow.frameW / 2 - FontRenderer.getTextLengthUsingFont(16*getScreenSizeMultiplier(), versionTXT, BitmapFont.EDITUNDO)/2,XolioWindow.frameH / 2+20, 0,16*getScreenSizeMultiplier(),versionTXT,BitmapFont.EDITUNDO,"FF4040",1f);
				
			}
		}
		else // or display menu !
		{
			if(depoping < 120)
			{
				// update animations
				if(selectionWait > 0)
					selectionWait -= 4;
				else if(selectionWait < 0)
					selectionWait += 4;
				else //moving in menu
				{
					if(validated == 0){
						if(Keyboard.isKeyDown(Client.clientConfig.getIntProp("DOWN", "208")) && currentSelection < menuitems.length-1)
						{
							currentSelection++;
							selectionWait = -32;
						}
						if(Keyboard.isKeyDown(FastConfig.keyUp) && currentSelection > 0)
						{
							currentSelection--;
							selectionWait = +32;
						}
					}
					else
						validated +=1;
				}
				//dislay menu
				int count = 0;
				for(String item : menuitems)
				{
					if(XolioWindow.frameH < 600)
					{
						if(Math.abs(currentSelection-count) < 6)
						{
							String menutxt = item;
							FontRenderer.drawTextUsingSpecificFont(XolioWindow.frameW / 2 - FontRenderer.getTextLengthUsingFont(16*getScreenSizeMultiplier()+16, menutxt, BitmapFont.EDITUNDO)/2,
									XolioWindow.frameH / 2f  + 12 + /*currentSelection*32*/ - count*32/* - selectionWait*/, 0, 16*getScreenSizeMultiplier()+16,menutxt,BitmapFont.EDITUNDO,
									(60-validated)/60f*(1-(float)Math.pow(Math.abs(currentSelection-count)/(menuitems.length*1f),0.55)));
						}
					}
					else
					{
						if(Math.abs(currentSelection-count) < 5)
						{
							String menutxt = item;
							int dist = 16 +  getScreenSizeMultiplier()*16;
							FontRenderer.drawTextUsingSpecificFont(XolioWindow.frameW / 2 - FontRenderer.getTextLengthUsingFont(16*getScreenSizeMultiplier()+16, menutxt, BitmapFont.EDITUNDO)/2,
									XolioWindow.frameH / 2f  - 32*4 + currentSelection*dist - count*dist + selectionWait, 0, 16*getScreenSizeMultiplier()+16,menutxt,BitmapFont.EDITUNDO,
									(60-validated)/60f*(1-(float)Math.pow(Math.abs(currentSelection-count)/(menuitems.length*1f),0.55)));
						}
					}
					
					count++;
				}
			}
		}
		//animation
		if(pressstart && depoping > 0)
				depoping -=3;
		//wait for enter
		if (waitForInput > 10 && Keyboard.isKeyDown(FastConfig.keyStart)) {
			pressstart = true;
		}
		if (Keyboard.isKeyDown(14)) {
			pressstart = false;
			depoping = 150;
		}
		String copy = "Copyright 2014 - XolioWare Interactive";
		FontRenderer.drawTextUsingSpecificFontRVBA(XolioWindow.frameW / 2 - FontRenderer.getTextLengthUsingFont(16*getScreenSizeMultiplier(), copy, BitmapFont.EDITUNDO)/2,20, 0, 16*getScreenSizeMultiplier(),copy,BitmapFont.EDITUNDO,1,0.8f,0.8f,0.8f);
		
		FontRenderer.drawTextUsingSpecificFont(2,XolioWindow.frameH-20,0, 16, "DoP Version "+VersionInfo.get(),BitmapFont.SMALLFONTS,0.5f);

		super.update();
		//while (Keyboard.next()) {System.out.println("K:" + Keyboard.getEventKey() + "("	+ Keyboard.getKeyName(Keyboard.getEventKey()) + ")");}
		 
	}
	
	public boolean onKeyPress(int k)
	{
		/*if(k == 19)
			eng.targetFPS = 60;*/
		if(pressstart && depoping <= 25 && k == FastConfig.keyStart)
		{
			//System.out.println("validated : "+currentSelection);
			int i = currentSelection;
			if(mayUpdate && i == 1)
				i = 10;
			if(mayUpdate && i >= 2 && i < 10)
				i--;
			switch(i)
			{
			case 0:
				//SoundManager.playSFX("wrong");
				validated = 1;
				//this.eng.changeScene(new GameScene(eng));
				this.eng.changeScene(new ServerSelectionScene(eng,bgscroll,true));
				break;
			case 1:
				//SoundManager.playSFX("wrong");
				validated = 1;
				this.eng.changeScene(new ServerSelectionScene(eng,bgscroll,false));
				break;
			case 2:
				validated = 1;
				this.eng.changeScene(new LevelEditorSelectionScene(eng,bgscroll));
				break;
			case 3:
				validated = 1;
				this.eng.changeScene(new OptionsScene(eng,bgscroll));
				//SoundManager.playSFX("wrong");
				break;
			case 4:
				validated = 1;
				this.eng.changeScene(new CreditsScene(eng));
				//SoundManager.playSFX("wrong");
				break;
			case 5:
				validated = 1;
				close();
				break;
			case 6:
				validated = 1;
				System.out.println("close");
				removeAutoLogin();
				close();
				break;
			case 10:
				//SoundManager.playSFX("wrong");
				Client.shouldUpdate = true;
				close();
				break;
			default:
				break;
			}
		}
		return true;
	}
	
	private void removeAutoLogin() {
		Client.getConfig().setProp("autologin", "ko");
		Client.getConfig().setProp("user","");
		Client.getConfig().setProp("pass","");
	}

	private void close() {
		eng.close();
	}

	int getScreenSizeMultiplier()
	{
		if(XolioWindow.frameW > 1200)
			return 2;
		return 1;
	}

	@Override
	public void handleHttpRequest(String info, String result) {
		if(info.equals("version"))
		{
			if(!result.equals(VersionInfo.version))
			{
				onlineVersion = result;
				mayUpdate = true;
				menuitems = new String[] {"Play on last server","#D02020Update","Select a server","Level editor","Options","Credits","Quit","Disconnect and Quit"};
			
			}
		}
	}

}
