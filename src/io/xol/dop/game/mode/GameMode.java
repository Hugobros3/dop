package io.xol.dop.game.mode;

import io.xol.dop.game.World;
import io.xol.dop.game.client.scenes.GameScene;
import io.xol.dop.game.server.game.ServerGame;
import io.xol.dop.game.server.net.ServerClient;


//(c) 2014 XolioWare Interactive

public abstract class GameMode {
	// This class ( and subclasses ) takes care of handling the gamemode-specific behaviors for server games and clients
	public abstract GameModeType getGMType();
	
	public abstract void initClient(GameScene scene);
	
	public abstract void initServer(ServerGame serverGame);
	
	public abstract long tickClient(World w);
	
	public abstract long tickServer(ServerGame game);
	
	public String getName()
	{
		return "Null Gamemode";
	}
	
	public String getDescription()
	{
		return "This gamemode should never show up.";
	}

	public void handlePacket(ServerGame game, ServerClient c, String msg) {
		
	}
}
