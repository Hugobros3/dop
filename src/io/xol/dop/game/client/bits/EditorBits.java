package io.xol.dop.game.client.bits;

//(c) 2014 XolioWare Interactive

import org.lwjgl.input.Mouse;

import io.xol.dop.game.World;
import io.xol.dop.game.client.FastConfig;
import io.xol.dop.game.client.scenes.GameScene;
import io.xol.dop.game.tiles.Tile;

public class EditorBits {

	GameScene scene;
	//for editor mode
	int brushType = 1;
	
	public EditorBits(GameScene s)
	{
		scene = s;
	}
	
	public void onWheel(int dx)
	{
		if(dx != 0)
		{
			// brush id selection
			int wheel = dx;
			if(wheel > 0 && brushType < Tile.tileTypesC-1)
				brushType++;
			if(wheel < 0 && brushType > 0)
				brushType--;
		}
	}
	
	public void fillTiles(World world,int x, int y, int id,int limit,int first) {
		//Dirty but simple fill routine
		if(x < 0 || y < 0)
			return;
		if(x > world.width*32-1 || y > world.height*32-1)
			return;
		if(world.getTileAt(x, y) == first)
		{
			if(world.sectorExists(x/32, y/32))
			{
				world.getSector(x, y).setDataAt(x, y, id);
			}
			if(x <= 0 || y <= 0)
				return;
			if(x >= world.width*32-1 || y >= world.height*32-1)
				return;
			fillTiles(world,x-1,y,id,limit-1,first);
			fillTiles(world,x+1,y,id,limit-1,first);
			fillTiles(world,x,y+1,id,limit-1,first);
			fillTiles(world,x,y-1,id,limit-1,first);
		}
	}

	public void update()
	{
		if (Mouse.isButtonDown(0))
			scene.world.setTileAt(scene.cursor.cursorX+scene.cam.cameraX, scene.cursor.cursorY+scene.cam.cameraY, brushType);
		if (Mouse.isButtonDown(1))
			brushType = scene.world.getTileAt(scene.cursor.cursorX+scene.cam.cameraX, scene.cursor.cursorY+scene.cam.cameraY); 
	}
	
	public boolean handle(int button, int posx, int posy) {
		//System.out.println("inside good zone");
		return true;
	}

	public boolean onKeyPress(int k) {
		if(k == FastConfig.keyFill)
		{
			
		}
		return false;
	}
}
