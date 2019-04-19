package io.xol.dop.game.client.renderer;

//(c) 2014 XolioWare Interactive

import io.xol.dop.game.tiles.InteractiveTile;
import io.xol.dop.game.tiles.Tile;
import io.xol.dop.game.tiles.TileConquerable;

public class BuildingRenderer {

	public static void renderBuildingTypeTile(int x, int y, int id,WorldRenderer worldRenderer, InteractiveTile it) {
		
		String name = Tile.getTileByID(id).getTextureName();
		//System.out.println("lol"+name);
		if(it instanceof TileConquerable)
		{
			TileConquerable tt = (TileConquerable)it;
			if(tt.nation != -1)
			{
				float[] color = tt.getColor();
				/*if(color[0] != 1f || color[1] != 1f || color[2] != 1f)
					System.out.println("ftg"+color[0]+"/"+color[1]+"/"+color[2]+"/");*/
				TileRenderer.renderTileWithNameAndColor(x*32, y*32, 32, 32, name, color);
			}
			else
				TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name);
		}
		else
			TileRenderer.renderTileWithName(x*32, y*32, 32, 32, name);
	}
}
