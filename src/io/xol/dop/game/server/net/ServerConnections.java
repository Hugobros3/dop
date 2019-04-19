package io.xol.dop.game.server.net;

import io.xol.dop.game.common.VersionInfo;
import io.xol.dop.game.common.nations.NationsColors;
import io.xol.dop.game.server.Server;
import io.xol.dop.game.server.tech.CommandEmitter;
import io.xol.dop.game.server.tech.ServerConsole;
import io.xol.dop.game.server.tech.UsersPrivileges;
import io.xol.engine.misc.ColorsTools;
import io.xol.engine.misc.HttpRequests;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

//(c) 2014 XolioWare Interactive

public class ServerConnections extends Thread{
	
	boolean running = true;
	
	//network aspect
	ServerSocket serverSocket;
	public List<ServerClient> clients = new ArrayList<ServerClient>();
	public static String ip = "none";
	
	public int maxClients = Server.serverConfig.getIntProp("maxusers","32");
	
	String hostname = HttpRequests.sendPost("http://dop.xol.io/api/sayMyName.php?host=1", "");
	
	public void start()
	{
		try
		{
			serverSocket = new ServerSocket(Server.serverConfig.getIntProp("server-port", "30410"));
			Server.log.info("Started server on port " + serverSocket.getLocalPort()+ ", ip=" + serverSocket.getInetAddress());
			ip = HttpRequests.sendPost("http://dop.xol.io/api/sayMyName.php?ip=1", "");//serverSocket.getInetAddress().getHostAddress();
			super.start();
		}
		catch (IOException e)
		{
			Server.log.severe("Can't open server socket. Double check that there is no other instance already running or an application using server port.");
			System.exit(-1);
		}

		this.setName("Server Connections Main Thread");
	}
	
	public void close()
	{
		running = false;
		try{
			serverSocket.close();
		} catch (IOException e) {
			Server.log.severe("An unexpected error happened during network stuff. More info below.");
			e.printStackTrace();
		}
	}
	
	public void run() {
		while (running) {
			try {
				Socket sock = serverSocket.accept();
				//Server.log.info("Accepted connection from "+sock.getInetAddress().getHostAddress()+":"+sock.getLocalPort());
				addClient(new ServerClient(sock));
				
			} catch (IOException e) {
				Server.log.severe("An unexpected error happened during network stuff. More info below.");
				e.printStackTrace();
			}
		}
	}
	
	/*
	 *  Network shit - moved !
	 */
	
	public synchronized void handle(ServerClient c, String in) {
		//Non-login mandatory requests
		
		if(in.startsWith("login/"))
			c.handleLogin(in.substring(6,in.length()));
		if(in.startsWith("info"))
			sendIntel(c);
		//Checks for auth
		if(!c.authentificated)
			return;
		//Login-mandatory requests ( you need to be authentificated to use them )
		if (in.equals("co/off")) {
			c.close();
			clients.remove(c);
		}
		if(in.startsWith("player/"))
			c.handlePlayer(in.substring(7,in.length()));
		if(in.startsWith("world/"))
		{
			if(Server.game != null)
				Server.game.handleWorldRequest(c, in.substring(6,in.length()));
			else
				c.send("world/nullworld");
		}
		if(in.startsWith("gameMode/"))
		{
			if(Server.game != null)
			{
				if(Server.game.gameMode != null)
					Server.game.gameMode.handlePacket(Server.game, c, in.substring(9,in.length()));
			}
		}
		if(in.startsWith("player/"))
		{
			if(c.profile != null)
				c.profile.handlePacket(Server.game, c, in.substring(7,in.length()));
		}
		if(in.startsWith("chat/"))
		{
			String chatMsg = in.substring(5, in.length());
			if(chatMsg.startsWith("/"))
			{
				ServerConsole.handleCommand(chatMsg.substring(1, chatMsg.length()), new CommandEmitter(c.name));
			}
			else if(chatMsg.length() > 0)
			{
				if(c.profile != null && c.profile.nation != -1 && Server.game != null && Server.game.nations != null && Server.game.nations[c.profile.nation] != null)
					sendAllChat("#"+ColorsTools.rgbToHex(NationsColors.getColor(Server.game.nations[c.profile.nation].color))+c.name+"#FFFFFF > "+chatMsg);
				else
					sendAllChat(c.name+/*" ("+c.getIp()+") */" > "+chatMsg);
			}
		}
		//Debug
		//System.out.println(ColorsTools.convertToAnsi("Client " + c.getHost()+" sent "+in));
	}

	private void sendIntel(ServerClient c) {
		c.send("info/name:"+Server.serverConfig.getProp("server-name","unnamedserver@"+hostname));
		c.send("info/motd:"+Server.serverConfig.getProp("server-desc","Default description."));
		c.send("info/connected:"+Server.handler.getNumberOfConnectedClients()+":"+maxClients);
		c.send("info/version:"+VersionInfo.version);
		if(Server.game != null)
			c.send("info/game:"+Server.game.gameName+":"+Server.game.world.name+":"+Server.game.gameMode.getGMType().getId());
		else
			c.send("info/nogame");
		c.send("info/done");
	}

	public void sendAllChat(String chat) {
		Server.log.info(ColorsTools.convertToAnsi(chat));
		sendAllRaw("chat/"+chat);
	}
	public void sendAllRaw(String raw) {
		for (ServerClient c : clients) {
			if(c.authentificated)
				c.send(raw);
		}
	}
	public synchronized void addClient(ServerClient serverClient) {
		serverClient.open();
		serverClient.start();
		clients.add(serverClient);
		
		//Check for banned ip
		if(UsersPrivileges.isIpBanned(serverClient.getIp()))
			disconnectClient(serverClient, "Banned IP address - "+serverClient.getIp());
	}
	public synchronized void disconnectClient(ServerClient serverClient,String message) {
		serverClient.send("disconnect/"+message);
		disconnectClient(serverClient);
	}
	public synchronized void disconnectClient(ServerClient serverClient) {
		serverClient.close();
		//serverClient.stop();
		clients.remove(serverClient);
	}
	public synchronized void disconnectClient(String ip) {
		ServerClient c = null;
		for(ServerClient sc : clients)
		{
			if(sc.getIp().equals(ip))
				c = sc;
		}
		if(c != null)
			disconnectClient(c);
	}

	public void closeAll() {
		List<ServerClient> disconnectEm = new ArrayList<ServerClient>();
		for(ServerClient sc : clients)
		{
			disconnectEm.add(sc);
		}
		for(ServerClient sc : disconnectEm)
		{
			disconnectClient(sc,"Server Closing");
		}
	}
	
	/*
	 *  END NETWORK SHIT
	 */
	
	public int getNumberOfConnectedClients()
	{
		return clients.size();
	}

	public ServerClient getClientByName(String name) {
		ServerClient him = null;
		for(ServerClient c : clients)
		{
			if(c.authentificated)
			{
				if(c.name.equals(name))
					him = c;
			}
		}
		return him;
	}
	
	public void dispatchWorldUpdate(int posX, int posY, String msg)
	{
		for(ServerClient c : clients)
		{
			if(c.isViewingSector(posX, posY))
			{
				c.send(msg);
			}
		}
	}
}
