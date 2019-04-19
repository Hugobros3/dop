package io.xol.dop.game.client.fx;

//(c) 2014 XolioWare Interactive

import io.xol.dop.game.client.renderer.NumbersRenderer;
import io.xol.dop.game.units.Unit;

public class FXDamage extends FXBase{
	int x = 0;
	int y = 0;
	
	int timeLeft = 60;
	int pv;
	
	public FXDamage(Unit unit, int pv)
	{
		this.x = unit.posX*32;
		this.y = unit.posY*32;
		this.pv = pv;
	}
	
	@Override
	public void render(int camX, int camY) {
		y++;
		NumbersRenderer.renderNumerStringColor(pv+"", x-camX, y-camY, "FF0000",timeLeft/60f);
		//ObjectRenderer.renderTexturedRectAlpha(x-camX, y-camY, 32, 32, "units/say/"+name,1f);
		timeLeft--;
		if(timeLeft < 0)
			kill();
	}
}
