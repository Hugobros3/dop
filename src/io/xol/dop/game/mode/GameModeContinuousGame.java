package io.xol.dop.game.mode;

import io.xol.dop.game.World;
import io.xol.dop.game.client.scenes.GameScene;
import io.xol.dop.game.server.game.ServerGame;

//(c) 2014 XolioWare Interactive

public class GameModeContinuousGame extends GameModeBasicGame{
	
	long tickWait = 0;
	
	@Override
	public GameModeType getGMType() {
		return GameModeType.CONTINUOUS_GAME;
	}
	
	public String getName()
	{
		return "Continuous game";
	}
	
	public String getDescription()
	{
		return "Every player can act simultanously.";
	}
	
	// Server
	
	@Override
	public void initServer(ServerGame game) {
		tickWait = (int) (1000L/(game.gamecfg.getIntProp("ticks-per-hour", "3600")/3600f));
		System.out.println("tickwait:"+tickWait);
	}
	
	@Override
	public long tickServer(ServerGame game) {
		super.tickServer(game);
		return tickWait;
	}

	// Client
	
	public void initClient(GameScene scene) {
		super.initClient(scene);
	}

	@Override
	public long tickClient(World w) {
		return 2000l;
	}
}
