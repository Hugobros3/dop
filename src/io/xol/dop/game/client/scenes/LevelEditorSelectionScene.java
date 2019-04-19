package io.xol.dop.game.client.scenes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import io.xol.dop.game.World;
import io.xol.dop.game.client.Client;
import io.xol.dop.game.client.FastConfig;
import io.xol.dop.game.client.subscenes.NewLevelSubscene;
import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.base.XolioWindow;
import io.xol.engine.base.font.BitmapFont;
import io.xol.engine.base.font.FontRenderer;
import io.xol.engine.gui.KeyButtonDrawer;
import io.xol.engine.misc.ConfigFile;
import io.xol.engine.scene.Scene;

//(c) 2014 XolioWare Interactive

public class LevelEditorSelectionScene extends Scene {

	public LevelEditorSelectionScene(XolioWindow XolioWindow,int bg) {
		super(XolioWindow);
		List<String> lvls = new ArrayList<String>();
		//lvls.add("<Nouveau>");
		for(String f : new File(System.getProperty("user.dir")+"/levels").list())
		{
			if(!f.contains("_playing") && !f.contains("_played") && !f.contains("null"))
			{
				lvls.add(f);
			}
		}
		if(lvls.size() == 0)
			lvls.add("Levels folder can't be empty !");
		menuitems = lvls.toArray(menuitems);
		bgscroll = bg;
		// TODO Auto-generated constructor stub
	}
	
	boolean pressstart = false;
	int bgscroll = 0;
	int waitForInput = 0;
	String[] menuitems = {};
	int currentSelection = 0;
	int cooldown = 0;
	
	ConfigFile levelcfg;
	boolean hasPreview = false;
	
	public void update() {
		bgscroll++;
		waitForInput++;
		if(bgscroll >= 256)
			bgscroll = 0;
		//bg
		ObjectRenderer.renderTexturedRect(XolioWindow.frameW / 2+bgscroll-128,XolioWindow.frameH / 2+bgscroll-128, XolioWindow.frameW*2f, XolioWindow.frameH*2f,0f, 0f, XolioWindow.frameW / 2 *2f, XolioWindow.frameH / 2 *2f, 128f,"gui/menubggreen");
		//moving in menu
		if(cooldown > 0)
			cooldown --;
		if(this.subscene == null){
			if(Keyboard.isKeyDown(FastConfig.keyDown) && currentSelection < menuitems.length-1 && cooldown == 0)
			{
				levelcfg = null;
				currentSelection++;
				cooldown = 8;
			}
			if(Keyboard.isKeyDown(FastConfig.keyUp) && currentSelection > 0 && cooldown == 0)
			{
				levelcfg = null;
				currentSelection--;
				cooldown = 8;
			}
		}
		//level info loading
		if(levelcfg == null)
			loadLevelInfo(menuitems[currentSelection]);
		//scene title 'n stuff
		FontRenderer.drawTextUsingSpecificFontRVBA(32,XolioWindow.frameH - 32*(1+getScreenSizeMultiplier()),0,32+getScreenSizeMultiplier()*16,"Level editor",BitmapFont.EDITUNDO,1f,0.2f,0.8f,0.2f);
		FontRenderer.drawTextUsingSpecificFontRVBA(32,XolioWindow.frameH - 16-32*(1+getScreenSizeMultiplier()),0,getScreenSizeMultiplier()*16,"Select an existing level or create a new one",BitmapFont.EDITUNDO,1f,0.2f,0.8f,0.2f);
		//Display current level's shit
		if(!hasPreview)
			ObjectRenderer.renderTexturedRect(XolioWindow.frameW - 90*(1+getScreenSizeMultiplier()), XolioWindow.frameH-110*(1+getScreenSizeMultiplier()), 128*(1+getScreenSizeMultiplier()), 128*(1+getScreenSizeMultiplier()), "gui/nopreview");
		else
			ObjectRenderer.renderTexturedRect(XolioWindow.frameW - 90*(1+getScreenSizeMultiplier()), XolioWindow.frameH-110*(1+getScreenSizeMultiplier()), 128*(1+getScreenSizeMultiplier()), 128*(1+getScreenSizeMultiplier()), "../levels/"+menuitems[currentSelection]+"/preview");
		//dislay levels selection
		int count = 0;
		for(String item : menuitems)
		{
			int padding = 64*(1+getScreenSizeMultiplier());
			if(count == currentSelection)
				FontRenderer.drawTextUsingSpecificFont(32,XolioWindow.frameH - padding - count*32,0,16+getScreenSizeMultiplier()*16,item,BitmapFont.EDITUNDO,1f);
			else
				FontRenderer.drawTextUsingSpecificFont(32,XolioWindow.frameH - padding - count*32,0,16+getScreenSizeMultiplier()*16,item,BitmapFont.EDITUNDO,0.5f);
				//drawCenteredText(item,XolioWindow.frameH - padding - count*32,16,1f,1,1,0.2f);
			count++;
		}
		//buttons tips
		drawLeftedText("Load level",12,48,16,1f,1,1,1f);
		KeyButtonDrawer.drawButtonForKey(20, 20,FastConfig.keyStart , 1);
		
		drawCenteredText("New level",48,16,1f,1,1,1f);
		KeyButtonDrawer.drawButtonForKeyCentered(0, 20,Client.clientConfig.getIntProp("NEWKEY", "49") , 1);
		
		
		drawRightedText("Back",12,48,16,1f,1,1,1f);
		KeyButtonDrawer.drawButtonForKeyRightSide(60, 20,Client.clientConfig.getIntProp("BACKKEY", "14") , 1);
		super.update();
		//while (Keyboard.next()) {System.out.println("K:" + Keyboard.getEventKey() + "("	+ Keyboard.getKeyName(Keyboard.getEventKey()) + ")");}
		 
	}
	
