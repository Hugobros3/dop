package io.xol.dop.game.client.bits;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.lwjgl.input.Mouse;

import io.xol.dop.game.client.scenes.GameScene;
import io.xol.dop.game.client.subscenes.CreateNationSubscene;
import io.xol.dop.game.client.subscenes.NationsSubscene;
import io.xol.dop.game.common.nations.NationsColors;
import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.base.TexturesHandler;
import io.xol.engine.base.XolioWindow;
import io.xol.engine.base.font.BitmapFont;
import io.xol.engine.base.font.FontRenderer;
import io.xol.engine.misc.ColorsTools;

//(c) 2014 XolioWare Interactive

public class NationsInfo {
	GameScene scene;
	
	public static List<String> nationsList = new ArrayList<String>();
	public static String[] nations;
	public static String[] nationsDesc;
	public static String[] nationsPlayers;
	public static int[] nationsColors;
	public static int playerNation;
	
	public static int funds = -1;
	public static int population = -1;
	
	public NationsInfo(GameScene s)
	{
		scene = s;
	}

	public String getPlayerNation() {
		if(nations == null)
			return "#FF0000Error : Null nations";
		if(playerNation == -1)
			return "#FF0000No nation selected";
		if(playerNation < nations.length)
		{
			String nation = nations[playerNation];
			if(nation != null)
				return "#"+getColorHex()+nation;
		}
		//debug
		//System.out.println("wtf nation = "+playerNation);
		return "#FF0000Random error";
	}

	public static String getNation(int nation) {
		if(nation < nations.length)
		{
			String name = nations[nation];
			if(name != null)
				return name;
			return "error"+nation;
		}
		return "Error, nation > list ("+nation+")";
	}

	public static String getColorHex() {
		
		return getColorHex(playerNation);
	}
	
	public static String getColorHex(int i) {
		
		if(nationsColors != null)
			return ColorsTools.rgbToHex(NationsColors.getColor(nationsColors[i]));
		return "FFFFFF";
	}
	
	public static int getColor()
	{
		return getColor(playerNation);
	}
	
	public static int getColor(int i)
	{
		if(nationsColors != null)
			return NationsColors.getColor(nationsColors[i]);
		return 0;
	}
	
	public void handlePacket(String message)
	{
		//System.out.println(message);
		String data[] = message.split(":");
		if(data.length > 1)
		{
			if(data[0].equals("list"))
			{
				nationsList.clear();
				String split[] = data[1].split("/");
				int nationsCount = split.length;
				nations = new String[nationsCount];
				//System.out.println("debug: made nations"+nations.length);
				for(int i = 0; i < nationsCount; i++)
				{
					if(split[i] != null)
					{
						String name = split[i];
						if(name.equals("null"))
							name = null;
						else
							nationsList.add(split[i]);
						nations[i] = name;
					}
				}
			}
			if(data[0].equals("desc"))
			{
				String split[] = data[1].split("/");
				int nationsCount = split.length;
				nationsDesc = new String[nationsCount];
				//System.out.println("debug: made nationsDesc"+split.length);
				for(int i = 0; i < nationsCount; i++)
				{
					if(split[i] != null)
					{
						String desc = split[i];
						if(desc.equals("null"))
							desc = null;
						nationsDesc[i] = desc;
					}
				}
			}
			if(data[0].equals("players"))
			{
				String split[] = data[1].split("/");
				int nationsCount = split.length;
				nationsPlayers = new String[nationsCount];
				for(int i = 0; i < nationsCount; i++)
				{
					if(split[i] != null)
					{
						String play = split[i];
						if(play.equals("null"))
							play = null;
						nationsPlayers[i] = play;
					}
				}
			}
			if(data[0].equals("colors"))
			{
				String split[] = data[1].split("/");
				int nationsCount = split.length;
				nationsColors = new int[nationsCount];
				for(int i = 0; i < nationsCount; i++)
				{
					if(split[i] != null)
					{
						String play = split[i];
						if(play.equals("null"))
							play = "0";
						nationsColors[i] = Integer.parseInt(play);
					}
				}
			}
			if(data[0].equals("flags"))
			{
				File f = new File("res/textures/cache/nations_temp.png");
				if(f.exists())
					f.delete();
				try{
					TexturesHandler.freeTexture("cache/nations_temp");
					FileOutputStream fos = new FileOutputStream(f);
					fos.write(Base64.decodeBase64(data[1]));
					fos.close();
					//System.out.println("File written");
				}
				catch(Exception e)
				{
					
				}
			}
		}
		if(message.equals("nationAdded"))
		{
			if(scene.subscene != null && scene.subscene instanceof CreateNationSubscene)
			{
				scene.setSubscene(new NationsSubscene(scene,NationsInfo.playerNation != -1));
			}
		}
		if(scene.subscene != null && scene.subscene instanceof NationsSubscene)
		{
			((NationsSubscene)scene.subscene).updateData(this);
		}
	}
	
