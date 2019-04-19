package io.xol.dop.game.client.bits;

import io.xol.dop.game.client.Client;
import io.xol.dop.game.client.scenes.GameScene;
import io.xol.dop.game.client.subscenes.SelectUnitSubscene;
import io.xol.dop.game.client.subscenes.UnitActionSubscene;
import io.xol.dop.game.common.nations.NationsColors;
import io.xol.dop.game.pathfinder.AcceptablePath;
import io.xol.dop.game.pathfinder.PathRenderer;
import io.xol.dop.game.pathfinder.Pathfinder;
import io.xol.dop.game.tiles.InteractiveTile;
import io.xol.dop.game.tiles.Tile;
import io.xol.dop.game.tiles.TileFactory;
import io.xol.dop.game.units.Unit;
import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.base.font.BitmapFont;
import io.xol.engine.base.font.FontRenderer;
import io.xol.engine.misc.ColorsTools;

//(c) 2014 XolioWare Interactive

public class ControlBits {

	GameScene scene;
	int posSelectedX = -1;
	int posSelectedY = -1;
	
	int lastCX = -1;
	int lastCY = -1;
	public boolean unitSelected = false;
	public AcceptablePath acceptablePath;
	
	public ControlBits(GameScene s) {
		this.scene = s;
	}

	public boolean handle(int button, int posx, int posy) {
		if(scene.world == null)
			return false;
		if(scene.world.sectors == null)
			return false;
		int cx = scene.cursor.cursorX+scene.cam.cameraX;
		int cy = scene.cursor.cursorY+scene.cam.cameraY;
		if(cx >= scene.world.width*32 || cy >= scene.world.height*32)
			return false;
		if(unitSelected)
		{
			/*if(cx == posSelectedX && cy == posSelectedY)
			{
				unitSelected = false;
				acceptablePath = null;
				return true;
			}*/
			Unit unit = scene.world.getUnitAt(posSelectedX, posSelectedY);
			if(unit != null)
			{
				scene.setSubscene(new UnitActionSubscene(scene,unit,Tile.getTileByID(scene.world.getTileAt(posSelectedX, posSelectedY)),scene.cursor.cursorX,scene.cursor.cursorY));
				/*
				//Asks the unit to move !
				String moveUnitMsg = "gameMode/moveUnit:"+posSelectedX+":"+posSelectedY+":"+cx+":"+cy;
				Client.connect.send(moveUnitMsg);
				//System.out.println(moveUnitMsg);
				unitSelected = false;
				acceptablePath = null;
				*/
			}
			else
				unitSelected = false;
			return true;
		}
		Unit selected = scene.world.getUnitAt(cx, cy);
		if(selected != null)
		{
			//System.out.println("Clicked "+selected.name);
			if(!unitSelected && selected.nation == NationsInfo.playerNation)
			{
				unitSelected = true;
				posSelectedX = cx;
				posSelectedY = cy;
			}
			return true;
		}
		//System.out.println("cursor over tile "+selectedTile.getTileName());
		InteractiveTile it = scene.world.getInteractiveTile(cx, cy);
		if(it != null)
		{
			if(it instanceof TileFactory && ((TileFactory) it).nation == NationsInfo.playerNation)
			{
				posSelectedX = cx;
				posSelectedY = cy;
				scene.subscene = new SelectUnitSubscene(scene);
				return true;
			}
		}
		return false;
	}

	public boolean onKeyPress(int k) {
		if(scene.subscene != null && scene.subscene instanceof SelectUnitSubscene)
		{
			scene.subscene.onKeyPress(k);
			return true;
		}
		return false;
	}
	
	public void selectedUnit(int sel) {
		String spawnUnitMsg = "gameMode/makeUnit:"+posSelectedX+":"+posSelectedY+":"+sel;
		Client.connect.send(spawnUnitMsg);
		//System.out.println(spawnUnitMsg);
	}
	
