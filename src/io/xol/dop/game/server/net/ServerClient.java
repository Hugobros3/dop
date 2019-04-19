package io.xol.dop.game.server.net;

import io.xol.dop.game.Sector;
import io.xol.dop.game.common.VersionInfo;
import io.xol.dop.game.common.nations.Nation;
import io.xol.dop.game.mode.GameModeContinuousGame;
//import io.xol.dop.game.common.nations.NationsColors;
import io.xol.dop.game.server.Server;
import io.xol.dop.game.server.ServerPlayer;
import io.xol.dop.game.server.tech.UsersPrivileges;
//import io.xol.engine.misc.ColorsTools;
import io.xol.engine.misc.HttpRequestThread;
import io.xol.engine.misc.HttpRequester;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.codec.binary.Base64;

//(c) 2014 XolioWare Interactive

public class ServerClient extends Thread implements HttpRequester {

	Socket sock;
	public int id = 0;
	DataInputStream in = null;
	DataOutputStream out = null;

	boolean validToken = false;
	public boolean authentificated = false;
	boolean died = false;
	boolean alreadyKilled = false;
	
	public String name = "undefined";
	public String version = "undefined";
	
	public ServerPlayer profile;
	
	String token = "undefined";
	
	ServerClient(Socket s) {
		sock = s;
		id = s.getPort();
		this.setName("Client thread "+id);
	}

	//Here's the usefull things !
	
	public void handleLogin(String m)
	{
		if(m.startsWith("username:"))
		{
			this.name = m.replace("username:","");
		}
		if(m.startsWith("logintoken:"))
		{
			token = m.replace("logintoken:","");
		}
		if(m.startsWith("version:"))
		{
			version = m.replace("version:","");
			if(Server.serverConfig.getProp("check-version", "true").equals("true"))
			{
				if(!version.equals(VersionInfo.version))
					Server.handler.disconnectClient(this, "Wrong version ! "+version+" != "+VersionInfo.version);
			}
		}
		if(m.startsWith("confirm"))
		{
			if(name.equals("undefined"))
				return;
			if(UsersPrivileges.isUserBanned(name))
			{
				Server.handler.disconnectClient(this,"Banned username - "+name);
				return;
			}
			if(token.length() != 20)
			{
				Server.handler.disconnectClient(this,"No valid token supplied");
				return;
			}
			if(Server.serverConfig.getIntProp("offline-mode", "0") == 1)
			{
				//Offline-mode !
				System.out.println("Warning : Offline-mode is on, letting "+this.name+" connecting without verification");
				authentificated = true;
				profile = new ServerPlayer(this);
				Server.handler.sendAllChat("#FFD000"+name+" ("+getIp()+")"+" joined.");
				send("login/ok");
			}
			else
				new HttpRequestThread(this, "checktoken", "http://dop.xol.io/api/serverTokenChecker.php", "username="+this.name+"&token="+token).start();
		}
	}

