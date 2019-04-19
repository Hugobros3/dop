package io.xol.dop.game.tiles;

//(c) 2014 XolioWare Interactive

import io.xol.dop.game.WorldRemote;
import io.xol.dop.game.client.bits.NationsInfo;
import io.xol.dop.game.server.Server;
import io.xol.dop.game.units.Unit;
import io.xol.dop.game.units.UnitAbleToConquer;
import io.xol.engine.misc.ColorsTools;

public abstract class TileConquerable extends InteractiveTile{
	// abstract class for conquerable tiles
	public int nation = -1;
	// data for nation takeover
	public int conqueringNation = -1;
	public int points = 0;
	
	public boolean canInteract(){
		return true;
	}
	
	public boolean isConquerable(Unit u)
	{
		if(u.nation == nation)
			return false;
		if(!(u instanceof UnitAbleToConquer))
			return false;
		return true;
	}
	
	public int getPointsNeeded()
	{
		return 20;
	}
	
	public void conquerTick(Unit u) // This runs server-side
	{
		if(u.nation != -1)
		{
			int n = u.nation;
			if(n == nation)
				return; // can't conquer self nation !
			if(n != conqueringNation)
			{
				points = 0;
				conqueringNation = n;
			}
			Server.handler.dispatchWorldUpdate(posX,posY,"world/unitConquering:"+posX+":"+posY+":"+(getPointsNeeded()-points));
			//System.out.println("Conquering tile... [n="+nation+", nn="+conqueringNation+", p="+points+", pn"+getPointsNeeded()+"]");
			points++;
			if(points >= getPointsNeeded())
			{
				nation = conqueringNation;
				points = 0;
				//System.out.println("Tile conquered ! Now owned by nationID "+nation);
				//Client updating
				Server.handler.dispatchWorldUpdate(posX,posY,"world/unitConquering:"+posX+":"+posY+":-1");
				Server.handler.dispatchWorldUpdate(posX,posY,"world/changeNation:"+posX+":"+posY+":"+nation);
			}
		}
	}
	
	public float[] getColor()
	{
		if(nation != -1 && this.world instanceof WorldRemote)
		{
			int color = NationsInfo.getColor(nation);//NationsColors.getColor(nation);
			//color+=256*256*256;
			int rgb[] = ColorsTools.rgbSplit(color);
			//System.out.println(nation+" : "+color);
			//return new float[] {1f,0f,1f};
			return new float[] {rgb[0]/255f,rgb[1]/255f,rgb[2]/255f};
		}
		else if(nation != -1)
		{
			return new float[] {0f,0f,0f};
		}
		return new float[] {1,1,1};
	}
}
