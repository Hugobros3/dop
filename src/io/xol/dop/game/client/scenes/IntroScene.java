package io.xol.dop.game.client.scenes;

import io.xol.dop.game.client.FastConfig;
import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.base.XolioWindow;
import io.xol.engine.scene.Scene;

//(c) 2014 XolioWare Interactive

public class IntroScene extends Scene {
	float i = 0;

	public IntroScene(XolioWindow XolioWindow) {
		super(XolioWindow);
	}

	public void update() {
		i+=0.02;
		
		if(i < 3.14)
		{
			ObjectRenderer.renderTexturedRectAlpha(XolioWindow.frameW / 2,XolioWindow.frameH / 2, 512,512,"xolioware", (float) Math.abs(Math.sin(i)));
		}
		if(i > 4 && i < 3.14+4)
		{
			ObjectRenderer.renderTexturedRectAlpha(XolioWindow.frameW / 2,XolioWindow.frameH / 2, 512,512,"despotism", (float) Math.abs(Math.sin(i-4)));
		}
		if(i > 9)
		{
			this.eng.changeScene(new LoginScene(eng));
		}
		super.update();
	}
	//Handle direct keys access
	public boolean onKeyPress(int k)
	{
		//Direct to menu
		if(k == FastConfig.keyStart)
		{
			this.eng.changeScene(new LoginScene(eng));
		}
		if(k == FastConfig.keyDebug)
		{
			this.eng.changeScene(new BackToMenuScene(eng,0,"Unknow error","We're just debbuging."));
		}
		return false;
	}
}
