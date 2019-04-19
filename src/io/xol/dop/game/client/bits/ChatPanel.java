package io.xol.dop.game.client.bits;

//(c) 2014 XolioWare Interactive

import io.xol.dop.game.client.Client;
import io.xol.dop.game.client.FastConfig;
import io.xol.dop.game.client.scenes.GameScene;
import io.xol.engine.base.XolioWindow;
import io.xol.engine.base.font.BitmapFont;
import io.xol.engine.base.font.FontRenderer;
import io.xol.engine.gui.InputText;

public class ChatPanel {

	int chatHistorySize = 50;
	String[] chatHistory = new String[chatHistorySize];
	InputText inputBox = new InputText();
	public boolean chatting = false;
	@SuppressWarnings("unused")
	private GameScene scene;
	
	public ChatPanel(GameScene s)
	{
		scene = s;
		java.util.Arrays.fill(chatHistory,"");
	}
	
	public void key(int k)
	{
		if(k == FastConfig.keyStart)
		{
			if(chatting)
			{
				chatting = false;
				Client.connect.send("chat/"+inputBox.text);
				inputBox.text = "";
			}
			else
			{
				inputBox.text = "";
				chatting = true;
			}
			
		}
		else
		{
			if(chatting)
				inputBox.input(k);
			/*else
			{
				if(scene.editor != null)
					scene.editor.onKeyPress(k);
			}*/
		}
			
	}
	
	public void update()
	{
		String m;
		while((m = Client.connect.getLastChatMessage()) != null)
			insert(m);
		draw(10);
		if(!chatting)
			inputBox.text = "<Press enter to chat>";
		//inputBox.drawWithBackGroundTransparent(12,25, 32, BitmapFont.SMALLFONTS, XolioWindow.frameW/3*2);
		inputBox.focus = true;
	}
	
	public void draw(int lines) {
		int a = 0;
		for(String text : chatHistory)
		{
			a++;
			if( a >= chatHistorySize-lines)
				FontRenderer.drawTextUsingSpecificFont(9,(-a+chatHistorySize+3)*24, 0, 32,text, BitmapFont.SMALLFONTS);
		}
		inputBox.drawWithBackGroundTransparent(12,25, 32, BitmapFont.SMALLFONTS, XolioWindow.frameW/3*2);
		
	}
	public void insert(String t)
	{
		for(int i = 0; i < chatHistorySize-1;i++)
		{
			chatHistory[i]=chatHistory[i+1];
		}
		chatHistory[chatHistorySize-1] = t;
	}
}
