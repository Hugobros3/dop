package io.xol.dop.game.client.scenes;

import io.xol.dop.game.client.subscenes.OptionsSubscene;
import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.base.XolioWindow;
import io.xol.engine.scene.Scene;

//(c) 2014 XolioWare Interactive

public class OptionsScene extends Scene{

	int bgscroll = 0;
	
	public OptionsScene(XolioWindow eng, int bg) {
		super(eng);
		bgscroll = bg;
		this.setSubscene(new OptionsSubscene(this,false));
	}

	public void update() {
		bgscroll++;
		if(bgscroll >= 256)
			bgscroll = 0;
		//bg
		ObjectRenderer.renderTexturedRect(XolioWindow.frameW / 2+bgscroll-128,XolioWindow.frameH / 2+bgscroll-128, XolioWindow.frameW*2f, XolioWindow.frameH*2f,0f, 0f, XolioWindow.frameW / 2 *2f, XolioWindow.frameH / 2 *2f, 128f,"gui/menubgpurple");
		if(this.subscene == null)
			this.eng.changeScene(new MainMenuScene(this.eng, bgscroll));
		super.update();
	}
	
	public boolean onKeyPress(int k)
	{
		if(this.subscene != null)
			return subscene.onKeyPress(k);
		return false;
	}
}
