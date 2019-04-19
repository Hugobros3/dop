package io.xol.dop.game.client.bits;

//(c) 2014 XolioWare Interactive

import org.lwjgl.input.Mouse;

import io.xol.dop.game.client.Client;
import io.xol.dop.game.client.scenes.GameScene;
import io.xol.dop.game.client.subscenes.NationsSubscene;
import io.xol.dop.game.mode.GameModeContinuousGame;
import io.xol.dop.game.tiles.Tile;
import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.base.XolioWindow;
import io.xol.engine.base.font.BitmapFont;
import io.xol.engine.base.font.FontRenderer;

public class GuiTopBar {
	
	GameScene scene;
	String rightString1 = null;
	String rightString2 = null;
	
	String leftString1 = null;
	String leftString2 = null;
	
	String midString1 = null;
	String midString2 = null;
	
	public GuiTopBar(GameScene s)
	{
		scene = s;
	}
	
	public void update()
	{
		//This huge and ugly class displays the HUD and periodically updates it
		
		//This part hides the unusable part of the screen and displays the black gradient
		int scrapPlace = XolioWindow.frameH % 32;
		ObjectRenderer.renderTexturedRect(XolioWindow.frameW / 2,XolioWindow.frameH, XolioWindow.frameW, scrapPlace, 0f, 0f,64f, scrapPlace, 64f, "gui/ingameInfo");
		// filler                               xpos                ypos                          width       h       tsx  tsy    tex       tey      texName
		ObjectRenderer.renderTexturedRect(XolioWindow.frameW / 2,XolioWindow.frameH - 32*getScreenSizeMultiplier()-(scrapPlace/2), XolioWindow.frameW, 64*getScreenSizeMultiplier(), 0f, 0f,64f, 64f, 64f, "gui/ingameInfo");
		//
		int fontSize = 16*getScreenSizeMultiplier();
		//Right side first :
		rightString1 = Client.username+"@"+scene.address;
		rightString2 = "";
		if(scene.nations != null)
			rightString2 = scene.nations.getPlayerNation();
		int maxRightLen = Math.max(FontRenderer.getTextLengthUsingFont(fontSize, rightString1, BitmapFont.SMALLFONTS), FontRenderer.getTextLengthUsingFont(fontSize, rightString2, BitmapFont.SMALLFONTS));
		FontRenderer.drawTextUsingSpecificFont(XolioWindow.frameW-16-FontRenderer.getTextLengthUsingFont(fontSize, rightString1, BitmapFont.SMALLFONTS), XolioWindow.frameH-fontSize, 0, fontSize, rightString1, BitmapFont.SMALLFONTS);
		FontRenderer.drawTextUsingSpecificFont(XolioWindow.frameW-16-FontRenderer.getTextLengthUsingFont(fontSize, rightString2, BitmapFont.SMALLFONTS), XolioWindow.frameH-fontSize*1.8f, 0, fontSize, rightString2, BitmapFont.SMALLFONTS);
		
		//Nations settings
		if(Mouse.getX() > XolioWindow.frameW-16-FontRenderer.getTextLengthUsingFont(fontSize, rightString2, BitmapFont.SMALLFONTS) && Mouse.getX() < XolioWindow.frameW-16 && Mouse.getY() > XolioWindow.frameH-fontSize*1.8f && Mouse.getY() < XolioWindow.frameH-fontSize*1.8f+fontSize)
		{
			String displayMeh = "Click here to access nation settings";
			renderToolTip(XolioWindow.frameW-16-FontRenderer.getTextLengthUsingFont(fontSize, displayMeh, BitmapFont.TINYFONTS),(int)(XolioWindow.frameH-fontSize*3.0f),NationsInfo.getColorHex(),new String[]{displayMeh},fontSize);
			//System.out.println("in.");
		}
		//Disconnect button
		if(Mouse.getX() > XolioWindow.frameW-16-FontRenderer.getTextLengthUsingFont(fontSize, rightString1, BitmapFont.SMALLFONTS) && Mouse.getX() < XolioWindow.frameW-16 && Mouse.getY() > XolioWindow.frameH-fontSize*1.8f+fontSize && Mouse.getY() < XolioWindow.frameH-fontSize+fontSize)
		{
			String displayMeh = "Click here to disconnect.";
			renderToolTip(XolioWindow.frameW-16-FontRenderer.getTextLengthUsingFont(fontSize, displayMeh, BitmapFont.TINYFONTS),(int)(XolioWindow.frameH-fontSize*3.0f),"404040",new String[]{displayMeh},fontSize);
			//System.out.println("in.");
		}
		
		//Left side then
		leftString1 = "Funds : "+parseFunds(NationsInfo.funds);
		leftString2 = "Population : "+parsePop(NationsInfo.population);
		int maxLeftLen = Math.max(FontRenderer.getTextLengthUsingFont(fontSize, leftString1, BitmapFont.SMALLFONTS), FontRenderer.getTextLengthUsingFont(fontSize, leftString2, BitmapFont.SMALLFONTS));
		if(scene.editor != null) // IF EDITOR MODE
		{
			ObjectRenderer.renderTexturedRect(19*getScreenSizeMultiplier(), XolioWindow.frameH - 19*getScreenSizeMultiplier(), 32*getScreenSizeMultiplier(),32*getScreenSizeMultiplier(), "tiles/"+Tile.getTileByID(scene.editor.brushType).getTextureName());
			leftString1 = "Now placing :";
			leftString2 = scene.editor.brushType + " - " +Tile.getTileByID(scene.editor.brushType).getTileName()+ "";
			maxLeftLen = Math.max(FontRenderer.getTextLengthUsingFont(fontSize, leftString1, BitmapFont.SMALLFONTS), FontRenderer.getTextLengthUsingFont(fontSize, leftString2, BitmapFont.SMALLFONTS));
			maxLeftLen += 32*getScreenSizeMultiplier();
			FontRenderer.drawTextUsingSpecificFont(fontSize/2+32*getScreenSizeMultiplier(), XolioWindow.frameH-fontSize, 0, fontSize, leftString1, BitmapFont.SMALLFONTS);
			FontRenderer.drawTextUsingSpecificFont(fontSize/2+32*getScreenSizeMultiplier(), XolioWindow.frameH-fontSize*1.8f, 0, fontSize, leftString2, BitmapFont.SMALLFONTS);
		}
		else // ELSE
		{
			FontRenderer.drawTextUsingSpecificFont(fontSize/2, XolioWindow.frameH-fontSize, 0, fontSize, leftString1, BitmapFont.SMALLFONTS);
			FontRenderer.drawTextUsingSpecificFont(fontSize/2, XolioWindow.frameH-fontSize*1.8f, 0, fontSize, leftString2, BitmapFont.SMALLFONTS);
		}
		//Enought room for mid info ?
		midString1 = "Local world - ";
		midString2 = "";
		if(scene.isRemote)
		{
			if(scene.gameMode == null)
				midString1 = "Server ["+scene.serverName+"] - Gamemode is null - "+scene.cuco+"/"+scene.maxco+" users online.";
			else
				midString1 = "Server ["+scene.serverName+"] - "+scene.gameMode.getName()+" - "+scene.cuco+"/"+scene.maxco+" users online.";
			if(scene.editor != null)
				midString2 = "mouse click to place, wheel to select, right to pick";
			
			if(scene.world != null && scene.world.renderer != null)
			{
				if(scene.gameMode != null && scene.gameMode instanceof GameModeContinuousGame)
					midString2 = "Server speed :"+scene.gtuh+"gtu/h Time : "+scene.world.time/100  + ":" + (scene.world.time%100)*60/100;
				/*if(InputAbstractor.isKeyDown(FastConfig.keyDebug))
					scene.world.time++;
				scene.world.time = scene.world.time % 2400;
				if(scene.world.time%30 == 0)
				{
					scene.world.globalReRender();
				}*/
			}
		}
		int maxMidLen = Math.max(FontRenderer.getTextLengthUsingFont(fontSize, midString1, BitmapFont.SMALLFONTS), FontRenderer.getTextLengthUsingFont(fontSize, midString2, BitmapFont.SMALLFONTS));
		if(maxMidLen < (XolioWindow.frameW - maxLeftLen - maxRightLen))
		{
			int bestSpot1 = maxLeftLen+(XolioWindow.frameW-(maxLeftLen+maxRightLen))/2-FontRenderer.getTextLengthUsingFont(fontSize, midString1, BitmapFont.SMALLFONTS)/2;
			FontRenderer.drawTextUsingSpecificFont(bestSpot1, XolioWindow.frameH-fontSize, 0, fontSize, midString1, BitmapFont.SMALLFONTS);
			int bestSpot2 = maxLeftLen+(XolioWindow.frameW-(maxLeftLen+maxRightLen))/2-FontRenderer.getTextLengthUsingFont(fontSize, midString2, BitmapFont.SMALLFONTS)/2;
			FontRenderer.drawTextUsingSpecificFont(bestSpot2, XolioWindow.frameH-fontSize*1.8f, 0, fontSize, midString2, BitmapFont.SMALLFONTS);
		}
	}

