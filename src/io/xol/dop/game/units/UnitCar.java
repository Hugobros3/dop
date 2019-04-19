package io.xol.dop.game.units;

//(c) 2014 XolioWare Interactive

public class UnitCar extends Unit{

	public UnitCar(String name2) {
		super(name2);
	}

	public int getTicksPerMove(int tileID)
	{
		return 1; // Number of ticks to wait before moving
	}
	
	@Override
	public String saveMeta() {
		return "";
	}

	@Override
	public void loadMeta(String meta) {
		
	}

}
