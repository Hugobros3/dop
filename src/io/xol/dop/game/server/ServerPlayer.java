package io.xol.dop.game.server;

import java.util.HashMap;
import java.util.Map;

import io.xol.dop.game.Sector;
import io.xol.dop.game.World;
import io.xol.dop.game.server.game.ServerGame;
import io.xol.dop.game.server.net.ServerClient;
import io.xol.engine.misc.ConfigFile;

//(c) 2014 XolioWare Interactive

public class ServerPlayer {

	ConfigFile playerData;
	ServerClient playerConnection;
	
	public int camX = 0;
	public int camY = 0;
	
	public int screenWidth = 0;
	public int screenHeight = 0;
	
	public int nation = -1;
	
	public Map<String,Long> loadedSectors = new HashMap<String,Long>();
	
	public ServerPlayer(ServerClient serverClient) {
		playerConnection = serverClient;
		playerData = new ConfigFile(getConfigFilePath());
		//Sets dates
		playerData.setProp("lastlogin",""+System.currentTimeMillis());
		if(playerData.getProp("firstlogin","nope").equals("nope"))
			playerData.setProp("firstlogin",""+System.currentTimeMillis());
		nation = playerData.getIntProp("nation", "-1");
		//System.out.println("Player profile "+playerConnection.name+" loaded.");
	}

	private String getConfigFilePath() {
		if(Server.game != null)
			return "games/"+Server.game.gameName+"/players/"+playerConnection.name+".cfg";
		return "players/"+playerConnection.name+".cfg";
	}

	public void save() {
		long lastTime = Long.parseLong(playerData.getProp("timeplayed","0"));
		long lastLogin = Long.parseLong(playerData.getProp("lastlogin","0"));
		playerData.setProp("timeplayed",""+(lastTime+(System.currentTimeMillis()-lastLogin)));
		playerData.setProp("nation", nation);
		playerData.save();
		//System.out.println("Player profile "+playerConnection.name+" saved.");
	}

	public void handlePacket(ServerGame game, ServerClient c, String msg) {
		String[] data = msg.split(":");
		if(data[0].equals("camera"))
		{
			camX = Integer.parseInt(data[1]);
			camY = Integer.parseInt(data[2]);
			screenWidth = Integer.parseInt(data[3]);
			screenHeight = Integer.parseInt(data[4]);
			updatePlayerSectors();
			//System.out.println("Got a camera packet");
		}
	}

	private void updatePlayerSectors() {
		World world = Server.game.world;
		if(world != null)
		{
			for (int i = 0; i < world.width; i++) {
				for (int y = world.height; y >= 0; y--) {
					if((i+1)*32 >= camX && (y+1)*32 >= camY && (i)*32 <= camX+screenWidth/32 && (y)*32 <= camY+screenHeight/32)
					{
						if(world.sectorExists(i, y))
						{
							if(!loadedSectors.containsKey(i+":"+y))
							{
								//Send needed sectors
								Sector sec = world.getSector(i*32, y*32);
								playerConnection.sendSector(sec, i, y);
								loadedSectors.put(i+":"+y, sec.lastUpdate);
								//System.out.println("added sector"+loadedSectors.size()+" :"+i+":"+y);
							}
							else
							{
								//Update needed sectors
								long sectorLastUpdate = loadedSectors.get(i+":"+y);
								Sector sec = world.getSector(i*32, y*32);
								if(sec.lastUpdate > sectorLastUpdate)
								{
									playerConnection.sendSector(sec, i, y);
									loadedSectors.remove(i+":"+y);
									loadedSectors.put(i+":"+y, sec.lastUpdate);
									//System.out.println("updated sector");
								}
							}
						}
					}
				}
			}
		}
		else if(loadedSectors.size() != 0)
			loadedSectors.clear();
		//Clean not viewed sectors
		Map<String,Long> newSectors = new HashMap<String,Long>();
		
		for(String coords : loadedSectors.keySet())
		{
			int i = Integer.parseInt(coords.split(":")[0]);
			int y = Integer.parseInt(coords.split(":")[1]);
			if((i+1)*32 >= camX && (y+1)*32 >= camY && (i)*32 <= camX+screenWidth/32 && (y)*32 <= camY+screenHeight/32)
			{
				newSectors.put(coords, loadedSectors.get(coords));
			}
		}
		
		loadedSectors = newSectors;
		//System.out.println(loadedSectors.size()+" sectors for plauer "+this.playerConnection.name);
	}
}