	public void renderNationsPanel()
	{
		int x = XolioWindow.frameW-220;
		int y = XolioWindow.frameH-XolioWindow.frameH/2+nationsList.size()*16;
		
		String color = "FFFFFF";
		
		int maxLen = 200;
		/*int nbLines = 0;
		for(String line : nationsList)
		{
			//maxLen = Math.max(maxLen, FontRenderer.getTextLengthUsingFont(32, line, BitmapFont.TINYFONTS));
			nbLines++;
		}*/
		int nbLines = nationsList.size();
		int fontHeight = 24;
		float overlay = ( Mouse.getX() > x && Mouse.getX() < x+200 && Mouse.getY()-24-8 < y && Mouse.getY() >  y-nbLines*fontHeight+24) ? 0.1f : 0.8f;
		
		ObjectRenderer.renderColoredRect(x+maxLen/2+2, y+28-(nbLines)*fontHeight/2, maxLen+4, nbLines*fontHeight+8, 0, color, 0.5f*overlay);
		nbLines = 0;
		//int id = 0;
		if(nations != null)
		{
			for(int id = 0; id < nations.length; id++)
			{
				if(nations[id] != null)
				{
					if(nationsColors != null)
						FontRenderer.drawTextUsingSpecificFontHex(x, y-nbLines*fontHeight, 0, 32, " #"+ColorsTools.rgbToHex(NationsColors.getColor(nationsColors[id]))+nations[id], BitmapFont.SMALLFONTS, "FFFFFF", 1f*overlay);
					else
						FontRenderer.drawTextUsingSpecificFontHex(x, y-nbLines*fontHeight, 0, 32, " "+nations[id], BitmapFont.SMALLFONTS, "FFFFFF", 1f*overlay);
					nbLines++;
				}
			}
			ObjectRenderer.drawLine(x-2, y+32, x+maxLen+6, y+32, 2, 0, 0, 0, 1f*overlay);
			ObjectRenderer.drawLine(x-2, y-nbLines*fontHeight+24, x+maxLen+6,  y-nbLines*fontHeight+24, 2, 0, 0, 0, 1f*overlay);
			ObjectRenderer.drawLine(x-1, y+32, x-1,  y-nbLines*fontHeight+24, 2, 0, 0, 0, 1f*overlay);
			ObjectRenderer.drawLine(x+maxLen+5, y+32, x+maxLen+5, y-nbLines*fontHeight+24, 2, 0, 0, 0, 1f*overlay);
		}
		//ObjectRenderer.drawLine(0, 0, 200,200, 2, 45,45,45, 0.5f);
		//ObjectRenderer.renderTexturedRectAlpha(x+maxLen/2+2, y+(nbLines)*8-4, maxLen+4, nbLines*16+8, "misc/greytex", 0.5f);
	}
}
