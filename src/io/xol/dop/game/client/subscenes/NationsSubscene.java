package io.xol.dop.game.client.subscenes;

import org.lwjgl.input.Mouse;

import io.xol.dop.game.client.Client;
import io.xol.dop.game.client.FastConfig;
import io.xol.dop.game.client.bits.NationsInfo;
import io.xol.dop.game.client.scenes.GameScene;
import io.xol.dop.game.common.nations.NationsColors;
import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.base.TexturesHandler;
import io.xol.engine.base.XolioWindow;
import io.xol.engine.base.font.BitmapFont;
import io.xol.engine.base.font.FontRenderer;
import io.xol.engine.gui.ClickableButton;
import io.xol.engine.gui.CorneredBoxDrawer;
import io.xol.engine.misc.ColorsTools;
import io.xol.engine.scene.Scene;
import io.xol.engine.scene.SubScene;

//(c) 2014 XolioWare Interactive

public class NationsSubscene extends SubScene{
	
	ClickableButton newNation = new ClickableButton(0,0,"New nation",BitmapFont.SMALLFONTS,1);
	
	long lastUpdate = 0;
	NationData[] nations;
	int scroll = 0;
	int size = 0;
	int selection = 0;
	
	int lastMX=0;
	int lastMY=0;
	
	boolean canLeave;
	
	public NationsSubscene(Scene p, boolean canLeave) {
		super(p);
		this.canLeave = canLeave;
		TexturesHandler.freeTexture("cache/nations_temp");
	}
	
	public void updateData(NationsInfo info)
	{
		if(NationsInfo.nations == null)
			return;
		nations = new NationData[NationsInfo.nations.length];
		for(int i = 0; i < NationsInfo.nations.length; i++)
		{
			if(NationsInfo.nations[i] != null)
			{
				String desc = "No description";
				String players = "NO INFO";
				if(NationsInfo.nationsDesc != null && NationsInfo.nationsDesc[i] != null)
					desc = NationsInfo.nationsDesc[i];
				if(NationsInfo.nationsPlayers != null && NationsInfo.nationsPlayers[i] != null)
					players = NationsInfo.nationsPlayers[i];
				if(NationsInfo.nationsColors != null)
					nations[i] = new NationData(i,"#"+ColorsTools.rgbToHex(NationsColors.getColor(NationsInfo.nationsColors[i]))+NationsInfo.nations[i],desc,players);
				else
					nations[i] = new NationData(i,NationsInfo.nations[i],desc,players);
			}
		}
		size = NationsInfo.nationsList.size();
		//System.out.println("NAtions data : "+nations.length+" = "+nations.toString());
	}
	
	public void update()
	{
		newNation.update();
		if(this.newNation.clicked)
			this.parent.setSubscene(new CreateNationSubscene(parent));
		//Net update
		if(lastUpdate < System.currentTimeMillis() - 10000)
		{
			lastUpdate = System.currentTimeMillis();
			Client.connect.send("player/nationInfo");
			Client.connect.send("player/nationFlags");
		}
		int boxw = XolioWindow.frameW-150;
		int boxh = XolioWindow.frameH-150;
		//Mouse update
		if(lastMX != Mouse.getX() || lastMY != Mouse.getY())
		{
			lastMX = Mouse.getX();
			lastMY = Mouse.getY();	
			if(Mouse.getX() > 75 && XolioWindow.frameW-Mouse.getX() > 75)
			{
				if(Mouse.getY() > 163 && XolioWindow.frameH-125 > Mouse.getY())
				{//boxh-128 164
					//System.out.println((XolioWindow.frameH-Mouse.getY()-125)/64+"-"+Mouse.getY());
					selection = scroll+(XolioWindow.frameH-Mouse.getY()-125)/64;
				}
			}
		}
		
		if(selection >= size)
			selection = size-1;
		//Drawing
		CorneredBoxDrawer.drawCorneredBox(XolioWindow.frameW/2, XolioWindow.frameH/2, boxw, boxh, 8, "gui/nationsSelectBG");
		FontRenderer.drawTextUsingSpecificFontHex(90, XolioWindow.frameH-120, 0, 32, "SELECT A NATION", BitmapFont.EDITUNDO, "00A0FF", 1);
		
		newNation.setPos(100, 90);
		int decal = newNation.draw();
		FontRenderer.drawTextUsingSpecificFontHex(90+decal+30, 80, 0, 32, "Join or create a nation to play", BitmapFont.SMALLFONTS, "00A0FF", 1);
		//nationsSelectBG
		int id = scroll;
		int id2 = scroll;
		if(nations != null)
		{
			int i = 20;
			while(i < boxh-128 && id < nations.length)
			{
				if(id < nations.length && nations[id] != null)
				{
					nations[id].render(i,selection==id2);
					i+=64;
					id2++;
				}
				id++;
			}
		}
		String bottomRightText = (selection+1)+"/"+size;
		FontRenderer.drawTextUsingSpecificFontHex(XolioWindow.frameW-100-FontRenderer.getTextLengthUsingFont(32, bottomRightText, BitmapFont.SMALLFONTS), 80, 0, 32, bottomRightText, BitmapFont.SMALLFONTS, "FFFFFF", 1);
		
	}
	
