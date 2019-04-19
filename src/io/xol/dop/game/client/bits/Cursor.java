package io.xol.dop.game.client.bits;

import org.lwjgl.input.Mouse;

import io.xol.dop.game.client.scenes.GameScene;
import io.xol.engine.base.AnimationsHelper;
import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.base.XolioWindow;

//(c) 2014 XolioWare Interactive

public class Cursor {

	GameScene scene;
	
	public int cursorX = 0;
	public int cursorY = 0;
	
	public Cursor(GameScene gameScene) {
		scene = gameScene;
	}

	public void update()
	{
		// cursor update
		cursorX = (Mouse.getX()) / 32;
		cursorY = (Mouse.getY()) / 32;
		int maxCursorY = (int) (Math.ceil((XolioWindow.frameH-64)/32f)-1*getScreenSizeMultiplier());
		// map painting
		if (cursorY > maxCursorY)
			cursorY = maxCursorY;
		//draw
		ObjectRenderer.renderTexturedRect(cursorX * 32 + 16, cursorY * 32 + 16,32 * 2, 32 * 2, AnimationsHelper.animatedTextureName("misc/cursor", 3, 100, true));
	}
	
	int getScreenSizeMultiplier()
	{
		if(XolioWindow.frameW > 1200)
			return 2;
		return 1;
	}
}