	public void update()
	{
		if(scene.world == null)
			return;
		if(scene.world.sectors == null)
			return;
		
		if(acceptablePath != null)
			PathRenderer.renderPath(acceptablePath,-scene.cam.cameraX*32+16,+16-scene.cam.cameraY*32);
		
		int cx = scene.cursor.cursorX+scene.cam.cameraX;
		int cy = scene.cursor.cursorY+scene.cam.cameraY;
		if(cx >= scene.world.width*32 || cy >= scene.world.height*32)
			return;
		
		if(unitSelected)
		{
			Unit unit = scene.world.getUnitAt(posSelectedX, posSelectedY);
			if(!(lastCX == cx && lastCY == cy))
			{
				lastCX = cx;
				lastCY = cy;
				if(unit != null)
				{
					acceptablePath = Pathfinder.pathFind(scene.world, unit, cx, cy);
					//System.out.println("computin path"+acceptablePath.toString());
				}
				else
					unitSelected = false;
			}
			if(acceptablePath != null)
			{
				renderToolTip(scene.cursor.cursorX*32+32+8,scene.cursor.cursorY*32,"0000FF",new String[] {"distance : "+acceptablePath.distance,"Moving cost :"+acceptablePath.sumWeight});
				return;
			}
			renderUnitAttackRange(unit,-scene.cam.cameraX*32+16,+16-scene.cam.cameraY*32);
		}
		else
		{
			Unit selected = scene.world.getUnitAt(cx, cy);
			if(selected != null)
			{
				renderToolTip(scene.cursor.cursorX*32+32+8,scene.cursor.cursorY*32,ColorsTools.rgbToHex(NationsColors.getColor(NationsInfo.nationsColors[selected.nation])),selected.getToolTipText());
				//System.out.println("cursor over unit "+selected.name);
				return;
			}
		}
		//System.out.println("cursor over tile "+selectedTile.getTileName());
		InteractiveTile it = scene.world.getInteractiveTile(cx, cy);
		if(it != null)
		{
			renderToolTip(scene.cursor.cursorX*32+32+8,scene.cursor.cursorY*32,it.getTooltipColor(),it.getTooltip());
		}
		/*
		Tile selectedTile = Tile.getTileByID(scene.world.getTileAt(cx, cy));
		String tn = selectedTile.getTileName();
		if(selectedTile.canInteract())
			renderToolTip(scene.cursor.cursorX*32+32+8,scene.cursor.cursorY*32,"00FF00",new String[] {selectedTile.getTileName()});
		if(tn.equals("Town") || tn.equals("Village") || tn.equals("Farm"))
			renderToolTip(scene.cursor.cursorX*32+32+8,scene.cursor.cursorY*32,"00FF00",new String[] {selectedTile.getTileName()});*/
		//ObjectRenderer.renderTexturedRect(200,30,32,32, "misc/path");
	}
	
	private void renderUnitAttackRange(Unit u, int x, int y) {
		
	}

	public void renderToolTip(int x, int y, String color, String[] text)
	{
		int maxLen = 0;
		int nbLines = 0;
		for(String line : text)
		{
			maxLen = Math.max(maxLen, FontRenderer.getTextLengthUsingFont(32, line, BitmapFont.TINYFONTS));
			nbLines++;
		}
		ObjectRenderer.renderColoredRect(x+maxLen/2+2, y+28-(nbLines)*8, maxLen+4, nbLines*16+8, 0, color, 0.5f);
		nbLines = 0;
		for(String line : text)
		{
			FontRenderer.drawTextUsingSpecificFontHex(x, y-nbLines*16, 0, 32, line, BitmapFont.TINYFONTS, "FFFFFF", 1f);
			nbLines++;
		}
		ObjectRenderer.drawLine(x-2, y+32, x+maxLen+6, y+32, 2, 0, 0, 0, 1f);
		ObjectRenderer.drawLine(x-2, y-nbLines*16+24, x+maxLen+6,  y-nbLines*16+24, 2, 0, 0, 0, 1f);
		ObjectRenderer.drawLine(x-1, y+32, x-1,  y-nbLines*16+24, 2, 0, 0, 0, 1f);
		ObjectRenderer.drawLine(x+maxLen+5, y+32, x+maxLen+5, y-nbLines*16+24, 2, 0, 0, 0, 1f);
		//ObjectRenderer.drawLine(0, 0, 200,200, 2, 45,45,45, 0.5f);
		//ObjectRenderer.renderTexturedRectAlpha(x+maxLen/2+2, y+(nbLines)*8-4, maxLen+4, nbLines*16+8, "misc/greytex", 0.5f);
	}
}
