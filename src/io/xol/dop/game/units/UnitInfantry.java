package io.xol.dop.game.units;

//(c) 2014 XolioWare Interactive

public class UnitInfantry extends GroundUnit implements UnitAbleToConquer{
	
	public UnitInfantry(String name) {
		super(name);
	}
	
	@Override
	public String saveMeta() {
		return "";
	}

	@Override
	public void loadMeta(String meta) {
		
	}
	
	public void draw(int posx, int posy,boolean over,boolean inGame)
	{
		super.draw(posx, posy,over,inGame);
	}
}
