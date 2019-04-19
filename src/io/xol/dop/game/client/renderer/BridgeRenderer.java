package io.xol.dop.game.client.renderer;

import io.xol.dop.game.tiles.Tile;

//(c) 2014 XolioWare Interactive

public class BridgeRenderer {
	public static void renderBridgeTypeTile(int x,int y,int id,WorldRenderer w)
	{

		ShoreRenderer.renderShoreTypeTile(x, y, id, w,"grass");
		String name = Tile.getTileByID(id).getTextureName();
		//lines
		if(w.getTileSecure(x, y-1) == id || w.getTileSecure(x, y+1) == id)
		{
			//ObjectRenderer.renderRotatedTexturedRect(x * 32 + 16, y * 32 + 16,32, 32, "tiles/" + name+"_b" , 0f);
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name+"_b");
			return;
		}
		if(w.getTileSecure(x-1, y) == id || w.getTileSecure(x+1, y) == id)
		{
			//ObjectRenderer.renderRotatedTexturedRect(x * 32 + 16, y * 32 + 16,32, 32, "tiles/" + name+"", 0f);
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name);
			return;
		}
	}
}
