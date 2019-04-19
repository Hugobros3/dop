package io.xol.dop.game.client.net;

import io.xol.dop.game.WorldRemote;
import io.xol.dop.game.client.Client;
import io.xol.dop.game.common.VersionInfo;
import io.xol.engine.misc.HttpRequestThread;
import io.xol.engine.misc.HttpRequester;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

//(c) 2014 XolioWare Interactive

public class ServerConnection extends Thread implements HttpRequester {
	
	// Network handling class - takes care of connecting to a server and dealing the info with it.
	
	public String ip = "";
	public int port = 30410;
	
	private Socket socket;
	public DataInputStream in;
	public DataOutputStream out;
	
	//Utility for ping server
	boolean whoisMode = false;
	
	//Status check
	boolean connected = false;
	boolean failed = false;
	String latestErrorMessage="";
	public String connectionStatus="Establishing connection...";
	
	//Linking with world accessor
	//public RemoteWorldAccessor worldAccessor;
	public WorldRemote remoteWorld = null;
	
	// Receiving buffers
	
	public List<String> chatReceived = new ArrayList<String>();
	public List<String> techReceived = new ArrayList<String>();
	
	// Code magic here
	boolean die = false;
	boolean dead = false;
	
	public ServerConnection(String i, int p)
	{
		ip = i;
		port = p;
		this.setName("Server Connection thread - "+ip);
		connect();
	}
	
	//Connect on/off
	
	public boolean connect()
	{
		System.out.println("Connecting to "+ip+":"+port+".");
		try{
			socket = new Socket(ip,port);
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			connectionStatus = "Established, waiting for login token...";
			this.start();
			send("info");
			auth();
			connected = true;
			return true;
		}
		catch(Exception e)
		{
			failed = true;
			latestErrorMessage = "Failed to connect to "+ip+":"+port+". ("+e.getClass().getName()+")";
			System.out.println(latestErrorMessage);
			//e.printStackTrace();
			return false;
		}
	}
	
	//@SuppressWarnings("deprecation")
	public void close()
	{
		if(dead)
			return;
		dead=true;
		try{
			in.close();
			out.close();
			socket.close();
			connected=false;
			die=true;
		}
		catch(Exception e)
		{
			System.out.println("Couldn't close connection to "+ip+":"+port+". ("+e.getClass().getName()+")");
			//e.printStackTrace();
		}
	}
	
	// I/O
	
	public void handle(String msg)
	{
		//System.out.println("m:"+msg); //debug
		if(msg.startsWith("chat/"))
			chatReceived.add(msg.substring(5,msg.length()));
		if(msg.startsWith("world/"))
		{
			if(remoteWorld != null)
				remoteWorld.handleWorldMessage(msg.substring(6,msg.length()));
			else
				System.out.println("Received a message about the world, but no remote world exists as of now...\nFaulty message : \n"+msg.substring(6,msg.length()));
		}
		if(msg.startsWith("disconnect/"))
		{
			latestErrorMessage = msg.replace("disconnect/", "");
			failed = true;
			close();
		}
		else
			techReceived.add(msg);
	}
	
	public void send(String msg)
	{
		try{
			//System.out.println(">"+msg); //debug
			out.writeUTF(msg);
		}
		catch(Exception e)
		{
			//close();
			System.out.println("Fatal error while handling connection to "+ip+":"+port+". ("+e.getClass().getName()+")");
			e.printStackTrace();
		}
	}
	//accessor
	
	public synchronized String getLastChatMessage()
	{
		if(chatReceived.size() > 0)
		{
			String m = chatReceived.get(0);
			chatReceived.remove(0);
			return m;
		}
		return null;
	}
	
	public synchronized String getLastTechMessage()
	{
		if(techReceived.size() > 0)
		{
			String m = techReceived.get(0);
			techReceived.remove(0);
			return m;
		}
		return null;
	}
	
	//auth
	private void auth() {
		if(Client.offline)
		{
			//If online-mode, send dummy info
			send("login/start");
			
			send("login/username:"+Client.username);
			send("login/logintoken:nopenopenopenopenope");
			send("login/version:"+VersionInfo.version);
			send("login/confirm");
		}
		else
		{
			//Before sending the server the info it needs for authentification we need a valid login token so
			//we fire up a http request to grab one
			new HttpRequestThread(this, "token", "http://dop.xol.io/api/serverTokenObtainer.php", "username="+Client.username+"&sessid="+Client.session_key).start();
		}
	}

	//run
	public void run()
	{
		while (!die)
		{
			//Just wait for the goddamn packets to come !
			try
			{
				handle(in.readUTF());
			}
			catch(Exception e)
			{
				if(!die) // If the thread was killed then there is no point handling the error.
				{
					close();
					failed = true;
					latestErrorMessage = "Fatal error while handling connection to "+ip+":"+port+". ("+e.getClass().getName()+")";
					System.out.println(latestErrorMessage);
					e.printStackTrace();
				}
			}
		}
		System.out.println("Letting thread die as it finished it's job.");
	}

	public boolean hasFailed() {
		return failed;
	}

	public String getLatestErrorMessage() {
		return this.latestErrorMessage;
	}
	
	public void handleHttpRequest(String info, String result) {
		//System.out.println("Request "+info+" got answered: "+result);
		if(info.equals("token")){
			if(result.startsWith("ok"))
			{
				String token = result.split(":")[1];
				
				send("login/start");
				
				send("login/username:"+Client.username);
				send("login/logintoken:"+token);
				send("login/version:"+VersionInfo.version);
				send("login/confirm");
				connectionStatus = "Token obtained, logging in...";
			}
			else
			{
				close();
				failed = true;
				latestErrorMessage = "Could not obtain token from DoP servers ( "+result+" ).";
			}
		}
	}
}
