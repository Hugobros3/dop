package io.xol.engine.gui;

import io.xol.engine.base.font.BitmapFont;
import io.xol.engine.base.font.FontRenderer;

import org.lwjgl.input.Keyboard;

//(c) 2014 XolioWare Interactive

public class InputText extends Focusable{
	public String text = "";
	
	public static int charDelay = 10;
	
	public void update()
	{
		if(focus)
		{
			while (Keyboard.next()) {
				if(Keyboard.getEventKeyState() == true){
				char c =Keyboard.getEventCharacter();
				int ek = Keyboard.getEventKey();
				if(ek == 14)
				{
					if(text.length() > 0)
					text = text.substring(0,text.length()-1);
				}
				else if(ek == 28)
				{
					
				}
				else//if(TextKeys.isTextKey(ek))
				{
					if(c != 0)
						text+=c;
				}
				}
			}
		}
	}
	public void input(int k)
	{
		char c = Keyboard.getEventCharacter();

		int ek = k;
		if(ek == 14)
		{
			if(text.length() > 0)
			text = text.substring(0,text.length()-1);
		}
		else if(ek == 28)
		{
			
		}
		else//if(TextKeys.isTextKey(ek))
		{
			if(c != 0)
				text+=c;
		}

		//System.out.println("passing input "+k+" c="+c+" txt="+text);
	}
	public void drawWithBackGround(float posx,float posy,int size,BitmapFont font,int maxlen)
	{
		if(focus)
			CorneredBoxDrawer.drawCorneredBox(posx+maxlen/2, posy+size/2, maxlen, 32, 8, "gui/textbox");
		else
			CorneredBoxDrawer.drawCorneredBox(posx+maxlen/2, posy+size/2, maxlen, 32, 8, "gui/textboxnofocus");
		FontRenderer.drawTextUsingSpecificFont(posx, posy,0,size,text,font,1f);
		//System.out.println(text);
	}
	public void drawWithBackGroundTransparent(float posx,float posy,int size,BitmapFont font,int maxlen)
	{
		if(focus)
			CorneredBoxDrawer.drawCorneredBox(posx+maxlen/2, posy+size/2, maxlen, 32, 8, "gui/textboxtransp");
		else
			CorneredBoxDrawer.drawCorneredBox(posx+maxlen/2, posy+size/2, maxlen, 32, 8, "gui/textboxnofocustransp");
		FontRenderer.drawTextUsingSpecificFont(posx, posy,0,size,text,font,1f);
		//System.out.println(text);
	}
	public void drawWithBackGroundPassworded(float posx,float posy,int size,BitmapFont font,int maxlen)
	{
		String passworded = "";
		for(@SuppressWarnings("unused") char c : text.toCharArray())
			passworded+="*";
		if(focus)
			CorneredBoxDrawer.drawCorneredBox(posx+maxlen/2, posy+size/2, maxlen, 32, 8, "gui/textbox");
		else
			CorneredBoxDrawer.drawCorneredBox(posx+maxlen/2, posy+size/2, maxlen, 32, 8, "gui/textboxnofocus");
		FontRenderer.drawTextUsingSpecificFont(posx, posy,0,size,passworded,font,1f);
		
	}
	public void setText(String t)
	{
		text = t;
	}
}
