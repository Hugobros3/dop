package io.xol.dop.game.server.tech;

import java.io.File;

import org.newdawn.slick.util.Log;

import io.xol.dop.game.common.VersionInfo;
import io.xol.dop.game.common.nations.Nation;
import io.xol.dop.game.mode.GameModeType;
import io.xol.dop.game.server.Server;
import io.xol.dop.game.server.game.ServerGame;
import io.xol.dop.game.server.net.ServerClient;
import io.xol.dop.game.server.net.ServerConnections;

//(c) 2014 XolioWare Interactive

public class ServerConsole {
	@SuppressWarnings("deprecation")
	public static void handleCommand(String cmd, CommandEmitter emitter)
	{
		Log.info(("["+emitter.name+"] ")+"Entered command : " + cmd);

		String args[] = cmd.split(" ");
		//No rights needed
		if (cmd.equals("uptime")) {
			emitter.reply("The server has been running for "+(System.currentTimeMillis()/1000-Server.initS)+" seconds.");
			return;
		}
		else if (cmd.equals("info")) {
			emitter.reply("The server's ip is "+ServerConnections.ip);
			emitter.reply("It's running version "+VersionInfo.get()+" of the server software.");
			return;
		}
		else if (cmd.equals("clients")) {
			emitter.reply("==Listing clients==");
			for(ServerClient client : Server.handler.clients)
			{
				emitter.reply(client.getIp()+"/"+client.getHost()+":"+client.id+" - "+client.name);
			}
			emitter.reply("==done==");
			return;
		}
		else if (cmd.startsWith("nations")) {
			if(Server.game != null)
			{
				if(args.length == 2 && args[1].equals("list")){
					if(Server.game.nations == null)
						emitter.reply("Nations null");
					else
					{
						emitter.reply("==Listing nations==");
						for(int i = 0; i < Server.game.nations.length; i++)
						{
							Nation n = Server.game.nations[i];
							if(n != null)
								emitter.reply(n.id + " : " + n.name + " - " + n.desc);
						}
						emitter.reply("==done==");
					}
				}
				else if(args.length == 4 && args[1].equals("create")){
					Nation myNation = new Nation(args[2],Integer.parseInt(args[3]),Server.game);
					Server.game.addNation(myNation);
					emitter.reply("Nation \""+myNation.name+"\" made");
				}
				else if(args.length == 2 && args[1].equals("genTex")){
					Server.game.generateNationTexture();
					emitter.reply("texgen");
				}
				else if(args.length == 3 && args[1].equals("delete")){
					int deleteID = Integer.parseInt(args[2]);
					Server.game.deleteNation(deleteID);
				}
				//generateNationTexture()
				else
				{
					emitter.reply("Nation commands");
					emitter.reply("nations list");
					emitter.reply("nations create name color");
				}
			}
			else
			{
				emitter.reply("No game loaded.");
			}
			
			return;
		}
		else if (cmd.equals("help")) {
			emitter.reply("Avaible commands :");
			//general
			emitter.reply("info - Gives a few info about the server.");
			emitter.reply("uptime - Self-explaining.");
			//net
			emitter.reply("clients - Lists users and ips connected to the server.");
		}
		
		//Rights check
		
		if(!emitter.hasRights("any"))
		{
			emitter.reply("Huh sorry, but I think you don't have the right to do that.");
			return;
		}
		
		//general
		if (cmd.equals("stop")) {
			emitter.reply("Stopping server.");
			Server.running = false;
			Server.closeServer();
			return;
		}
		else if (cmd.equals("reloadconfig")) {
			Server.reloadConfig();
			emitter.reply("Config reloaded.");
			return;
		}
		//net
		else if (args[0].equals("kick") && args.length == 2) {
			ServerClient tokick = Server.handler.getClientByName(args[1]);
			if(tokick != null)
			{
				Server.handler.disconnectClient(tokick);
				emitter.reply("Forced disconnect for user "+args[1]);
			}
			else
			{
				emitter.reply("User '"+tokick+"' not found.");
			}
			return;
		}
		else if (args[0].equals("kickip") && args.length == 2) {
			String tokick = args[1];
			Server.handler.disconnectClient(tokick);
			emitter.reply("Forced disconnect for ip "+tokick);
			return;
		}
		//game
		else if (args[0].equals("makegame") && args.length == 3) {
			String gameName = args[1];
			String gameWorld = args[2];
			emitter.reply("You requested to make a new game called \""+gameName+"\" using level "+gameWorld);
			Server.game = new ServerGame(gameName,gameWorld,GameModeType.getType(0));
			Server.game.start();
			emitter.reply("New game created an started.");
			Server.serverConfig.setProp("current-game", gameName);
			Server.serverConfig.save();
			return;
		}
		else if (args[0].equals("loadgame") && args.length == 2) {
			String gameName = args[1];
			emitter.reply("Loading game \""+gameName+"\".");
			Server.game = new ServerGame(gameName);
			Server.game.start();
			emitter.reply("New game loaded and started.");
			Server.serverConfig.setProp("current-game", gameName);
			Server.serverConfig.save();
			return;
		}
		else if (args[0].equals("listgames")) {
			emitter.reply("Listing games in games/ directory.");
			for(File f : new File(System.getProperty("user.dir") + "/games/").listFiles())
			{
				if(f.isDirectory())
					emitter.reply(f.getName()+"");
			}
			return;
		}
		else if (args[0].equals("save")) {
			if(Server.game != null)
				Server.game.save();
			Server.serverConfig.save();
			return;
		}
		else if (args[0].equals("op")) {
			if(args.length == 1)
				emitter.reply("/op <username>");
			else
			{
				ServerClient c = Server.handler.getClientByName(args[1]);
				if(c == null)
					emitter.reply("There is no such user connected !");
				else
					UsersPrivileges.admins.add(args[1]);
			}
			return;
		}
		else if (args[0].equals("time")) {
			if(Server.game != null && Server.game.world != null)
			{
				if(args.length == 1)
					emitter.reply("Current time : "+(Server.game.world.time/100  + ":" + (Server.game.world.time%100)*60/100));
				else
				{
					try{
					int newtime = Integer.parseInt(args[1]);
					Server.game.world.time = newtime;
					Server.handler.sendAllRaw("world/time:"+Server.game.world.time);
					emitter.reply("Time set to : "+(Server.game.world.time/100  + ":" + (Server.game.world.time%100)*60/100));
					}
					catch(Exception e)
					{
						emitter.reply("You must input a numeric value !");
					}
				}
			}
			else
				emitter.reply("No world loaded !");
			return;
		}
		else if (args[0].equals("unloadgame")) {
			if(Server.game != null)
			{
				emitter.reply("Saving and stopping current game...");
				Server.game.save();
				Server.game.stop();
				Server.game = null;
				Server.serverConfig.setProp("current-game", "null");
				Server.serverConfig.save();
				emitter.reply("Done !");
			}
			else
			{
				emitter.reply("There is no game loaded as of now !");
			}
				Server.game.save();
			Server.serverConfig.save();
			return;
		}
		//help
		else if (cmd.equals("help")) {
			emitter.reply("stop - Stops the server.");
			
			emitter.reply("kickip - Will force disconnect that ip. May kick multiple people on it.");
			emitter.reply("kick - Will force disconnect that user.");
			emitter.reply("ban - Will refuse any connection from this user. Redo to unban.");
			emitter.reply("banip - Will refuse any connection from this IP. Redo to unban.");
			//game
			emitter.reply("makegame <name> <world> - Will start a new game on specified world.");
			emitter.reply("loadgame <name> - Will load a game");
			emitter.reply("listgames - Will list games avaible for loading.");
			emitter.reply("loadgame <name> - Will load a game");
			return;
		}
		else
		{
			emitter.reply("Unrecognized command. Try help.");
		}
	}
}
