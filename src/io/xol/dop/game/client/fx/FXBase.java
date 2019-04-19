package io.xol.dop.game.client.fx;

//(c) 2014 XolioWare Interactive

public abstract class FXBase {
	boolean alive = true;
	
	public abstract void render(int camX, int camY);
	
	public boolean isDead()
	{
		return !alive;
	}
	
	public void kill()
	{
		alive = false;
	}
}
