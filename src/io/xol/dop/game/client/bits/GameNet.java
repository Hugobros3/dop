package io.xol.dop.game.client.bits;

import io.xol.dop.game.WorldRemote;
import io.xol.dop.game.client.Client;
import io.xol.dop.game.client.scenes.BackToMenuScene;
import io.xol.dop.game.client.scenes.GameScene;
import io.xol.dop.game.mode.GameModeType;

//(c) 2014 XolioWare Interactive

public class GameNet {
	private GameScene scene;

	public GameNet(GameScene gameScene) {
		scene = gameScene;
	}
	
	public void update()
	{
		errorCheck();
		//tech messages
		String m;
		while((m = Client.connect.getLastTechMessage()) != null)
			handleTech(m);
	}
	
	private void handleTech(String m) {
		//System.out.println("techmsg:"+m);
		if(m.equals("login/ok"))
		{
			Client.connect.send("player/info");
			Client.connect.send("player/nation");
			Client.connect.send("player/nationInfo");
			Client.connect.send("player/nationLogo");
			Client.connect.send("player/nationFlags");
			scene.world = new WorldRemote();
			Client.connect.connectionStatus = "Connected !";
		}
		else if(m.startsWith("info/"))
		{
			String[] data = m.replace("info/", "").split(":");
			if(data.length > 1)
			{
				if(data[0].equals("name"))
					scene.serverName = data[1];
				if(data[0].equals("version"))
					scene.serverVersion = data[1];
				if(data[0].equals("motd"))
					scene.serverMotd = data[1];
				if(data[0].equals("game"))
				{
					scene.gameName = data[1];
					if(scene.gameMode == null)
					{
						scene.gameMode = GameModeType.makeGameModeByType(GameModeType.getType(Integer.parseInt(data[3])));
						scene.gameMode.initClient(scene);
					}
				}
				if(data[0].equals("gtu"))
				{
					scene.gtuh = Integer.parseInt(data[1]);
				}
				if(data[0].equals("connected"))
				{
					scene.cuco = Integer.parseInt(data[1]);
					scene.maxco = Integer.parseInt(data[2]);
				}
				if(data[0].equals("playerNation"))
				{
					NationsInfo.funds = Integer.parseInt(data[1]);
					NationsInfo.population = Integer.parseInt(data[2]);
				}
			}
		}
		else if(m.startsWith("player/"))
		{
			String[] data = m.replace("player/", "").split(":");
			if(data.length > 1)
			{
				if(data[0].equals("nation") && scene.nations != null)
					NationsInfo.playerNation = Integer.parseInt(data[1]);
			}
		}
		else if(m.startsWith("nation/"))
		{
			if(scene.nations != null)
				scene.nations.handlePacket(m.replace("nation/", ""));
		}
	}

	private void errorCheck() {
		//Just moving error checks into their own class.
		if(Client.connect == null)
		{
			scene.eng.changeScene(new BackToMenuScene(scene.eng,0,"Network error","Socket object is null !!!"));
			return;
		}
		if(Client.connect.hasFailed())
			scene.eng.changeScene(new BackToMenuScene(scene.eng,0,"Network error",Client.connect.getLatestErrorMessage()));
	}
}
