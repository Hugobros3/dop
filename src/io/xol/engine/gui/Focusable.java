package io.xol.engine.gui;

//(c) 2014 XolioWare Interactive

public abstract class Focusable {

	public boolean focus = false;
	
	public boolean hasFocus()
	{
		return focus;
	}
	public void setFocus(boolean b)
	{
		focus = b;
	}
}
