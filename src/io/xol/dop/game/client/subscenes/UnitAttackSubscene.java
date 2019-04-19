package io.xol.dop.game.client.subscenes;

//(c) 2014 XolioWare Interactive

import io.xol.dop.game.client.Client;
import io.xol.dop.game.client.FastConfig;
import io.xol.dop.game.client.scenes.GameScene;
import io.xol.engine.base.AnimationsHelper;
import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.scene.SubScene;

public class UnitAttackSubscene extends SubScene {
	UnitActionSubscene parent;
	int[] attackablePlaces;
	int selectedTarget = 0;
	
	public UnitAttackSubscene(UnitActionSubscene parent) {
		super(parent.p);
		this.parent = parent;
		attackablePlaces = parent.unit.getReallyAttackableCoords(parent.targetX,parent.targetY);
		if(attackablePlaces == null)
			parent.p.setSubscene(parent);
	}

	public void update()
	{
		//Background
		if(parent.attackablePlaces != null)
		{
			for(int i = 0; i < parent.attackablePlaces.length/2; i++)
			{
				//System.out.println(i);
				ObjectRenderer.renderTexturedRectAlpha(parent.attackablePlaces[i*2]*32-parent.p.cam.cameraX*32+16, parent.attackablePlaces[i*2+1]*32+16-parent.p.cam.cameraY*32,32,32,AnimationsHelper.animatedTextureName("misc/aiming", 3, 250, true),0.5f);
			}
		}
		//Cursors
		if(attackablePlaces != null)
		{
			for(int i = 0; i < attackablePlaces.length/2; i++)
			{
				//System.out.println(i);
				ObjectRenderer.renderTexturedRectAlpha(attackablePlaces[i*2]*32-parent.p.cam.cameraX*32+24, attackablePlaces[i*2+1]*32+8-parent.p.cam.cameraY*32,64,64, "misc/target",(selectedTarget == i ? 0.8f : 0.5f));
			}
		}
		super.update();
	}
	
	public boolean onKeyPress(int k) {
		if(k == FastConfig.keyDown)
		{
			selectedTarget++;
			if(selectedTarget >= attackablePlaces.length/2)
				selectedTarget = 0;
		}
		if(k == FastConfig.keyUp)
		{
			selectedTarget--;
			if(selectedTarget < 0)
				selectedTarget = 0;//attackablePlaces.length/2-1;
		}
		if(k == FastConfig.keyStart && attackablePlaces != null)
		{
			send();
			return true;
		}
		if(k == FastConfig.keyBack)
		{
			parent.p.setSubscene(parent);
			return true;
		}
		return false;
	}
	
	private void send() {
		String moveUnitMsg = "gameMode/moveUnit:"+parent.unit.posX+":"+parent.unit.posY+":"+parent.targetX+":"+parent.targetY;
		Client.connect.send(moveUnitMsg);
		String attackMsg = "gameMode/attackUnit:"+parent.unit.posX+":"+parent.unit.posY+":"+attackablePlaces[selectedTarget*2]+":"+attackablePlaces[selectedTarget*2+1];
		Client.connect.send(attackMsg);
		//System.out.println(attackMsg);
		GameScene gameScene = (GameScene)parent.p;
		gameScene.control.unitSelected = false;
		gameScene.control.acceptablePath = null;
		parent.p.setSubscene(null);
	}

	public boolean onClick(int posx,int posy,int button)
	{
		if(attackablePlaces != null)
		{
			for(int i = 0; i < attackablePlaces.length/2; i++)
			{
				if(posx > attackablePlaces[i*2]*32-parent.p.cam.cameraX*32 && posx < attackablePlaces[i*2]*32-parent.p.cam.cameraX*32+32)
				{
					if(posy > attackablePlaces[i*2+1]*32+8-parent.p.cam.cameraY*32 && posy < attackablePlaces[i*2+1]*32+8-parent.p.cam.cameraY*32+32)
					{
						//System.out.println("in !");
						selectedTarget = i;
						send();
						return true;
					}
				}
				//System.out.println(posx+":"+posy+" - "+(attackablePlaces[i*2]*32-parent.p.cam.cameraX*32)+":"+(attackablePlaces[i*2+1]*32+8-parent.p.cam.cameraY*32));
				//ObjectRenderer.renderTexturedRectAlpha(attackablePlaces[i*2]*32-parent.p.cam.cameraX*32+24, attackablePlaces[i*2+1]*32+8-parent.p.cam.cameraY*32,64,64, "misc/target",(selectedTarget == i ? 0.8f : 0.5f));
			}
		}
		return false;
	}
}
