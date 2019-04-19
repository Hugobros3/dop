package io.xol.engine.scene;

import java.util.ArrayList;
import java.util.List;

import io.xol.engine.base.XolioWindow;
import io.xol.engine.gui.Button;

//(c) 2014 XolioWare Interactive

public class Scene {
	public XolioWindow eng;
	public List<Button> buttons = new ArrayList<Button>();
	public boolean resized = false;
	
	public SubScene subscene = null;
	boolean shouldDestroySubscene = false;
	
	public Scene(XolioWindow XolioWindow) {
		eng = XolioWindow;
	}

	public void update() {
		if(resized)
			resized = false;
		for (Button b : buttons) {
			b.render();
			b.update();
		}
		if(shouldDestroySubscene)
		{
			subscene = null;
			shouldDestroySubscene = false;
		}
		if(subscene != null)
			subscene.update();
		
		XolioWindow.tick();
	}
	
	public boolean onClick(int posx,int posy,int button)
	{
		return false;
	}
	
	public boolean onKeyPress(int k)
	{
		return false;
	}
	
	public boolean onKeyRelease(int k)
	{
		return false;
	}
	
	public boolean onScroll(int scrollAmount)
	{
		return false;
	}
	
	public void setSubscene(SubScene s)
	{
		subscene = s;
	}
	
	public void destroySubscene()
	{
		shouldDestroySubscene = true;
	}
}
