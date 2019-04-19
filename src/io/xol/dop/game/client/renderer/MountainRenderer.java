package io.xol.dop.game.client.renderer;

//(c) 2014 XolioWare Interactive

import io.xol.dop.game.client.FastConfig;
import io.xol.dop.game.tiles.Tile;

public class MountainRenderer {

	public static void renderMountainTypeTile(int x, int y, int id, WorldRenderer w) {
		String name = Tile.getTileByID(id).getTextureName();
		
		if(FastConfig.renderDenseTiles)
		{
			if(w.getTileSecure(x+1, y) == id)
			{
				TileRenderer.renderTileWithName(x*32+16, y*32+8, 32, 32, name);
			}
			else
				TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name);
			
			if(w.getTileSecure(x-1, y) == id)
			{
				TileRenderer.renderTileWithName(x*32-16, y*32+8, 32, 32, name);
				TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name);
				TileRenderer.renderTileWithName(x*32-32, y*32, 32, 32, name);
			}
		}
		else
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name);
	}
}