package io.xol.dop.game.client.fx;

//(c) 2014 XolioWare Interactive

import io.xol.dop.game.units.Unit;
import io.xol.engine.base.ObjectRenderer;

public class FXCaption extends FXBase{

	int x = 0;
	int y = 0;
	
	int timeLeft = 60;
	String name;
	
	public FXCaption(Unit unit, String name)
	{
		this.x = unit.posX*32;
		this.y = unit.posY*32;
		this.name = name;
	}
	
	@Override
	public void render(int camX, int camY) {
		ObjectRenderer.renderTexturedRectAlpha(x-camX, y-camY, 32, 32, "units/say/"+name,1f);
		timeLeft--;
		if(timeLeft < 0)
			kill();
	}

}
