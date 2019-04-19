package io.xol.dop.game.mode;

import io.xol.dop.game.Sector;
import io.xol.dop.game.World;
import io.xol.dop.game.client.bits.ControlBits;
import io.xol.dop.game.client.bits.NationsInfo;
import io.xol.dop.game.client.scenes.GameScene;
import io.xol.dop.game.common.nations.Nation;
import io.xol.dop.game.server.Server;
import io.xol.dop.game.server.game.ServerGame;
import io.xol.dop.game.server.net.ServerClient;
import io.xol.dop.game.units.Unit;

//(c) 2014 XolioWare Interactive

public abstract class GameModeBasicGame extends GameMode {

	int tickCounter = 0;
	
	@Override
	public GameModeType getGMType() {
		return null;
	}

	public long tickServer(ServerGame game) {
		World w = game.world;
		w.time++;
		if(w.time > 2400)
		{
			//A day passed !
			w.time = 0;
		}
		Server.handler.sendAllRaw("world/time:"+w.time);
		tickCounter++;
		if(tickCounter > 100)
			tickCounter = 0;
		//First clean all the nations population and buildings list to recompute it !
		for(Nation n : game.nations)
		{
			if(n != null)
			{
				n.population = 0;
				n.ownedBuildings = 0;
			}
		}
		//Check all sectors and update their units!
		for(int secX = 0; secX < w.width; secX++)
		{
			for(int secY = 0; secY < w.height; secY++)
			{
				Sector s = w.sectors[secX][secY];
				if(s != null)
				{
					s.tick(tickCounter,game);
				}
			}
		}
		//Then compute the money income/outcomes
		for(Nation n : game.nations)
		{
			if(n != null)
			{
				//n.funds = 0;
				n.funds += n.population*Float.parseFloat(game.gamecfg.getProp("funds-pop-tick", "0.1"));
			}
		}
		return 0l;
	}
	
	public void handlePacket(ServerGame game, ServerClient c, String msg) {
		if(msg.startsWith("makeUnit"))
		{
			if(c.profile != null && c.profile.nation != -1)
			{
				String[] data = msg.split(":");
				int unitID = Integer.parseInt(data[3]);
				if(unitID < Unit.unitTypesC && Unit.unitCosts[unitID] <= game.nations[c.profile.nation].funds)
				{
					game.nations[c.profile.nation].funds-= Unit.unitCosts[unitID];
					//System.out.println(data[3]+":"+msg);
					Unit addmeh = Unit.makeUnit(unitID);
					addmeh.init(Integer.parseInt(data[1]), Integer.parseInt(data[2]),game.world);
					addmeh.nation = c.profile.nation;
					game.world.getSector(addmeh.posX, addmeh.posY).addUnit(addmeh,addmeh.posX%32,addmeh.posY%32);
					//System.out.println("Added unit "+addmeh.toString()+" at "+addmeh.posX);
					//game.sendSector(c,addmeh.posX/32,addmeh.posY/32);
					game.updateSector(addmeh.posX/32,addmeh.posY/32);
					c.sendPlayerInfo();
				}
			}
			else
				c.send("chat/Error : null nation");
		}
		else if(msg.startsWith("moveUnit"))
		{
			String[] data = msg.split(":");
			//System.out.println(data[3]+":"+msg);
			
			Unit moveMeh = game.world.getUnitAt(Integer.parseInt(data[1]),Integer.parseInt(data[2]));
			if(moveMeh != null)
				moveMeh.requestMove(Integer.parseInt(data[3]), Integer.parseInt(data[4]));
			
			//c.send("chat/Move request taken");
		}
		else if(msg.startsWith("conquerUnit"))
		{
			String[] data = msg.split(":");
			Unit unit = game.world.getUnitAt(Integer.parseInt(data[1]),Integer.parseInt(data[2]));
			if(unit != null)
				unit.requestConquer(Integer.parseInt(data[3]), Integer.parseInt(data[4]));
			//c.send("chat/Move request taken");
		}
		else if(msg.startsWith("attackUnit"))
		{
			String[] data = msg.split(":");
			//System.out.println(data[3]+":"+msg);
			Unit attacker = game.world.getUnitAt(Integer.parseInt(data[1]),Integer.parseInt(data[2]));
			if(attacker != null)
			{
				Unit target = game.world.getUnitAt(Integer.parseInt(data[3]),Integer.parseInt(data[4]));
				if(target != null)
				{
					if(target.isAttackableBy(attacker))
						attacker.requestAttack(target,0);
					else
						c.sendChat("#FF0000You can't attack this unit.");
				}
			}
			//c.send("chat/Move request taken");
		}
		super.handlePacket(game,c, msg);
	}
	
	public void initClient(GameScene scene)
	{
		scene.control = new ControlBits(scene);
		scene.nations = new NationsInfo(scene);
	}
}
