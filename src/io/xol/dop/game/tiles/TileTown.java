package io.xol.dop.game.tiles;

//(c) 2014 XolioWare Interactive

import io.xol.dop.game.client.bits.NationsInfo;

public class TileTown extends TileConquerable{
	public int population = 100;
	
	@Override
	public String save() {
		return population+";"+nation;
	}

	@Override
	public void load(String str) {
		//System.out.println("load:"+str);
		String[] data = str.split(";");
		if(data.length > 1)
		{
			population = Integer.parseInt(data[0]);
			nation = Integer.parseInt(data[1]);
		}
	}

	@Override
	public String[] getTooltip() {
		if(nation == -1)
			return new String[] {"Town","Free","Population : "+population};
		return new String[] {"Town","Nation : "+NationsInfo.getNation(nation),"Population : "+population};
	}

	@Override
	public String getTooltipColor() {
		return "00FF00";
	}
}
