package io.xol.dop.game.client.subscenes;

//(c) 2014 XolioWare Interactive

import org.lwjgl.input.Mouse;

import io.xol.dop.game.client.Client;
import io.xol.dop.game.client.FastConfig;
import io.xol.dop.game.client.scenes.GameScene;
import io.xol.dop.game.tiles.InteractiveTile;
import io.xol.dop.game.tiles.Tile;
import io.xol.dop.game.tiles.TileConquerable;
import io.xol.dop.game.units.Unit;
import io.xol.engine.base.AnimationsHelper;
import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.base.XolioWindow;
import io.xol.engine.base.font.BitmapFont;
import io.xol.engine.base.font.FontRenderer;
import io.xol.engine.scene.SubScene;

public class UnitActionSubscene extends SubScene{

	Unit unit;
	Tile tile;
	
	int posX;
	int posY;
	
	String[] actions = {"move","attack","cancel"};
	int selected = 0;
	
	boolean canMove;
	boolean canAttack;
	
	int targetX;
	int targetY;
	
	boolean mouseOver = false;
	
	GameScene p;
	//temp
	int[] attackablePlaces;
	
	public UnitActionSubscene(GameScene p, Unit u, Tile t, int x, int y) {
		super(p);
		this.p = p;
		
		unit = u;
		tile = t;
		
		targetX = x+p.cam.cameraX;
		targetY = y+p.cam.cameraY;
				
		this.canMove = (u.posX != x || u.posY != y);
		this.canAttack = u.getReallyAttackableCoords(targetX,targetY) != null;
		
		posX = x*32-16;
		posY = y*32+24;
		
		// custom interactive-tile shit
		InteractiveTile it = u.world.getInteractiveTile(targetX, targetY);
		if(it != null)
		{
			if(it instanceof TileConquerable)
			{
				TileConquerable conq = (TileConquerable)it;
				if(conq.isConquerable(u))
					actions = new String[]{"move","attack","conquer","cancel"};
			}
		}
		
		//temp
		attackablePlaces = unit.getAttackableCoords(targetX,targetY);
	}

	public void update()
	{
		//temp
		if(attackablePlaces != null)
		{
			for(int i = 0; i < attackablePlaces.length/2; i++)
			{
				//System.out.println(i);
				ObjectRenderer.renderTexturedRectAlpha(attackablePlaces[i*2]*32-p.cam.cameraX*32+16, attackablePlaces[i*2+1]*32+16-p.cam.cameraY*32,32,32,AnimationsHelper.animatedTextureName("misc/aiming", 3, 250, true),0.5f);
			}
		}
		//addedAction = false;
		mouseOver = false;
		int i = 0;
		int maxSize = 0;
		for(String action : actions)
		{
			int size = FontRenderer.getTextLengthUsingFont(32, action, BitmapFont.SMALLFONTS);
			if(size > maxSize)
				maxSize = size;
		}
		int decX = 0;
		int decY = 0;
		int j = 0;
		int height = actions.length;
		maxSize+=48;
		
		if(posX+maxSize > XolioWindow.frameW)
			decX-=maxSize;
		if(posY-height*34 < 0)
			decY+=height*34;
		
		if(Mouse.getX() > posX+decX+16 && Mouse.getX() < posX+decX+maxSize+16 && Mouse.getY() > posY+decY-height*34+16 && Mouse.getY() < posY+decY+16)
		{
			int lol = ((posY+decY+16)-Mouse.getY())/34;
			//System.out.println(lol);
			mouseOver = true;
			selected = lol;
		}
		ObjectRenderer.renderColoredRect(posX+decX+maxSize/2+16, posY+decY-height*17+17,maxSize , height*34, 0, "FFD168", 1f); //B7954B
		
		ObjectRenderer.drawLine(posX+decX+16-2, posY+decY+16+2, posX+decX+maxSize+16+2, posY+decY+16+2, 2f, "705B2E", 1);
		ObjectRenderer.drawLine(posX+decX+16-2, posY+decY-height*34+16, posX+decX+maxSize+16+2, posY+decY-height*34+16, 2f, "705B2E", 1);
		
		ObjectRenderer.drawLine(posX+decX+16-1, posY+decY+16+2, posX+decX+16-1, posY+decY-height*34+16, 2f, "705B2E", 1);
		ObjectRenderer.drawLine(posX+decX+maxSize+16+1, posY+decY+16+2, posX+decX+maxSize+16+1, posY+decY-height*34+16, 2f, "705B2E", 1);
		for(String action : actions)
		{
			if(/*addedAction || (!addedAction && i != 2)*/true)
			{
				if(i == selected)
					ObjectRenderer.renderColoredRect(posX+decX+maxSize/2+16, posY-j*34+decY,maxSize , 34, 0, "AF8D49", 1f); //B7954B
				//ObjectRenderer.renderTexturedRect(posX+32+decX, posY-j*34+decY, 32, 32, (i%2)*16, (i/2)*16, 16+(i%2)*16, 16+(i/2)*16, 32, "fx/unitactions");
				ObjectRenderer.renderTexturedRect(posX+32+decX, posY-j*34+decY, 32, 32, 0, 0, 16,16, 16, "gui/actions/"+actions[i]);
				
				float transp = 1f;
				if(!canMove && this.actions[i].equals("move"))
					transp = 0.5f;
				if(!canAttack && this.actions[i].equals("attack"))
					transp = 0.5f;
				FontRenderer.drawTextUsingSpecificFont(posX+56+decX, posY-16-j*34+decY, 0, 32, action, BitmapFont.SMALLFONTS,transp);
				j++;
			}
			i++;
		}
	}
	
	public boolean onKeyPress(int k) {
		if( k == FastConfig.keyUp)
		{
			selected--;
			/*if(!addedAction && selected == 2)
				selected--;*/
		}
		else if( k == FastConfig.keyDown)
		{
			selected++;
			/*if(!addedAction && selected == 2)
				selected++;*/
		}
		if(selected >= 3)
			selected = 3;
		if(selected < 0)
			selected = 0;
		if(k == FastConfig.keyStart)
		{
			validate(selected);
			return true;
		}
		return false;
	}

	private void validate(int o) {

		GameScene gameScene = (GameScene)parent;
		if(o >= this.actions.length)
			return;
		String option = this.actions[o];
		if(option.equals("move"))
		{
			if(canMove)
			{
				String moveUnitMsg = "gameMode/moveUnit:"+unit.posX+":"+unit.posY+":"+targetX+":"+targetY;
				Client.connect.send(moveUnitMsg);
			}
			else
				return;
			//System.out.println(moveUnitMsg);
		}
		else if(option.equals("attack"))
		{
			if(canAttack)
				this.parent.setSubscene(new UnitAttackSubscene(this));
			return;
		}
		else if(option.equals("conquer"))
		{
			if(canMove)
			{
				String moveUnitMsg = "gameMode/moveUnit:"+unit.posX+":"+unit.posY+":"+targetX+":"+targetY;
				Client.connect.send(moveUnitMsg);
			}
			String conquerUnitMsg = "gameMode/conquerUnit:"+unit.posX+":"+unit.posY+":"+targetX+":"+targetY;
			Client.connect.send(conquerUnitMsg);
		}
		gameScene.control.unitSelected = false;
		gameScene.control.acceptablePath = null;
		parent.setSubscene(null);
	}
	
	public boolean onClick(int posx,int posy,int button)
	{
		if(mouseOver)
		{
			validate(selected);
			return true;
		}
		return false;
	}
}
