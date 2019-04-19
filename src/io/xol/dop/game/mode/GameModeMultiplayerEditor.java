package io.xol.dop.game.mode;

import io.xol.dop.game.World;
import io.xol.dop.game.client.bits.EditorBits;
import io.xol.dop.game.client.scenes.GameScene;
import io.xol.dop.game.server.game.ServerGame;

//(c) 2014 XolioWare Interactive

public class GameModeMultiplayerEditor extends GameMode {

	public GameModeType getGMType() {
		return GameModeType.MULTIPLAYER_EDITOR;
	}

	public String getName()
	{
		return "Multiplayer editor";
	}
	
	public String getDescription()
	{
		return "This gamemode allow for editing a map with other players.";
	}

	// Client
	
	@Override
	public void initClient(GameScene scene) {
		scene.editor = new EditorBits(scene);
	}
	
	@Override
	public long tickClient(World w) {
		
		return 0;
	}
	
	// Server
	
	@Override
	public void initServer(ServerGame game) {
		
	}
	
	@Override
	public long tickServer(ServerGame game) {
		return 1000l;
	}
}