	public boolean onKeyPress(int k)
	{
		//wait for enter
		if(this.subscene == null){
			if (waitForInput > 10 && Keyboard.isKeyDown(FastConfig.keyStart)) {
				this.eng.changeScene(new EditorScene(eng,new World(levelcfg.getIntProp("width", "10"),levelcfg.getIntProp("width", "10"),menuitems[currentSelection],true)));
			}
			//or for back !
			if (waitForInput > 10 && Keyboard.isKeyDown(Client.clientConfig.getIntProp("BACKKEY", "14"))) {
				this.eng.changeScene(new MainMenuScene(eng,bgscroll));
			}
			//and don't forget about new level handling !
			if (waitForInput > 10 && Keyboard.isKeyDown(Client.clientConfig.getIntProp("NEWKEY", "49"))) {
				this.setSubscene(new NewLevelSubscene(this));
				//this.eng.changeScene(new MainMenuScene(eng,bgscroll));
			}
		}
		else
		{
			this.subscene.onKeyPress(k);
		}
		return false;
	}
	
	public boolean onClick(int posx,int posy,int button)
	{
		if(this.subscene != null)
			subscene.onClick(posx, posy, button);
		return true;
	}
	
	int getScreenSizeMultiplier()
	{
		if(XolioWindow.frameW > 1200)
			return 2;
		return 1;
	}
	
	void drawCenteredText(String t,float height,int basesize,float r,float v,float b,float a)
	{
		FontRenderer.drawTextUsingSpecificFontRVBA(XolioWindow.frameW / 2 - FontRenderer.getTextLengthUsingFont(basesize, t, BitmapFont.EDITUNDO)/2,
				height, 0, basesize,t,BitmapFont.EDITUNDO,a,r,v,b);
	}
	
	void drawRightedText(String t,float decx,float height,int basesize,float r,float v,float b,float a)
	{
		FontRenderer.drawTextUsingSpecificFontRVBA(XolioWindow.frameW - decx -FontRenderer.getTextLengthUsingFont(basesize, t, BitmapFont.EDITUNDO),
				height, 0, basesize,t,BitmapFont.EDITUNDO,a,r,v,b);
	}
	
	void drawLeftedText(String t,float decx,float height,int basesize,float r,float v,float b,float a)
	{
		FontRenderer.drawTextUsingSpecificFontRVBA(decx /*-FontRenderer.getTextLengthUsingFont(basesize, t, BitmapFont.EDITUNDO),*/,
				height, 0, basesize,t,BitmapFont.EDITUNDO,a,r,v,b);
	}
	
	void loadLevelInfo(String name)
	{
		levelcfg = new ConfigFile("levels/"+name+"/level.cfg");
		File f = new File(System.getProperty("user.dir")+"/levels/"+name+"/preview.png");
		hasPreview = f.exists();
	}
}