	private String parseFunds(int funds) {
		String num = funds+"";
		String result = "";
		int i = 0;
		for(int z = num.toCharArray().length-1; z >= 0; z--)
		{
			char c = num.toCharArray()[z];
			if(i == 3)
			{
				i = 0;
				result="."+result;
			}
			result = c + result;
			i++;
		}
		result+="$";
		return result;
	}

	private String parsePop(int pop)
	{
		String num = pop+"";
		String result = "";
		int i = 0;
		for(int z = num.toCharArray().length-1; z >= 0; z--)
		{
			char c = num.toCharArray()[z];
			if(i == 3)
			{
				i = 0;
				result="."+result;
			}
			result = c + result;
			i++;
		}
		result+="";
		return result;
	}
	
	int getScreenSizeMultiplier()
	{
		if(XolioWindow.frameW > 1200)
			return 2;
		return 1;
	}
	
	public void renderToolTip(int x, int y, String color, String[] text,int size)
	{
		int maxLen = 0;
		int nbLines = 0;
		for(String line : text)
		{
			maxLen = Math.max(maxLen, FontRenderer.getTextLengthUsingFont(size, line, BitmapFont.TINYFONTS));
			nbLines++;
		}
		ObjectRenderer.renderColoredRect(x+maxLen/2+2, (int)(y+size*0.875-(nbLines)*size/4), maxLen+4, nbLines*size/2+8, 0, color, 0.5f);
		nbLines = 0;
		for(String line : text)
		{
			FontRenderer.drawTextUsingSpecificFontHex(x, y-nbLines*size, 0, size, line, BitmapFont.TINYFONTS, "FFFFFF", 1f);
			nbLines++;
		}
		float borderSize = size/16f;
		//System.out.println(borderSize);
		ObjectRenderer.drawLine(x, (int) (y+(size*0.875)+4), x+maxLen+4, (int) (y+(size*0.875)+4), borderSize, 0, 0, 0, 1);
		ObjectRenderer.drawLine(x, (int) (y-nbLines*16+12+size*(6/16f)), x+maxLen+4, (int) (y-nbLines*16+12+size*(6/16f)), borderSize, 0, 0, 0, 1);
		ObjectRenderer.drawLine(x, (int) (y+(size*0.875)+4), x,(int) (y-nbLines*16+12+size*(6/16f)), borderSize, 0, 0, 0, 1);
		ObjectRenderer.drawLine(x+maxLen+4, (int) (y+(size*0.875)+4), x+maxLen+4,(int) (y-nbLines*16+12+size*(6/16f)), borderSize, 0, 0, 0, 1);
		//ObjectRenderer.drawLine(0, 0, 200,200, 2, 45,45,45, 0.5f);
		//ObjectRenderer.renderTexturedRectAlpha(x+maxLen/2+2, y+(nbLines)*8-4, maxLen+4, nbLines*16+8, "misc/greytex", 0.5f);
	}

	public void handleClick(int posx, int posy, int button) {
		int fontSize = 16*getScreenSizeMultiplier();
		//Nations settings
		if(Mouse.getX() > XolioWindow.frameW-16-FontRenderer.getTextLengthUsingFont(fontSize, rightString2, BitmapFont.SMALLFONTS) && Mouse.getX() < XolioWindow.frameW-16 && Mouse.getY() > XolioWindow.frameH-fontSize*1.8f && Mouse.getY() < XolioWindow.frameH-fontSize*1.8f+fontSize)
		{
			scene.setSubscene(new NationsSubscene(scene,true));
		}
		//Disconnect button
		if(Mouse.getX() > XolioWindow.frameW-16-FontRenderer.getTextLengthUsingFont(fontSize, rightString1, BitmapFont.SMALLFONTS) && Mouse.getX() < XolioWindow.frameW-16 && Mouse.getY() > XolioWindow.frameH-fontSize*1.8f+fontSize && Mouse.getY() < XolioWindow.frameH-fontSize+fontSize)
		{
			//System.out.println("in.");
		}
	}
}
