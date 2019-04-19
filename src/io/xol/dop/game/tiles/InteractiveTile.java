package io.xol.dop.game.tiles;

import io.xol.dop.game.World;

//(c) 2014 XolioWare Interactive

public abstract class InteractiveTile implements Cloneable {
	
	World world;
	public int posX;
	public int posY;
	public Tile tile;
	
	public InteractiveTile getClone()
	{
		InteractiveTile returnme = null;
		try {
			returnme = (InteractiveTile) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return returnme;
	}
	
	public void init(int x,int y, World w)
	{
		posX = x;
		posY = y;
		world = w;
	}
	
	public void tick()
	{
		// Do nothing
	}
	
	public boolean canInteract(){
		return false;
	}
	
	public abstract String[] getTooltip();
	public abstract String getTooltipColor();
	public abstract String save();
	public abstract void load(String str);
}
