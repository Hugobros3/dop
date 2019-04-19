package io.xol.dop.game.client.bits;

import org.lwjgl.input.Keyboard;

import io.xol.dop.game.client.FastConfig;
import io.xol.dop.game.client.scenes.GameScene;
import io.xol.engine.base.XolioWindow;

//(c) 2014 XolioWare Interactive

public class Camera {

	GameScene scene;
	public int cameraX = 0;
	public int cameraY = 0;
	int lastcameraX = 0;
	int lastcameraY = 0;
	boolean cameraMoved = true;
	
	public Camera(GameScene gameScene) {
		scene = gameScene;
	}
	
	public void update()
	{
		//camera movement
		lastcameraX = cameraX;
		lastcameraY = cameraY;
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
		if(scene.world != null)
		{
			int maxCamX = scene.world.width*32-XolioWindow.frameW/32+5;
			if(cameraX > maxCamX)
				cameraX = maxCamX;
			int maxCamY = scene.world.height*32-XolioWindow.frameH/32+5;
			if(cameraY > maxCamY)
				cameraY = maxCamY;
		}
		cameraMoved = (cameraMoved || lastcameraX != cameraX || (lastcameraY != cameraY));
	}
	
	public boolean moved()
	{
		boolean m = cameraMoved;
		cameraMoved = false;
		return m;
	}
}
