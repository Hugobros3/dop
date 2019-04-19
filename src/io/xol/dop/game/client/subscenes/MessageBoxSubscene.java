package io.xol.dop.game.client.subscenes;

//(c) 2014 XolioWare Interactive

import io.xol.dop.game.client.FastConfig;
import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.base.XolioWindow;
import io.xol.engine.base.font.BitmapFont;
import io.xol.engine.base.font.FontRenderer;
import io.xol.engine.gui.ClickableButton;
import io.xol.engine.gui.CorneredBoxDrawer;
import io.xol.engine.scene.Scene;
import io.xol.engine.scene.SubScene;

public class MessageBoxSubscene extends SubScene {

	SubScene previous;
	String[] message;
	ClickableButton ok = new ClickableButton(100,100,"Ok",BitmapFont.EDITUNDO,1);
	
	public MessageBoxSubscene(Scene p,String[] message,SubScene previous) {
		super(p);
		this.message = message;
		this.previous = previous;
	}

	public void update()
	{
		if(previous != null)
			previous.update();
		ObjectRenderer.renderColoredRect(XolioWindow.frameW/2, XolioWindow.frameH/2, XolioWindow.frameW, XolioWindow.frameH, 0, "000000", 0.3f);
		int height = 0;
		int boxw = 300;
		int boxh = 200;
		for(String line : message)
		{
			int width = FontRenderer.getTextLengthUsingFont(32, line, BitmapFont.SMALLFONTS);
			width+=60;
			if(width > boxw)
				boxw = width;
			height+=30;
		}
		boxh = height+60;
		CorneredBoxDrawer.drawCorneredBox(XolioWindow.frameW/2, XolioWindow.frameH/2-10, boxw, boxh, 8, "gui/messageBoxBG");
		int i = 0;
		for(String line : message)
		{
			FontRenderer.drawTextUsingSpecificFont(XolioWindow.frameW/2-FontRenderer.getTextLengthUsingFont(32, line, BitmapFont.SMALLFONTS)/2
					, XolioWindow.frameH/2-i*30+(height-30)/2, 0, 32, line, BitmapFont.SMALLFONTS);
			i++;
		}
		ok.setPos(XolioWindow.frameW/2-ok.getWidth(), XolioWindow.frameH/2-i*30+(height-30)/2);
		ok.update();
		ok.draw();
		if(ok.clicked)
			close();
	}
	
	public boolean onKeyPress(int k) {
		if(k == FastConfig.keyStart)
		{
			close();
		}
		return false;
	}
	
	public boolean onClick(int posx,int posy,int button)
	{
		return false;
	}
	
	void close()
	{
		parent.setSubscene(previous);
	}
	
}
