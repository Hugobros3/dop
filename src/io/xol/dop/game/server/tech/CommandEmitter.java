package io.xol.dop.game.server.tech;

import io.xol.dop.game.server.Server;
import io.xol.dop.game.server.net.ServerClient;
import io.xol.engine.misc.ColorsTools;

//(c) 2014 XolioWare Interactive

public class CommandEmitter {

	boolean isConsole;
	String name;
	
	public CommandEmitter()
	{
		isConsole = true;
		name = "CONSOLE";
	}
	
	public CommandEmitter(String n)
	{
		isConsole = false;
		name = n;
	}
	
	public void reply(String msg)
	{
		if(isConsole)
		{
			System.out.println(ColorsTools.convertToAnsi("#FF00FF"+msg));
		}
		else
		{
			ServerClient client = Server.handler.getClientByName(name);
			if(client != null)
				client.send("chat/#FF00FF"+msg);
		}
	}
	
	public boolean hasRights(String r)
	{
		if(isConsole)
			return true;
		if(UsersPrivileges.isUserAdmin(name))
			return true;
		return false;
	}
}
