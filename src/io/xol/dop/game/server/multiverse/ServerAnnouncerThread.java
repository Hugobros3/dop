package io.xol.dop.game.server.multiverse;

import java.util.Random;

import io.xol.dop.game.common.VersionInfo;
import io.xol.dop.game.server.Server;
import io.xol.engine.misc.HttpRequests;

//(c) 2014 XolioWare Interactive

public class ServerAnnouncerThread extends Thread{

	boolean run = true;
	
	int lolcode = 0;
	public long updatedelay = 0;
	
	public String srv_name;
	public String srv_desc;
	
	public void init()
	{
		lolcode = Server.serverConfig.getIntProp("lolcode", "0");
		if(lolcode == 0L)
		{
			//System.out.println("lolcode = 0");
			Random rnd = new Random();
			lolcode = rnd.nextInt(100000);
			Server.serverConfig.setProp("lolcode", lolcode);
		}
		updatedelay = Long.parseLong(Server.serverConfig.getProp("update-delay", "10000"));
		String hostname = HttpRequests.sendPost("http://dop.xol.io/api/sayMyName.php?host=1", "");
		srv_name = Server.serverConfig.getProp("server-name","unnamedserver@"+hostname);
		srv_desc = Server.serverConfig.getProp("server-desc","Default description.");
		setName("Multiverse thread");
	}
	
	public void flagStop()
	{
		run = false;
	}
	
	public void run() {
		try {
			String ip =  HttpRequests.sendPost("http://dop.xol.io/api/sayMyName.php?ip=1", "");
			while(run)
			{
				//System.out.println("Updating server data on Multiverse.");
				if(Server.serverConfig.getProp("enable-multiverse", "false").equals("true"))
				{
					HttpRequests.sendPost("http://dop.xol.io/api/serverAnnounce.php", "srvname="+srv_name+"&desc="+srv_desc
							+ "&ip="+ip+"&mu="+Server.handler.maxClients+"&u="+Server.handler.getNumberOfConnectedClients()
							+ "&n=0&w=default&p=1&v="+VersionInfo.get()+"&lolcode="+lolcode);
					sleep(updatedelay);
				}
				else
					sleep(6000);
			}
		} catch (Exception e) {
			Server.log.severe("An unexpected error happened during multiverse stuff. More info below.");
			e.printStackTrace();
		}
	}
}
