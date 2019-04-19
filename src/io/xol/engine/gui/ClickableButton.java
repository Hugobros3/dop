package io.xol.engine.gui;

import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.base.font.BitmapFont;
import io.xol.engine.base.font.FontRenderer;

//import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

//(c) 2014 XolioWare Interactive

public class ClickableButton extends Focusable {

	int posx;
	int posy;
	public boolean clicked = false;
	String text = "";
	BitmapFont font;
	public int size;
	public ClickableButton(int x,int y,String t,BitmapFont f,int s)
	{
		posx = x;
		posy = y;
		text = t;
		font = f;
		size = s;
	}
	
	public int getWidth()
	{
		int width = FontRenderer.getTextLengthUsingFont(size*16, text, font);
		return width+0;
	}
	
	public boolean isOver() {
		return (Mouse.getX() > posx && Mouse.getX() < posx + getWidth()*2 && Mouse.getY() > posy && Mouse.getY() < posy + (size)*20);
		
	}

	public int draw()
	{
		int width = FontRenderer.getTextLengthUsingFont(size*16, text, font);
		if(focus || isOver())
		{
			ObjectRenderer.renderTexturedRotatedRect(posx, posy,24*size , 48*size, 0, 0, 0, 12/64f, 1, "gui/focusbutton");
			ObjectRenderer.renderTexturedRotatedRect(posx+width-5, posy,(width)*size*2f , 48*size, 0, 12/64f, 0, 24/64f, 1f, "gui/focusbutton");
			ObjectRenderer.renderTexturedRotatedRect(posx+width*2*size-12, posy,24*size , 48*size, 0, 52/64f, 0, 1f, 1f, "gui/focusbutton");
			FontRenderer.drawTextUsingSpecificFont(posx-6, posy-12, 0, size*32, text, font);
		}
		else
		{
			ObjectRenderer.renderTexturedRotatedRect(posx, posy,24*size , 48*size, 0, 0, 0, 12/64f, 1, "gui/button");
			ObjectRenderer.renderTexturedRotatedRect(posx+width-5, posy,(width)*size*2f , 48*size, 0, 12/64f, 0, 24/64f, 1f, "gui/button");
			ObjectRenderer.renderTexturedRotatedRect(posx+width*2*size-12, posy,24*size , 48*size, 0, 52/64f, 0, 1f, 1f, "gui/button");
			FontRenderer.drawTextUsingSpecificFont(posx-6, posy-12, 0, size*32, text, font);
		}
		return width*2*size-12;
	}
	
	public void update() {
		
		clicked = false;
		if (isOver() && Mouse.isButtonDown(0) /*|| (focus && Keyboard.isKeyDown(FastConfig.keyStart))-*/) {
			//System.out.println("clicked"+focus+"="+posx);
			clicked = true;
		}
	}
	public void setPos(float f, float g) {
		posx = (int) f;
		posy = (int) g;
	}
}
