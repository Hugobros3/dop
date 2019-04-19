package io.xol.dop.game.client.subscenes;

import io.xol.dop.game.client.FastConfig;
import io.xol.dop.game.client.scenes.GameScene;
import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.base.XolioWindow;
import io.xol.engine.base.font.BitmapFont;
import io.xol.engine.base.font.FontRenderer;
import io.xol.engine.gui.ClickableButton;
import io.xol.engine.gui.CorneredBoxDrawer;
//import io.xol.engine.locale.Localizer;
import io.xol.engine.scene.SubScene;

public class NotPauseSubscene extends SubScene{

	String[] message = new String[1];
	
	ClickableButton back = new ClickableButton(100,100,"Back",BitmapFont.EDITUNDO,1);
	ClickableButton options = new ClickableButton(100,100,"Options",BitmapFont.EDITUNDO,1);
	ClickableButton quit = new ClickableButton(100,100,"Disconnect",BitmapFont.EDITUNDO,1);
	
	int selectedButton = 0;
	
	GameScene pp;
	
	public NotPauseSubscene(GameScene p) {
		super(p);
		pp = p;
		message[0] = "This is not a pause menu.";//Localizer.getText("really_quit");
		back.focus = true;
	}

	public void update()
	{
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
		boxh = height+140;
		CorneredBoxDrawer.drawCorneredBox(XolioWindow.frameW/2, XolioWindow.frameH/2-10, boxw, boxh, 8, "gui/notPauseBG");
		int i = 0;
		for(String line : message)
		{
			FontRenderer.drawTextUsingSpecificFont(XolioWindow.frameW/2-FontRenderer.getTextLengthUsingFont(32, line, BitmapFont.SMALLFONTS)/2
					, XolioWindow.frameH/2-i*30+(height+50)/2, 0, 32, line, BitmapFont.SMALLFONTS);
			i++;
		}
		back.setPos(XolioWindow.frameW/2-back.getWidth(), XolioWindow.frameH/2-i*30+(height+50)/2);
		back.update();
		back.draw();
		if(back.clicked)
			close();
		//options
		options.setPos(XolioWindow.frameW/2-options.getWidth(), XolioWindow.frameH/2-i*30+(height-30)/2);
		options.update();
		options.draw();
		if(options.clicked)
			pp.setSubscene(new OptionsSubscene(pp,true));
		//disconnect
		quit.setPos(XolioWindow.frameW/2-quit.getWidth(), XolioWindow.frameH/2-i*30+(height-110)/2);
		quit.update();
		quit.draw();
		if(quit.clicked)
			pp.backToMenu();
	}
	
	private void close() {
		this.parent.setSubscene(null);
	}

	public boolean onKeyPress(int k) {
		if(k == FastConfig.keyStart)
		{
			switch(selectedButton)
			{
			case 0:
				close();
				break;
			case 1:
				pp.setSubscene(new OptionsSubscene(pp,true));
				break;
			case 2:
				pp.backToMenu();
				break;
			default:
				break;
			}
			return true;
		}
		if(k == FastConfig.keyBack)
		{
			close();
		}
		if(k == FastConfig.keyDown)
		{
			selectedButton++;
		}
		if(k == FastConfig.keyUp)
		{
			selectedButton--;
		}
		if(selectedButton < 0)
			selectedButton = 0;
		if(selectedButton > 2)
			selectedButton = 2;
		back.focus = (selectedButton == 0);
		options.focus = (selectedButton == 1);
		quit.focus = (selectedButton == 2);
		return true;
	}
	
	public boolean onClick(int posx,int posy,int button)
	{
		return false;
	}
}
