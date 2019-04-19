package io.xol.dop.game.server.game;

import io.xol.dop.game.World;
import io.xol.dop.game.common.nations.Nation;
import io.xol.dop.game.mode.GameMode;
import io.xol.dop.game.mode.GameModeType;
import io.xol.dop.game.server.Server;
import io.xol.dop.game.server.net.ServerClient;
import io.xol.engine.misc.ConfigFile;
import io.xol.engine.misc.FoldersUtils;

import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

//(c) 2014 XolioWare Interactive

public class ServerGame extends Thread{

	public ConfigFile gamecfg;
	public World world;
	public String gameName;
	
	//public List<Nation> nations = new ArrayList<Nation>();
	public Nation[] nations = null;
	
	public boolean paused = false;
	public boolean stopped = false;
	public GameMode gameMode;
	
	public ServerGame(String name,String world,GameModeType gm)
	{
		//TODO nations size param
		nations = new Nation[gamecfg.getIntProp("nations", "24")];
		//This one is for creating
		gameName = name;
		checkFolders(gameName);
		gamecfg = new ConfigFile("games/" + gameName +"/game.cfg");
		prepare(world);
		gameMode = GameModeType.makeGameModeByType(gm);
		gamecfg.setProp("gameMode",GameModeType.getId(gameMode.getGMType()));
		//Update accessors
		this.setName("Server Game Thread");
	}
	
	public ServerGame(String name)
	{
		//This one is for loading
		gameName = name;
		checkFolders(gameName);
		gamecfg = new ConfigFile("games/" + gameName +"/game.cfg");
		String world = gamecfg.getProp("world","default");
		prepare(world);
		gameMode = GameModeType.makeGameModeByType(GameModeType.getType(gamecfg.getIntProp("gameMode","0")));
		//Load nations
		loadNations();
		this.setName("Server Game Thread");
	}

	public void run() {
		//run loop - todo
		System.out.println("World ticking thread started");
		generateNationTexture();
		gameMode.initServer(this);
		boolean wasEmpty = false;
		while(!stopped)
		{
			try {
				long wait = 1000l;
				if(Server.handler.clients.size() > 0 || Server.serverConfig.getBooleanProp("run-empty", true))
				{
					wasEmpty = false;
					wait = gameMode.tickServer(this);	
				}
				else
				{
					if(!wasEmpty)
					{
						System.out.println("Server is empty, pausing game.");
						wasEmpty = true;
					}
				}
				while(!stopped && wait > 0)
				{
					Thread.sleep(Math.min(1000l, wait));
					wait-=1000;
				}
				//System.out.println("Ticking");
				//Thread.sleep(1000L/(gamecfg.getIntProp("ticks-per-hour", "3600")/3600));
			} catch (InterruptedException e) {
				
			}
		}
		
	}
	//creating/stoping
	public void prepare(String world)
	{
		if(gamecfg.getProp("start-date", "0").equals("0"))
		{
			createNew(world);
		}
		load();
		save();
	}
	
	public void close()
	{
		save();
	}
	
	public void createNew(String world)
	{
		System.out.println("Creating a new game called "+gameName);
		java.util.Date date= new java.util.Date();
		gamecfg.setProp("start-date", ""+date.getTime());
		gamecfg.setProp("world", world);
		//copy level dir
		System.out.println("Copying level into game folder ... ");
		FoldersUtils.copyFolder(new File(System.getProperty("user.dir") + "/levels/"+world),new File(System.getProperty("user.dir")+"/games/"+gameName+"/level"));
		System.out.println("Done !");
	}
	//net code
	public void handleWorldRequest(ServerClient c, String req) {
		//System.out.println("client req:"+req);
		if(req.equals("info"))
			c.send("world/info:"+world.name+":"+world.width+":"+world.height); // world/info:name:width:height
		else if(req.startsWith("get") && req.split(":").length == 3) // world/get:1:5
		{
			int x = Integer.parseInt(req.split(":")[1]);
			int y = Integer.parseInt(req.split(":")[2]);
			//System.out.println("x="+x+"; y="+y);
			if(world.sectorExists(x, y))
			{
				c.sendSector(world.getSector(x*32, y*32),x,y);
			}
			else
			{
				c.send("world/nosector:"+x+":"+y);
			}
			// world/sector:1:5:b64bullshit
		}
		else if(req.startsWith("set") && gameMode.getGMType().equals(GameModeType.MULTIPLAYER_EDITOR)) // Multiplayer editor thingy
		{
			String[] infos = req.split(":");
			if(infos.length >= 4)
			{
				int x = Integer.parseInt(infos[1]);
				int y = Integer.parseInt(infos[2]);
				int d = Integer.parseInt(infos[3]);
				//System.out.println("[debug] setting tile at"+x+":"+y+" to "+d);
				world.setTileAt(x, y, d);
				for(ServerClient cc : Server.handler.clients)
				{
					cc.send("world/set:"+x+":"+y+":"+d);
				}
			}
		}
	}
	
