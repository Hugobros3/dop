package io.xol.dop.game.client.fx;

//(c) 2014 XolioWare Interactive

import io.xol.engine.base.ObjectRenderer;

public class FXSmoke extends FXBase{

	int x = 0;
	int y = 0;
	
	int timeLeft = 300;
	
	public FXSmoke(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void render(int camX, int camY) {
		y++;
		x+=Math.random()*3-1;
		//System.out.println("render fx"+(x-camX)+":"+ (y-camY)+" x:"+x+" y:"+y);
		//ObjectRenderer.renderTexturedRectAlpha(x-camX, y-camY, 32, 32, 0, 0, 16, 16, 16, "misc/smoke");
		ObjectRenderer.renderTexturedRectAlpha(x-camX, y-camY, 32, 32, "misc/smoke",timeLeft/300f);
		timeLeft--;
		if(timeLeft < 0)
			kill();
	}

}
