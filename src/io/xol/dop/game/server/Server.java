package io.xol.dop.game.server;

import java.io.File;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import io.xol.dop.game.common.VersionInfo;
import io.xol.dop.game.common.nations.NationsColors;
import io.xol.dop.game.server.game.ServerGame;
import io.xol.dop.game.server.multiverse.ServerAnnouncerThread;
import io.xol.dop.game.server.net.ServerConnections;
import io.xol.dop.game.server.tech.CommandEmitter;
import io.xol.dop.game.server.tech.ServerConsole;
import io.xol.dop.game.server.tech.UsersPrivileges;
import io.xol.dop.game.units.Unit;
import io.xol.engine.misc.ConfigFile;

//(c) 2014 XolioWare Interactive

public class Server {
	//The server class handles and make the link between all server components
	//It also takes care of the command line input as it's the main thread, 
	//thought the processing of command lines is handled by ServerConsole.java
	
	//Basic server stuff init !
	static public ConfigFile serverConfig = new ConfigFile("config/server.cfg");
	static public Logger log = Logger.getLogger("server");
	public static boolean running = true;
	public static long initS = System.currentTimeMillis()/1000; // <- uptime !
	
	//game logic aspect
	public static ServerGame game;
	
	//network aspect
	public static ServerConnections handler = new ServerConnections();
	
	//multiverse aspect
	public static ServerAnnouncerThread announcer = new ServerAnnouncerThread();
	
	public static void main(String[] args) {
		Unit.initUnits();
		NationsColors.loadDefaultColorScheme();
		//logger init
		initLog();
		//Start server services
		try {
			log.info("Starting server version " + VersionInfo.get());
			//init network
			handler.start();
			//init multiverse
			announcer.init();
			announcer.start();
			//load users privs
			UsersPrivileges.load();
			//init game if present
			String cgame = serverConfig.getProp("current-game","null");
			File gameFolder = new File(System.getProperty("user.dir") + "/games/"+cgame);
			//
			if(!cgame.equals("null") && gameFolder.exists())
			{
				log.info("Server current-game was \""+cgame+"\", loading it..");
				game = new ServerGame(cgame);
				game.start();
			}
			else
			{
				log.info("No current game found. Make a new one by entering \"makegame <name> <world>\" or load one that already exists.");
			}
		} catch (Exception e) { // Exceptions stuff
			log.severe("Could not initalize server. Weird error.");
			e.printStackTrace();
			System.exit(-1);
		}
		Scanner in = new Scanner(System.in);
		CommandEmitter console = new CommandEmitter();
		while (running) { // main loop
			System.out.print("> ");
			String cmd = in.nextLine(); // Wait for input
			if (cmd != null) {
				try{
					ServerConsole.handleCommand(cmd,console); // Process it
				}
				catch(Exception e)
				{
					System.out.println("error while handling command :");
					e.printStackTrace();
				}
			}
		}
		in.close();
		closeServer();
	}
	@SuppressWarnings("deprecation")
	public static void closeServer() {
		//Stopping ! We need to save the game !
		if(game != null)
			game.close();
		//When stopped, close sockets and save config.
		announcer.flagStop();
		announcer.stop();
		handler.closeAll();
		handler.stop();
		serverConfig.save();
		handler.close();
		UsersPrivileges.save();
		log.info("Server closed, sock destroyed.");
		Runtime.getRuntime().exit(0);
	}
	//log
	private static void initLog() {
		//Dirty class for having proper log formatting.
		Handler h = new ConsoleHandler();
		h.setFormatter(new LogFormatter());
		for(Handler iHandler : log.getParent().getHandlers())
        {
			log.getParent().removeHandler(iHandler);
        }
		log.addHandler(h);
	}
	//Multithread shit
	public static synchronized void stop()
	{
		running = false;
	}
	public static synchronized boolean isRunning()
	{
		return running;
	}
	//Config shit
	public static void reloadConfig() {
		UsersPrivileges.load();
		serverConfig.load();
	}
}