	public void updateSector(int x, int y) {
		for(ServerClient client : Server.handler.clients)
		{
			if(client.authentificated)
			{
				if(world.sectorExists(x, y))
				{
					client.sendSector(world.getSector(x*32, y*32),x,y);
				}
			}
		}
	}
	
	//Getters
	public Nation getNation(String name)
	{
		for(Nation n : nations)
		{
			if(n != null && n.name.equals(name))
				return n;
		}
		return null;
	}

	public int addNation(Nation myNation) {
		for(int i = 0; i < nations.length; i++)
		{
			if(nations[i] == null)
			{
				myNation.id = i;
				nations[i] = myNation;
				generateNationTexture();
				return i;
			}
		}
		return -1;
	}

	private void loadNations() {
		nations = new Nation[gamecfg.getIntProp("nations", "24")];
		for(int i = 0; i < nations.length; i++)
		{
			File f = new File("games/" + gameName +"/nations/"+i+".nation");
			if(f.exists())
				nations[i] = new Nation(i,this);
		}
	}

	public void deleteNation(int deleteID) {
		nations[deleteID] = null;
		File f = new File("games/" + gameName +"/nations/"+deleteID+".nation");
		if(f.exists())
			f.delete();
		f = new File("games/" + gameName +"/nations/logo_nation_"+deleteID+".png");
		if(f.exists())
			f.delete();
		generateNationTexture();
	}
	
	//Saving/loading
	public void load()
	{
		java.util.Date date= new java.util.Date();
		gamecfg.setProp("last-load-date", ""+date.getTime());
		this.world = new World(gamecfg.getProp("world", "default"),false,gameName);
	}
	
	public void save()
	{
		if(nations == null)
			return;
		for(Nation n : nations)
		{
			if(n != null)
				n.save();
		}
		world.saveWorld();
		gamecfg.save();
	}

	public void generateNationTexture()
	{
		File tex = new File("./games/"+gameName+"/nations/logos.png");
		if(tex.exists())
			tex.delete();
		System.out.print("Generating nation flags texture ...");
		try{
		//making texture
		BufferedImage texture = new BufferedImage(128,128,Transparency.TRANSLUCENT);
		//
		int count = 0;
		for(int i = 0; i < nations.length; i++)
		{
			File f = new File("./games/"+gameName+"/nations/logo_nation_"+i+".png");
			if(!f.exists()){
				f = new File("./res/textures/nations/nologo.png");
			}
			//System.out.println("omg :"+f.getName().replaceAll(".png", ""));
			BufferedImage pasteme = ImageIO.read(f);
			
			for(int x = 0;x < 24;x++)
			{
				for(int y = 0;y < 16;y++)
				{
					texture.setRGB((i/8)*32+x, (i%8)*16+y, pasteme.getRGB(x, y));
				}
			}
			count++;
			
		}
		ImageIO.write(texture, "PNG", tex);
		System.out.print(" done ! "+count+" sub-images merged into the file. \n");
		}
		catch(Exception e)
		{
			System.out.println("Something went wrong ! File could not be created.");
			e.printStackTrace();
		}
	}
	
	//saving/loading misc

	private void checkFolders(String name) {
		//Checks if games/ ang games/gamename exists and create them if needed.
		File folderg = new File(System.getProperty("user.dir") + "/games/");
		if(!folderg.exists())
			folderg.mkdir();
		//check if level folder exists
		File folder = new File(System.getProperty("user.dir") + "/games/" + name);
		if(!folder.exists())
			folder.mkdir(); // make it
		//subfolders
		File subfolder = new File(System.getProperty("user.dir") + "/games/" + name + "/nations/");
		if(!subfolder.exists())
			subfolder.mkdir(); // make it
		subfolder = new File(System.getProperty("user.dir") + "/games/" + name + "/players/");
		if(!subfolder.exists())
			subfolder.mkdir(); // make it
	}
}
