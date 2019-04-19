package io.xol.engine.scene;

//(c) 2014 XolioWare Interactive

public class SubScene {
	protected Scene parent;
	
	public SubScene(Scene p)
	{
		parent = p;
	}
	public void update() {
		
	}
	public boolean onKeyPress(int k) {
		return false;
	}

	public boolean onClick(int posx,int posy,int button)
	{
		return false;
	}
	public boolean onWheel(int dx) {
		return false;
	}
}
