package io.xol.dop.game.client.subscenes;

//(c) 2014 XolioWare Interactive

import io.xol.dop.game.World;
import io.xol.dop.game.client.Client;
import io.xol.dop.game.client.scenes.EditorScene;
import io.xol.engine.base.XolioWindow;
import io.xol.engine.base.font.BitmapFont;
import io.xol.engine.base.font.FontRenderer;
import io.xol.engine.gui.ClickableButton;
import io.xol.engine.gui.CorneredBoxDrawer;
import io.xol.engine.gui.FocusableObjectsHandler;
import io.xol.engine.gui.InputText;
import io.xol.engine.scene.Scene;
import io.xol.engine.scene.SubScene;

public class NewLevelSubscene extends SubScene{

	FocusableObjectsHandler guiHandler = new FocusableObjectsHandler();
	
	public InputText levelName = new InputText();
	
	public InputText levelWidth = new InputText();
	public InputText levelHeight = new InputText();
	
	public ClickableButton done = new ClickableButton(0,0,"Done",BitmapFont.EDITUNDO,1);
	public ClickableButton cancel = new ClickableButton(0,0,"Cancel",BitmapFont.EDITUNDO,1);
	
	public NewLevelSubscene(Scene p)
	{
		super(p);
		levelName.focus = true;
		//add buttons
		guiHandler.add(levelName);
		guiHandler.add(levelWidth);
		guiHandler.add(levelHeight);
		guiHandler.add(done);
		guiHandler.add(cancel);
	}
	int wait = 0;
	//int optionCycling = 0;
	
	//rendering
	public void update()
	{
		if(wait < 200)
			wait++;
		
		int boxw = 500;
		int boxh = 300;
		
		float spx = XolioWindow.frameW-boxw*getScreenSizeMultiplier();
		float spy = XolioWindow.frameH-boxh*getScreenSizeMultiplier();
		
		done.setPos(spx/2+16, spy/2+8*mul());
		cancel.setPos(XolioWindow.frameW-(spx/2)-cancel.getWidth()*2, spy/2+8*mul());
		
		CorneredBoxDrawer.drawCorneredBox(XolioWindow.frameW/2, XolioWindow.frameH/2, boxw*getScreenSizeMultiplier(), boxh*getScreenSizeMultiplier(), 8*getScreenSizeMultiplier(), "gui/newLevelBG");
		
		FontRenderer.drawTextUsingSpecificFont(spx/2+16, XolioWindow.frameH-spy/2-32*mul(),0,24*mul(),"New level",BitmapFont.EDITUNDO,1f);
		
		FontRenderer.drawTextUsingSpecificFont(spx/2+16, XolioWindow.frameH-spy/2-48*mul(),0,16*mul(),"Name :",BitmapFont.EDITUNDO,1f);
		
		levelName.drawWithBackGround(spx/2+16, XolioWindow.frameH-spy/2-64*mul(),16*mul(), BitmapFont.SMALLFONTS,(boxw-32)*getScreenSizeMultiplier());
		
		FontRenderer.drawTextUsingSpecificFont(spx/2+16, XolioWindow.frameH-spy/2-86*mul(),0,16*mul(),"Size in sectors* :",BitmapFont.EDITUNDO,1f);
		
		levelWidth.drawWithBackGround(spx/2+16, XolioWindow.frameH-spy/2-104*mul(),16*mul(), BitmapFont.SMALLFONTS,(boxw-40)/2*getScreenSizeMultiplier());
		
		levelHeight.drawWithBackGround(spx/2+16+(boxw-40)/2*getScreenSizeMultiplier()+16, XolioWindow.frameH-spy/2-104*mul(),16*mul(), BitmapFont.SMALLFONTS,(boxw-40)/2*getScreenSizeMultiplier());
		
		FontRenderer.drawTextUsingSpecificFont(spx/2+16, XolioWindow.frameH-spy/2-128*mul(),0,16*mul(),"* A sector is 32 blocks",BitmapFont.SMALLFONTS,1f);
		
		done.draw();
		cancel.draw();
		
		if(cancel.clicked)
			parent.destroySubscene();
		if(done.clicked)
		{
			tryCreateLevel();
		}
		super.update();
	}
	//Controls handling
	public boolean onKeyPress(int k) {
		if(k == Client.clientConfig.getIntProp("TABKEY", "15"))
			guiHandler.next();
		/*else if(k == FastConfig.keyStart)
			tryCreateLevel();*/
		else if(k == Client.clientConfig.getIntProp("BACKKEY", "14"))
			parent.destroySubscene();
		else
			guiHandler.handleInput(k);
		return false;
	}

	public boolean onClick(int posx,int posy,int button)
	{
		guiHandler.handleClick(posx, posy);
		return true;
	}
	//Simple dirty check
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    Integer.parseInt(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
	
	//GUI dirty stuff
	int mul()
	{
		return 1+getScreenSizeMultiplier();
	}
	
	int getScreenSizeMultiplier()
	{
		if(XolioWindow.frameW > 1200)
			return 2;
		return 1;
	}
	//Scene-specific actions
	private void tryCreateLevel() {
		if(!levelName.text.equals("") && isNumeric(levelHeight.text) && (isNumeric(levelWidth.text)))
		{
			World newworld = new World(Integer.parseInt(levelWidth.text),Integer.parseInt(levelHeight.text),levelName.text,true);
			parent.eng.changeScene(new EditorScene(parent.eng,newworld));
		}
		else
		{
			// be unhappy
		}
	}
}