	public void handlePlayer(String msg) {
		if(msg.equals("info"))
		{
			this.sendPlayerInfo();
		}
		if(msg.equals("nation"))
		{
			//Listing nations
			Nation[] nations = Server.game.nations;
			String list = "";
			for(int i = 0; i < nations.length; i++)
			{
				if(nations[i] != null)
				{
					list+=/*"#"+ColorsTools.rgbToHex(NationsColors.getColor(nations[i].color))+*/nations[i].name;
				}
				else
					list+="null";
				list+="/";
			}
			send("nation/list:"+list);
			//System.out.println("nation/list:"+list);
		}
		if(msg.equals("nationInfo"))
		{
			//Listing nations
			Nation[] nations = Server.game.nations;
			String listD = "";
			String listP = "";
			String listC = "";
			for(int i = 0; i < nations.length; i++)
			{
				if(nations[i] != null)
				{
					listD+=nations[i].desc;
					if(nations[i].desc.equals(""))
						listD+="null";
					listP+="No players info yet";
					listC+=nations[i].color;
				}
				else
				{
					listD+="null";
					listP+="null";
					listC+="null";
				}
				listD+="/";
				listP+="/";
				listC+="/";
			}
			send("nation/desc:"+listD);
			send("nation/players:"+listP);
			send("nation/colors:"+listC);
			//System.out.println("nation/list:"+list);
		}
		if(msg.startsWith("joinNation"))
		{
			String[] data = msg.split(":");
			if(data.length == 2)
			{
				int nationID = Integer.parseInt(data[1]);
				Nation nation = Server.game.nations[nationID];
				if(nation != null)
				{
					this.profile.nation = nationID;
					sendPlayerInfo();
				}
			}
		}
		if(msg.startsWith("nationFlags"))
		{
			Path path = Paths.get("games/"+Server.game.gameName+"/nations/logos.png");
			try {
				byte[] data = Files.readAllBytes(path);
				String logos = Base64.encodeBase64String(data);
				send("nation/flags:"+logos);
				//System.out.println("sent logos in b64 ("+logos.length()+")");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(msg.startsWith("makeNation"))
		{
			String[] data = msg.split(":");
			//System.out.println("lol"+data.length);
			if(data.length == 6)
			{
				//System.out.println("lol");
				int color = Integer.parseInt(data[4]);
				Nation newNation = new Nation(data[1],color,Server.game);
				if(!data[2].equals("null"))
					newNation.desc = data[2];
				if(!data[3].equals("null"))
					newNation.password = data[3];
				newNation.founder = this.name;
				int id = Server.game.addNation(newNation);
				System.out.println("id:"+id);
				if(id != -1 && !data[5].equals("none"))
				{
					File f = new File("./games/"+Server.game.gameName+"/nations/logo_nation_"+id+".png");
					if(f.exists())
						f.delete();
					try{
						FileOutputStream fos = new FileOutputStream(f);
						fos.write(Base64.decodeBase64(data[5]));
						fos.close();
						//System.out.println("File written");
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				if(id != -1)
				{
					send("nation/nationAdded");
				}
			}
		}
	}
	
	//Just socket bullshit !
	public void run() {
		//Server.log.info("Client " + id + " handling thread started properly.");
		while (!died) {
			try {
				Server.handler.handle(this, in.readUTF());
			} catch (IOException e) {
				died = true;
				//System.out.println("Socket "+id+" ("+getIp()+") died ("+e.getClass().getName()+")");
			}
		}
		//close();
		Server.handler.disconnectClient(this);
	}

	public void send(String msg) {
		try {
			out.writeUTF(msg);
			out.flush();
		} catch (IOException ioe) {
		}
	}

	public String getIp() {
		return sock.getInetAddress().getHostAddress();
	}

	public String getHost() {
		return sock.getInetAddress().getHostName();
	}

	public void open() {
		try {
			in = new DataInputStream(new BufferedInputStream(
					sock.getInputStream()));
			out = new DataOutputStream(new BufferedOutputStream(
					sock.getOutputStream()));
		} catch (Exception e) {
		}
	}

	public void close() {
		if(alreadyKilled)
			return;
		if(authentificated)
			Server.handler.sendAllChat("#FFD000"+name+" ("+getIp()+") left.");
		if(profile != null)
			profile.save();
		try {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		} catch (Exception e) {
		}
		alreadyKilled = true;
	}
	
	public void sendPlayerInfo()
	{
		send("player/nation:"+profile.nation);
		if(Server.game != null && Server.game.gameMode instanceof GameModeContinuousGame)
		{
			//GameModeContinuousGame gm = (GameModeContinuousGame) Server.game.gameMode;
			send("info/gtu:"+Server.game.gamecfg.getIntProp("ticks-per-hour", "3600"));
		}
		if(this.profile != null && this.profile.nation != -1)
		{
			Nation pNation = Server.game.nations[this.profile.nation];
			if(pNation != null)
			{
				send("info/playerNation:"+pNation.funds+":"+pNation.population);
				//System.out.println("sent player info2");
			}
		}
	}

	public void sendChat(String msg) {
		send("chat/"+msg);
	}
	
	public void sendSector(Sector sec, int x, int y) { // Sector updating function
		String b64data = sec.saveData();
		this.send("world/sector:"+x+":"+y+":"+Base64.encodeBase64String(b64data.getBytes()));
	}
	
	public boolean isViewingSector(int x, int y)
	{
		if(!authentificated)
			return false;
		if(profile == null)
			return false;
		/*else
			return true;*/
		//System.out.println(x+":"+y+"lol"+x%32+":"+y%32);
		return profile.loadedSectors.containsKey(x/32+":"+y/32);
	}
	
	public void handleHttpRequest(String info, String result) {
		//System.out.println("Request "+info+" got answered: "+result);
		if(info.equals("checktoken")){
			if(result.equals("ok")){
				authentificated = true;
				profile = new ServerPlayer(this);
				Server.handler.sendAllChat("#FFD000"+name+" ("+getIp()+")"+" joined.");
				send("login/ok");
			}
			else{
				Server.handler.disconnectClient(this,"Invalid session id !");
			}
		}
	}
}
