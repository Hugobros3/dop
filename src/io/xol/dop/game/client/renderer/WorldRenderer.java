package io.xol.dop.game.client.renderer;

import io.xol.dop.game.Sector;
import io.xol.dop.game.World;
import io.xol.dop.game.tiles.Tile;
import io.xol.engine.base.AnimationsHelper;
import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.base.TexturesHandler;
import io.xol.engine.base.XolioWindow;

import org.lwjgl.opengl.GL11;

//(c) 2014 XolioWare Interactive

public class WorldRenderer {

	World world;
	int displayList = 0;

	public static int rectCount = 0;
	public static int renderTicksCounter = 0;
	
	public WorldRenderer(World world) {
		this.world = world;
		displayList = GL11.glGenLists(1);
	}

	public void updateRender() {
		//
		//System.out.println("Updating world renderer.");
		rectCount = 0;
		GL11.glPushMatrix();
		// ObjectRenderer.renderTexturedRotatedRect(XolioWindow.frameW/2,
		// XolioWindow.frameH/2-220, 100, 100, 35,0,0,1,1,"test");
		for (int i = 0; i < world.width; i++) {
			for (int y = 0; y < world.height; y++) {
					updateSector(i,y);
				
			}
		}
		GL11.glNewList(displayList, GL11.GL_COMPILE);
		//WorldRenderer.rectCount = 0;
		for(Sector[] secarray : world.sectors)
		{
			for(Sector sec : secarray)
			{
				if(sec != null)
					sec.render();
			}
		}
		GL11.glEndList();
		GL11.glPopMatrix();
		//end
	}
	//Checks if sector's display list should be updated ( re-render the sector )
	void updateSector(int secx, int secy)
	{
		if(world.sectorExists(secx, secy))
		{
			Sector sector = world.getSector(secx*32, secy*32);
			//System.out.println("Updating sector"+secx+":"+secy+sector.toString());
			if(sector.needsRenderUpdate())
			{
				//System.out.println("Sector"+secx+":"+secy+" needs an update.");
				//System.out.println("need render upt");
				if(sector.displayL == -1)
				{
					sector.displayL = GL11.glGenLists(1);
					//System.out.println("New DL gen: "+sector.displayL);
				}

				GL11.glPushMatrix();
				GL11.glNewList(sector.displayL, GL11.GL_COMPILE);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,GL11.GL_NEAREST);
				TileRenderer.bindTexture();
				GL11.glBegin(GL11.GL_TRIANGLES);
				for(int i = 0; i < 32;i++)
				{
					for(int j = 31;j >= 0;j--)
					{
						
						//int id = getTileSecure(secx*32+i, secy*32+j);
						//System.out.println("Updating sector"+secx+":"+secy+"/"+id);
						renderTile(world,secx*32+i, secy*32+j);
						//renderGroundTile(secx*32+i, secy*32+j);
					}
				}
				GL11.glEnd();
				GL11.glEndList();
				GL11.glPopMatrix();
				sector.updated();
			}
		}
	}
	//safety procedures for not generating sectors when you don't need them ( you just want do render that world ! )
	public int getTileSecure(int x, int y)
	{
		int secx = x/32;
		int secy = y/32;
		if(world.sectorExists(secx, secy))
		{
			return world.getTileAt(x, y);
		}
		return 0;
	}
	//render dat pretty tile :o
	public void renderTile(World w,int x, int y) {
		
		int id = getTileSecure(x, y);
		int rt = Tile.getTileByID(id).getRenderType();
		TileRenderer.tileColor = w.getColorForTile(x,y);
		if(id != 0)
		{
			//TileRenderer.bindTexture();
			//System.out.println("Updating sector"+id+" "+x);
			if(Tile.getTileByID(id).getType().hasGroundBorders())
				TileRenderer.renderTileWithName(x*32, y*32, 32, 32, "grass");//ObjectRenderer.renderTexturedRect(x * 32 + 16, y * 32 + 16, 32, 32,"tiles/grass");
			if(rt == 1)
				TileRenderer.renderTileWithName(x*32, y*32, 32, 32, Tile.getTileByID(id).getTextureName());//System.out.print("nope");//ObjectRenderer.renderTexturedRect(x * 32 + 16, y * 32 + 16, 32, 32,"tiles/"+Tile.getTileByID(id).getTextureName());
			else if(rt == 2)
				RoadRenderer.renderRoadTypeTile(x, y, id, this);
			else if(rt == 3)
				BridgeRenderer.renderBridgeTypeTile(x, y, id, this);
			else if(rt == 4)
				BeachRenderer.renderBeachTypeTile(x, y, id, this);
			else if(rt == 5)
				BorderableTilesRenderer.renderBorderableTilesTypeTile(x, y, id, this);
			else if(rt == 6)
				ForestRenderer.renderForestTypeTile(x, y, id, this);
			else if(rt == 7)
				MountainRenderer.renderMountainTypeTile(x, y, id, this);
			else if(rt == 8)
			{
				//TODO : Code cs
				BuildingRenderer.renderBuildingTypeTile(x, y, id, this,this.world.getInteractiveTile(x,y));
			}
		}
		else
		{
			if(checkNearbyTiles(x,y))
			{
			ShoreRenderer.renderShoreTypeTile(x, y, id, this,"grass");
			}
		}
	}
	//Dirty method for checking nearby tiles
	private boolean checkNearbyTiles(int x, int y) {
		if(getTileSecure(x,y+1) != 0)
			return true;
		if(getTileSecure(x,y-1) != 0)
			return true;
		if(getTileSecure(x+1,y) != 0)
			return true;
		if(getTileSecure(x-1,y) != 0)
			return true;
		//
		if(getTileSecure(x+1,y+1) != 0)
			return true;
		if(getTileSecure(x+1,y-1) != 0)
			return true;
		if(getTileSecure(x+1,y+1) != 0)
			return true;
		if(getTileSecure(x-1,y+1) != 0)
			return true;
		//
		if(getTileSecure(x-1,y+1) != 0)
			return true;
		if(getTileSecure(x-1,y-1) != 0)
			return true;
		if(getTileSecure(x+1,y-1) != 0)
			return true;
		if(getTileSecure(x-1,y-1) != 0)
			return true;
		return false;
	}
	//Just calling the display lists and moving the camera
	public void render(int camx, int camy) {
		renderTicksCounter += 1;
	    renderTicksCounter %= 256;
	    float color[] = world.getColorForWorld();
	    ObjectRenderer.renderTexturedRotatedRectRVBA(XolioWindow.frameW / 2,XolioWindow.frameH / 2, XolioWindow.frameW, XolioWindow.frameH,0f,0f, 0f,
	    		XolioWindow.frameW / 32f, XolioWindow.frameH / 32f,
	    		AnimationsHelper.animatedTextureName("tiles/ocean", 3, 200,true)
	    		, color[0], color[1], color[2], 1);
	    
	    //ObjectRenderer.renderTexturedRect(XolioWindow.frameW / 2,XolioWindow.frameH / 2, XolioWindow.frameW, XolioWindow.frameH,0f, 0f, XolioWindow.frameW / 2, XolioWindow.frameH / 2, 16f,AnimationsHelper.animatedTextureName("tiles/ocean", 3, 200,true));
		GL11.glPushMatrix();
		GL11.glTranslated(-camx*32, -camy*32, 0);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,GL11.GL_NEAREST);
		for (int i = 0; i < world.width; i++) {
			for (int y = world.height; y >= 0; y--) {
				if((i+1)*32 >= camx && (y+1)*32 >= camy && (i)*32 <= camx+XolioWindow.frameW/32 && (y)*32 <= camy+XolioWindow.frameH/32)
				{
					if(world.sectorExists(i, y))
					{
						//System.out.println("mlol");
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,GL11.GL_NEAREST);
						Sector sector = world.getSector(i*32, y*32);
						sector.render();
						sector.renderEntities();
					}
				}
			}
		}
		renderBorders();
		//GL11.glCallList(displayList);
		GL11.glPopMatrix();
	}
	//Renders a clean black mask on borders.
	private void renderBorders()
	{
		TexturesHandler.bindTexture("./res/textures/misc/blacktex.png");
		//GL11.glPushMatrix();
		// draw quad
		
		int basewidth = 32*16*2;
		
		int width = world.width*32*32;
		int height = world.height*32*32;
		//horizontal
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(0, 1);
		GL11.glVertex2f(0, 0+height+basewidth);
		GL11.glTexCoord2d(1, 1);
		GL11.glVertex2f(0, 0+height);
		GL11.glTexCoord2d(1, 0);
		GL11.glVertex2f(width+basewidth, 0+height);
		GL11.glTexCoord2d(0, 0);
		GL11.glVertex2f(width+basewidth, basewidth+height);
		GL11.glEnd();
		//vertical
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(0, 1);
		GL11.glVertex2f(width+0, 0);
		GL11.glTexCoord2d(1, 1);
		GL11.glVertex2f(width+basewidth, 0);
		GL11.glTexCoord2d(1, 0);
		GL11.glVertex2f(width+basewidth, height+basewidth);
		GL11.glTexCoord2d(0, 0);
		GL11.glVertex2f(width+0, height+basewidth);
		GL11.glEnd();
		
		//ObjectRenderer.renderTexturedRect(posx*32, posy*32, 32*16, 32*16, "misc/blacktex");
	}
}
