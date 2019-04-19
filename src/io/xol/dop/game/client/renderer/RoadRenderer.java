package io.xol.dop.game.client.renderer;

import io.xol.dop.game.tiles.Tile;

//(c) 2014 XolioWare Interactive

public class RoadRenderer {

	
	public static void renderRoadTypeTile(int x,int y,int id,WorldRenderer w)
	{
		String name = Tile.getTileByID(id).getTextureName();
		//Check for crossings
		if(w.getTileSecure(x, y-1) == Tile.ROAD.getId() && w.getTileSecure(x, y+1) == Tile.ROAD.getId() &&
				w.getTileSecure(x-1, y) == Tile.RAILWAY.getId() && w.getTileSecure(x+1, y) == Tile.RAILWAY.getId())
		{
			TileRenderer.renderTileWithName(x*32, y*32+32, 32, 32, "crossing_a_part2");
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, "crossing_a_part1");
			return;
		}
		if(w.getTileSecure(x, y-1) == Tile.RAILWAY.getId() && w.getTileSecure(x, y+1) == Tile.RAILWAY.getId() &&
				w.getTileSecure(x-1, y) == Tile.ROAD.getId() && w.getTileSecure(x+1, y) == Tile.ROAD.getId())
		{
			TileRenderer.renderTileWithName(x*32, y*32+32, 32, 32, "crossing_b_part2");
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, "crossing_b_part1");
			return;
		}
		
		//4
		if(hasToConnect(w.getTileSecure(x, y-1),id,w,x,y-1) && hasToConnect(w.getTileSecure(x, y+1),id,w,x,y+1) && hasToConnect(w.getTileSecure(x+1, y),id,w,x+1,y) && hasToConnect(w.getTileSecure(x-1, y),id,w,x-1,y))
		{
			//ObjectRenderer.renderRotatedTexturedRect(x * 32 + 16, y * 32 + 16,32, 32, "tiles/" + name+"_crossing", 0);
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_crossing");
			return;
		}
		//3
		if(hasToConnect(w.getTileSecure(x, y+1),id,w,x,y+1) && hasToConnect(w.getTileSecure(x+1, y),id,w,x+1,y) && hasToConnect(w.getTileSecure(x-1, y),id,w,x-1,y))
		{
			//ObjectRenderer.renderRotatedTexturedRect(x * 32 + 16, y * 32 + 16,32, 32, "tiles/" + name+"_threecrossing", 0);
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_threecrossing");
			return;
		}
		if(hasToConnect(w.getTileSecure(x, y-1),id,w,x,y-1) && hasToConnect(w.getTileSecure(x, y+1),id,w,x,y+1) && hasToConnect(w.getTileSecure(x+1, y),id,w,x+1,y))
		{
			//ObjectRenderer.renderRotatedTexturedRect(x * 32 + 16, y * 32 + 16,32, 32, "tiles/" + name+"_threecrossing_c", 0f);
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_threecrossing_c");
			return;
		}
		if(hasToConnect(w.getTileSecure(x, y-1),id,w,x,y-1) && hasToConnect(w.getTileSecure(x, y+1),id,w,x,y+1) && hasToConnect(w.getTileSecure(x-1, y),id,w,x-1,y))
		{
			//ObjectRenderer.renderRotatedTexturedRect(x * 32 + 16, y * 32 + 16,32, 32, "tiles/" + name+"_threecrossing_d", 0f);
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_threecrossing_d");
			return;
		}
		if(hasToConnect(w.getTileSecure(x, y-1),id,w,x,y-1) && hasToConnect(w.getTileSecure(x+1, y),id,w,x+1,y) && hasToConnect(w.getTileSecure(x-1, y),id,w,x-1,y))
		{
			//ObjectRenderer.renderRotatedTexturedRect(x * 32 + 16, y * 32 + 16,32, 32, "tiles/" + name+"_threecrossing_b", 0f);
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_threecrossing_b");
			return;
		}
		//2
		if(hasToConnect(w.getTileSecure(x, y+1),id,w,x,y+1) && hasToConnect(w.getTileSecure(x+1, y),id,w,x+1,y))
		{
			//ObjectRenderer.renderRotatedTexturedRect(x * 32 + 16, y * 32 + 16,32, 32, "tiles/" + name+"_corner_b", 0);
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_corner_b");
			return;
		}
		if(hasToConnect(w.getTileSecure(x, y+1),id,w,x,y+1) && hasToConnect(w.getTileSecure(x-1, y),id,w,x-1,y))
		{
			//ObjectRenderer.renderRotatedTexturedRect(x * 32 + 16, y * 32 + 16,32, 32, "tiles/" + name+"_corner_c", 0);
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_corner_c");
			return;
		}
		if(hasToConnect(w.getTileSecure(x, y-1),id,w,x,y-1) && hasToConnect(w.getTileSecure(x+1, y),id,w,x+1,y))
		{
			//ObjectRenderer.renderRotatedTexturedRect(x * 32 + 16, y * 32 + 16,32, 32, "tiles/" + name+"_corner", 0);
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_corner");
			return;
		}
		if(hasToConnect(w.getTileSecure(x, y-1),id,w,x,y-1) && hasToConnect(w.getTileSecure(x-1, y),id,w,x-1,y))
		{
			//ObjectRenderer.renderRotatedTexturedRect(x * 32 + 16, y * 32 + 16,32, 32, "tiles/" + name+"_corner_d", 0);
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_corner_d");
			return;
		}
		//lines
		if(hasToConnect(w.getTileSecure(x, y-1),id,w,x,y-1) || hasToConnect(w.getTileSecure(x, y+1),id,w,x,y+1))
		{
			//ObjectRenderer.renderRotatedTexturedRect(x * 32 + 16, y * 32 + 16,32, 32, "tiles/" + name, 90f);
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_b");
			return;
		}
		if(hasToConnect(w.getTileSecure(x-1, y),id,w,x-1,y) || hasToConnect(w.getTileSecure(x+1, y),id,w,x+1,y))
		{
			//ObjectRenderer.renderRotatedTexturedRect(x * 32 + 16, y * 32 + 16,32, 32, "tiles/" + name, 0f);
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"");
			return;
		}
		//ObjectRenderer.renderRotatedTexturedRect(x * 32 + 16, y * 32 + 16,32, 32, "tiles/" + name, 0f);
		TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"");
	
	}
	static boolean hasToConnect(int connect, int to,WorldRenderer w,int x,int y)
	{
		if(to == Tile.ROAD.getId())
		{
			if(connect == Tile.ROAD.getId())
				return true;
			if(connect == Tile.BRIDGE.getId())
				return true;
			if(connect == Tile.RAILWAY.getId())
			{
				if(w.getTileSecure(x, y-1) == Tile.ROAD.getId() && w.getTileSecure(x, y+1) == Tile.ROAD.getId() &&
						w.getTileSecure(x-1, y) == Tile.RAILWAY.getId() && w.getTileSecure(x+1, y) == Tile.RAILWAY.getId())
				{
					return true;
				}
				if(w.getTileSecure(x, y-1) == Tile.RAILWAY.getId() && w.getTileSecure(x, y+1) == Tile.RAILWAY.getId() &&
						w.getTileSecure(x-1, y) == Tile.ROAD.getId() && w.getTileSecure(x+1, y) == Tile.ROAD.getId())
				{
					return true;
				}
			}
		}
		else if(to == Tile.RAILWAY.getId())
		{
			if(connect == Tile.RAILWAY.getId())
				return true;
			if(connect == Tile.ROAD.getId())
			{
				if(w.getTileSecure(x, y-1) == Tile.ROAD.getId() && w.getTileSecure(x, y+1) == Tile.ROAD.getId() &&
						w.getTileSecure(x-1, y) == Tile.RAILWAY.getId() && w.getTileSecure(x+1, y) == Tile.RAILWAY.getId())
				{
					return true;
				}
				if(w.getTileSecure(x, y-1) == Tile.RAILWAY.getId() && w.getTileSecure(x, y+1) == Tile.RAILWAY.getId() &&
						w.getTileSecure(x-1, y) == Tile.ROAD.getId() && w.getTileSecure(x+1, y) == Tile.ROAD.getId())
				{
					return true;
				}
			}
		}
		return false;
	}
}
