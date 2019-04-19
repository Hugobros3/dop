package io.xol.dop.game.client.renderer;

import io.xol.dop.game.tiles.Tile;

//(c) 2014 XolioWare Interactive

public class BeachRenderer {

	public static void renderBeachTypeTile(int x,int y,int id,WorldRenderer w)
	{
		String name = "beach";
		//Tile.getTileByID(Tile.getTileByID(w.getTileSecure(x+1, y-1)).getType().equals(TileType.)
		//corners
		if(Tile.getTileByID(w.getTileSecure(x+1, y-1)).getType().hasGroundBorders())
		{
			//ObjectRenderer.renderTexturedRect(x * 32 + 16, y * 32 + 16, 32, 32,"tiles/grass"+"_cornernw");
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_cornernw");
		}
		if(Tile.getTileByID(w.getTileSecure(x-1, y-1)).getType().hasGroundBorders())
		{
			//ObjectRenderer.renderTexturedRect(x * 32 + 16, y * 32 + 16, 32, 32,"tiles/grass"+"_cornerne");
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_cornerne");
		}
		if(Tile.getTileByID(w.getTileSecure(x+1, y+1)).getType().hasGroundBorders())
		{
			//ObjectRenderer.renderTexturedRect(x * 32 + 16, y * 32 + 16, 32, 32,"tiles/grass"+"_cornersw");
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_cornersw");
		}
		if(Tile.getTileByID(w.getTileSecure(x-1, y+1)).getType().hasGroundBorders())
		{
			//ObjectRenderer.renderTexturedRect(x * 32 + 16, y * 32 + 16, 32, 32,"tiles/grass"+"_cornerse");
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_cornerse");
		}
		//hole
		if(Tile.getTileByID(w.getTileSecure(x, y+1)).getType().hasGroundBorders() && Tile.getTileByID(w.getTileSecure(x, y-1)).getType().hasGroundBorders() && Tile.getTileByID(w.getTileSecure(x+1, y)).getType().hasGroundBorders() && Tile.getTileByID(w.getTileSecure(x-1, y)).getType().hasGroundBorders())
		{
			//ObjectRenderer.renderTexturedRect(x * 32 + 16, y * 32 + 16, 32, 32,"tiles/grass"+"_hole");
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_hole");
			return;
		}
		//ends
		if(Tile.getTileByID(w.getTileSecure(x, y-1)).getType().hasGroundBorders() && Tile.getTileByID(w.getTileSecure(x+1, y)).getType().hasGroundBorders() && Tile.getTileByID(w.getTileSecure(x-1, y)).getType().hasGroundBorders())
		{
			//ObjectRenderer.renderTexturedRect(x * 32 + 16, y * 32 + 16, 32, 32,"tiles/grass"+"_endn");
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_endn");
			return;
		}
		else if(Tile.getTileByID(w.getTileSecure(x, y+1)).getType().hasGroundBorders() && Tile.getTileByID(w.getTileSecure(x+1, y)).getType().hasGroundBorders() && Tile.getTileByID(w.getTileSecure(x-1, y)).getType().hasGroundBorders())
		{
			//ObjectRenderer.renderTexturedRect(x * 32 + 16, y * 32 + 16, 32, 32,"tiles/grass"+"_ends");
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_ends");
			return;
		}
		else if(Tile.getTileByID(w.getTileSecure(x-1, y)).getType().hasGroundBorders() && Tile.getTileByID(w.getTileSecure(x, y+1)).getType().hasGroundBorders() && Tile.getTileByID(w.getTileSecure(x, y-1)).getType().hasGroundBorders())
		{
			//ObjectRenderer.renderTexturedRect(x * 32 + 16, y * 32 + 16, 32, 32,"tiles/grass"+"_ende");
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_ende");
			return;
		}
		else if(Tile.getTileByID(w.getTileSecure(x+1, y)).getType().hasGroundBorders() && Tile.getTileByID(w.getTileSecure(x, y+1)).getType().hasGroundBorders() && Tile.getTileByID(w.getTileSecure(x, y-1)).getType().hasGroundBorders())
		{
			//ObjectRenderer.renderTexturedRect(x * 32 + 16, y * 32 + 16, 32, 32,"tiles/grass"+"_endw");
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_endw");
			return;
		}
		//internal corners
		if(Tile.getTileByID(w.getTileSecure(x, y+1)).getType().hasGroundBorders() && Tile.getTileByID(w.getTileSecure(x+1, y)).getType().hasGroundBorders())
		{
			//ObjectRenderer.renderTexturedRect(x * 32 + 16, y * 32 + 16, 32, 32,"tiles/grass"+"_intcornernw");
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_intcornernw");
			return;
		}
		if(Tile.getTileByID(w.getTileSecure(x, y+1)).getType().hasGroundBorders() && Tile.getTileByID(w.getTileSecure(x-1, y)).getType().hasGroundBorders())
		{
			//ObjectRenderer.renderTexturedRect(x * 32 + 16, y * 32 + 16, 32, 32,"tiles/grass"+"_intcornerne");
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_intcornerne");
			return;
		}
		if(Tile.getTileByID(w.getTileSecure(x, y-1)).getType().hasGroundBorders() && Tile.getTileByID(w.getTileSecure(x-1, y)).getType().hasGroundBorders())
		{
			//ObjectRenderer.renderTexturedRect(x * 32 + 16, y * 32 + 16, 32, 32,"tiles/grass"+"_intcornersw");
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_intcornersw");
			return;
		}
		if(Tile.getTileByID(w.getTileSecure(x, y-1)).getType().hasGroundBorders() && Tile.getTileByID(w.getTileSecure(x+1, y)).getType().hasGroundBorders())
		{
			//ObjectRenderer.renderTexturedRect(x * 32 + 16, y * 32 + 16, 32, 32,"tiles/grass"+"_intcornerse");
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_intcornerse");
			return;
		}
		//lines
		if(Tile.getTileByID(w.getTileSecure(x, y+1)).getType().hasGroundBorders())
		{
			//ObjectRenderer.renderTexturedRect(x * 32 + 16, y * 32 + 16, 32, 32,"tiles/grass"+"_linesouth");
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_linesouth");
		}
		if(Tile.getTileByID(w.getTileSecure(x, y-1)).getType().hasGroundBorders())
		{
			//ObjectRenderer.renderTexturedRect(x * 32 + 16, y * 32 + 16, 32, 32,"tiles/grass"+"_linenorth");
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_linenorth");
		}
		if(Tile.getTileByID(w.getTileSecure(x+1, y)).getType().hasGroundBorders())
		{
			//ObjectRenderer.renderTexturedRect(x * 32 + 16, y * 32 + 16, 32, 32,"tiles/grass"+"_linewest");
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_linewest");
		}
		if(Tile.getTileByID(w.getTileSecure(x-1, y)).getType().hasGroundBorders())
		{
			//ObjectRenderer.renderTexturedRect(x * 32 + 16, y * 32 + 16, 32, 32,"tiles/grass"+"_lineeast");
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_lineeast");
		}
		
		
		if(!Tile.getTileByID(w.getTileSecure(x+1, y-1)).getType().hasGroundBorders()
				&& !Tile.getTileByID(w.getTileSecure(x-1, y-1)).getType().hasGroundBorders()
				&& !Tile.getTileByID(w.getTileSecure(x+1, y+1)).getType().hasGroundBorders()
				&& !Tile.getTileByID(w.getTileSecure(x-1, y+1)).getType().hasGroundBorders()
				&& !Tile.getTileByID(w.getTileSecure(x+1, y)).getType().hasGroundBorders()
				&& !Tile.getTileByID(w.getTileSecure(x-1, y)).getType().hasGroundBorders()
				&& !Tile.getTileByID(w.getTileSecure(x, y+1)).getType().hasGroundBorders()
				&& !Tile.getTileByID(w.getTileSecure(x, y-1)).getType().hasGroundBorders())
		{
			//ObjectRenderer.renderTexturedRect(x * 32 + 16, y * 32 + 16, 32, 32,"tiles/grass"+"_cornernw");
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"");
		}
	}
}