	public boolean onKeyPress(int k) {
		if( k == FastConfig.keyUp)
		{
			selection--;
		}
		else if( k == FastConfig.keyDown)
		{
			selection++;
		}
		if(selection >= size)
			selection = size-1;
		if(selection < 0)
			selection = 0;
		//System.out.println("Max displayable nations :"+(XolioWindow.frameH-232)/64);
		if(selection+1 > (scroll*64+XolioWindow.frameH-232)/64)
			scroll++;
		if(selection < scroll)
			scroll--;
		//Validate
		if(k == FastConfig.keyStart)
		{
			validate(selection);
		}
		if(k == FastConfig.keyBack)
		{
			if(canLeave)
				parent.setSubscene(null);
			else
				((GameScene)parent).backToMenu();
		}
		return true;
	}
	
	public boolean onClick(int posx,int posy,int button)
	{
		if(posx > 75 && XolioWindow.frameW-posx > 75)
		{
			if(posy > 163 && XolioWindow.frameH-125 > posy)
			{
				validate(selection);
			}
		}
		return false;
	}
	
	public void validate(int id)
	{
		id++;
		int goodID = 0;
		int i = 0;
		while(i < id && i < nations.length)
		{
			if(nations[goodID] != null)
				i++;
			goodID++;
		}
		goodID--;
		//System.out.println("Validated nationID : "+goodID);
		Client.connect.send("player/joinNation:"+goodID);
		parent.destroySubscene();
	}
	
	public boolean onWheel(int dx) {
		//System.out.println(dx);
		scroll-=dx/120;
		if(scroll < 0)
			scroll = 0;
		if(scroll+(XolioWindow.frameH-150)/128 > size)
			scroll--;
		return true;
	}
	
	public class NationData{
		String name;
		String desc;
		String players;
		int id;
		public NationData(int i,String n,String d, String p)
		{
			id = i;
			desc = d;
			players = p;
			name = n;
		}
		public void render(int i, boolean b) {
			ObjectRenderer.drawLine(80,XolioWindow.frameH-105-i,XolioWindow.frameW-80,XolioWindow.frameH-105-i, 2, 0, 0, 0, 0.5f);
			//ObjectRenderer.renderTexturedRect(80,XolioWindow.frameH-105-i, 24*2, 32, 0, 0, 24, 16, 32, "cache_nation_"+id);
			if(b)
				ObjectRenderer.renderColoredRect(XolioWindow.frameW/2,XolioWindow.frameH-137-i,XolioWindow.frameW-160,64, 0, "000000", 0.3f);
			//id = (int) (Math.random()*3);
			int texSX = (id/8)*32;
			int texSY = (id%8)*16;
			ObjectRenderer.renderTexturedRect(80+24,XolioWindow.frameH-125-i, 24*2, 32, texSX, texSY, texSX+24, texSY+16, 128, "cache/nations_temp");
			
			FontRenderer.drawTextUsingSpecificFontHex(90+40,  XolioWindow.frameH-140-i, 0, 32, name+"#A0A0A0 - "+players, BitmapFont.SMALLFONTS, "FFFFFF", 1);
			FontRenderer.drawTextUsingSpecificFontHex(90+40,  XolioWindow.frameH-165-i, 0, 32, desc, BitmapFont.SMALLFONTS, "FFFFFF", 1);
			//FontRenderer.drawTextUsingSpecificFontHex(90+20,  XolioWindow.frameH-140-i, 0, 16*getScreenSizeMultiplier()+16, name, BitmapFont.SMALLFONTS, "FFFFFF", 1);
			
		}
	}
}
