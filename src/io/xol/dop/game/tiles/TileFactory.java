package io.xol.dop.game.tiles;

//(c) 2014 XolioWare Interactive

import io.xol.dop.game.client.bits.NationsInfo;

public class TileFactory extends TileConquerable{
	public int cooldown = 0;
	
	@Override
	public String save() {
		return cooldown+";"+nation;
	}

	@Override
	public void load(String str) {
		//System.out.println("load:"+str);
		String[] data = str.split(";");
		if(data.length > 1)
		{
			cooldown = Integer.parseInt(data[0]);
			nation = Integer.parseInt(data[1]);
		}
	}

	@Override
	public String[] getTooltip() {
		if(nation == -1)
			return new String[] {"Factory","Free"};
		return new String[] {"Factory","Nation : "+NationsInfo.getNation(nation),"Cooldown : "+cooldown};
	}

	@Override
	public String getTooltipColor() {
		return "00FF00";
	}
}
