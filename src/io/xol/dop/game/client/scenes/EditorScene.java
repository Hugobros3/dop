package io.xol.dop.game.client.scenes;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import io.xol.dop.game.World;
import io.xol.dop.game.client.Client;
import io.xol.dop.game.client.FastConfig;
import io.xol.dop.game.client.subscenes.SelectUnitSubscene;
import io.xol.dop.game.tiles.Tile;
import io.xol.engine.base.AnimationsHelper;
import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.base.XolioWindow;
import io.xol.engine.base.font.FontRenderer;
import io.xol.engine.gui.Button;
import io.xol.engine.scene.Scene;

//(c) 2014 XolioWare Interactive

public class EditorScene extends Scene {
	World world;

	int cameraX = 0;
	int cameraY = 0;
	
	int cursorX = 0;
	int cursorY = 0;
	
	int brushType = 1;
	
	public static boolean hasToSave = false;
	public static boolean hasToExit = false;
	
	public EditorScene(XolioWindow XolioWindow) {
		this(XolioWindow, new World(10, 10, "default",true));
	}

	public EditorScene(XolioWindow xolioWindow, World world) {
		super(xolioWindow);
		this.world = world;
		//New world button
		buttons.add(new Button(16, XolioWindow.frameH - 48, 32, 32, 0,"gui/editorbuttons", new Runnable() {	public void run() {
					}
				}));
		//Load world button
		buttons.add(new Button(64, XolioWindow.frameH - 48, 32, 32, 16,"gui/editorbuttons", new Runnable() { public void run() {
					}
				}));
		//Save button
		buttons.add(new Button(112, XolioWindow.frameH - 48, 32, 32, 32,"gui/editorbuttons", new Runnable() { public void run() {
						EditorScene.hasToSave = true;
					}
				}));
		//Options button
		buttons.add(new Button(160, XolioWindow.frameH - 48, 32, 32, 48,"gui/editorbuttons", new Runnable() {public void run() {
						EditorScene.hasToExit = true;
					}}));
	}

	public void update() {
		//save
		if(hasToSave)
		{
			hasToSave= false;
			world.saveWorld();
		}
		if(hasToExit)
		{
			hasToExit = false;
			eng.changeScene(new LevelEditorSelectionScene(eng,0));
		}
		//resize handle
		if(resized)
		{
			for(Button b : buttons)
			{
				b.posy = XolioWindow.frameH - 48;
			}
		}
		// cursor update
		cursorX = (Mouse.getX()) / 32;
		cursorY = (Mouse.getY()) / 32;
		int maxCursorY = (XolioWindow.frameH+5)/32-3;
		// map painting
		if (cursorY > maxCursorY)
			cursorY = maxCursorY;
		else {
			if (Mouse.isButtonDown(0))
				world.setTileAt(cursorX+cameraX, cursorY+cameraY, brushType);
			if (Mouse.isButtonDown(1))
				brushType = world.getTileAt(cursorX+cameraX, cursorY+cameraY); //Handle dat security !
		}
		render();
		//camera movement
		if(Keyboard.isKeyDown(FastConfig.keyUp))
		{
			cameraY++;
		}
		if(Keyboard.isKeyDown(FastConfig.keyDown))
		{
			cameraY--;
		}
		if(Keyboard.isKeyDown(FastConfig.keyLeft))
		{
			cameraX--;
		}
		if(Keyboard.isKeyDown(FastConfig.keyRight))
		{
			cameraX++;
		}
		if(cameraX < 0)
			cameraX = 0;
		if(cameraY < 0)
			cameraY = 0;
		int maxCamX = world.width*32-XolioWindow.frameW/32+5;
		if(cameraX > maxCamX)
			cameraX = maxCamX;
		int maxCamY = world.height*32-XolioWindow.frameH/32+5;
		if(cameraY > maxCamY)
			cameraY = maxCamY;
		
		super.update();
	}

	private void render() { // Moved all gui/world rendering shit here
		// background
		ObjectRenderer.renderTexturedRect(XolioWindow.frameW / 2,XolioWindow.frameH / 2, XolioWindow.frameW, XolioWindow.frameH,0f, 0f, XolioWindow.frameW / 2, XolioWindow.frameH / 2, 16f,AnimationsHelper.animatedTextureName("tiles/ocean", 3, 200,true));
		// world
		if(world.markClean())
			world.renderer.updateRender();
		world.renderer.render(cameraX, cameraY);
		// gui general
		ObjectRenderer.renderTexturedRect(XolioWindow.frameW / 2,XolioWindow.frameH - 32, XolioWindow.frameW, 64, 0f, 32f,XolioWindow.frameW / 2, 64f, 64f, "gui/editorbuttons");
		FontRenderer.drawText(220, XolioWindow.frameH - 24, 0, 12, "World:"+ world.name + " (" + world.width + "x" + world.height + ")");
		// cursor
		ObjectRenderer.renderTexturedRect(cursorX * 32 + 16, cursorY * 32 + 16,32 * 2, 32 * 2, AnimationsHelper.animatedTextureName("misc/cursor", 3, 100, true));
		FontRenderer.drawText(0, 0, 0, 12,"cameraX:" + cameraX + ", cameraY:" + cameraY + ", cx :"+ (cursorX+cameraX) + ", cy:" + (cursorY+cameraY)+" fps : "+XolioWindow.getFPS()/*+" rects : "+WorldRenderer.rectCount*/);
		//curent tile selected
		ObjectRenderer.renderTexturedRect(XolioWindow.frameW - 32, XolioWindow.frameH - 32, 32,32, "tiles/"+Tile.getTileByID(brushType).getTextureName());
		//System.out.println(brushType);
	}

	private void fillTiles(int x, int y, int id,int limit,int first) {
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
			fillTiles(x-1,y,id,limit-1,first);
			fillTiles(x+1,y,id,limit-1,first);
			fillTiles(x,y+1,id,limit-1,first);
			fillTiles(x,y-1,id,limit-1,first);
		}
	}
	
	//Handle mouse wheel
	public boolean onScroll(int dx)
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
		return true;
	}
	//Handle direct keys access
	public boolean onKeyPress(int k)
	{
		if(subscene != null)
			return subscene.onKeyPress(k);
		//Fill
		if(k == Client.clientConfig.getIntProp("FILLKEY", "33"))
		{
			fillTiles(cursorX+cameraX, cursorY+cameraY,brushType,10,world.getTileAt(cursorX+cameraX, cursorY+cameraY));
			world.markDirty();
		}
		else if(k == 22 /*U*/)
			this.subscene = new SelectUnitSubscene(this);
		return false;
	}
}
