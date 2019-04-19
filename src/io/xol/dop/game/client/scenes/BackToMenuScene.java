package io.xol.dop.game.client.scenes;

import io.xol.dop.game.client.Client;
import io.xol.dop.game.client.FastConfig;
import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.base.XolioWindow;
import io.xol.engine.base.font.BitmapFont;
import io.xol.engine.base.font.FontRenderer;
import io.xol.engine.gui.ClickableButton;
import io.xol.engine.gui.FocusableObjectsHandler;
import io.xol.engine.scene.Scene;

//(c) 2014 XolioWare Interactive

public class BackToMenuScene extends Scene{

	FocusableObjectsHandler guiHandler = new FocusableObjectsHandler();
	int bgscroll = 0;
	
	String errorMessage;
	String errorMessage2;
	public BackToMenuScene(XolioWindow XolioWindow,int bg,String errorMessage,String errorMessage2) {
		super(XolioWindow);
		bgscroll = bg;
		guiHandler.add(new ClickableButton(0,0,"Back to main menu",BitmapFont.EDITUNDO,1));
		this.errorMessage = errorMessage;
		this.errorMessage2 = errorMessage2;
	}
	
	public void update()
	{
		//bg
		bgscroll++;
		if(bgscroll >= 256)
			bgscroll = 0;
		ObjectRenderer.renderTexturedRect(XolioWindow.frameW / 2+bgscroll-128,XolioWindow.frameH / 2+bgscroll-128, XolioWindow.frameW*2f, XolioWindow.frameH*2f,0f, 0f, XolioWindow.frameW / 2 *2f, XolioWindow.frameH / 2 *2f, 128f,"gui/menubgred");
		//title
		drawCenteredText(errorMessage,XolioWindow.frameH-XolioWindow.frameH/4,48,"FF0000",1,BitmapFont.EDITUNDO);
		drawCenteredText(errorMessage2,XolioWindow.frameH-XolioWindow.frameH/3,32,"FF0000",1,BitmapFont.SMALLFONTS);
		//gui
		guiHandler.getButton(0).setPos(XolioWindow.frameW/2-guiHandler.getButton(0).getWidth(),XolioWindow.frameH/4);
		guiHandler.getButton(0).draw();
		
		if(guiHandler.getButton(0).clicked)
			this.eng.changeScene(new MainMenuScene(eng,bgscroll));
		super.update();
	}
	//Controls handling
	public boolean onKeyPress(int k) {
		if(k == Client.clientConfig.getIntProp("TABKEY", "15"))
			guiHandler.next();
		else if(k == FastConfig.keyStart)
			this.eng.changeScene(new MainMenuScene(eng,bgscroll));
		else
			guiHandler.handleInput(k);
		return false;
	}

	public boolean onClick(int posx,int posy,int button)
	{
		guiHandler.handleClick(posx, posy);
		return true;
	}
	
	void drawCenteredText(String t,float height,int basesize,String hex,float a,BitmapFont f)
	{
		FontRenderer.drawTextUsingSpecificFontHex(XolioWindow.frameW / 2 - FontRenderer.getTextLengthUsingFont(basesize, t, f)/2,
				height, 0, basesize,t,f,hex,a);
	}
	void drawCenteredText(String t,float height,int basesize,float r,float v,float b,float a)
	{
		FontRenderer.drawTextUsingSpecificFontRVBA(XolioWindow.frameW / 2 - FontRenderer.getTextLengthUsingFont(basesize, t, BitmapFont.EDITUNDO)/2,
				height, 0, basesize,t,BitmapFont.EDITUNDO,a,r,v,b);
	}
}
