package io.xol.dop.game;

import org.apache.commons.codec.binary.Base64;

import io.xol.dop.game.client.Client;
import io.xol.dop.game.client.fx.FXBase;
import io.xol.dop.game.client.fx.FXDamage;
import io.xol.dop.game.tiles.InteractiveTile;
import io.xol.dop.game.tiles.TileConquerable;
import io.xol.dop.game.units.Unit;
//import io.xol.dop.game.client.net.RemoteWorldAccessor;
import io.xol.engine.base.XolioWindow;

//(c) 2014 XolioWare Interactive

public class WorldRemote extends World{

	int camx = -1;
	int camy = -1;
	
	int viewRadius = 0;
	
	//public List<int[]> sectorsToDo = new ArrayList<int[]>();
	//RemoteWorldAccessor accessor;
	
	public boolean connected = false;
	
	public WorldRemote()
	{
		//Just set up that goddamn accessor, he takes care of the rest.
		//accessor = new RemoteWorldAccessor(this);
		//accessor.start();
		Client.connect.remoteWorld = this;
		Client.connect.send("world/info");
	}

	@SuppressWarnings("unused")
	private void loadWorld() {
		//nope
		System.out.println("You shall not load a remote world.");
	}
	
	public void saveWorld() {
		//even moar nope
		System.out.println("You shall not save a remote world.");
	}
	
	public void init(String name, int x, int y) {
		this.name = name;
		this.width = x;
		this.height = y;
		sectors = new Sector[x][y];
		connected = true;
		//accessor.go();
	}
	
	public void updateView(int x, int y)
	{
		//System.out.println("updating view x="+x+"; y="+y);
		if(connected && (camx != x || camy != y || XolioWindow.resized))
		{
			camy = y;
			camx = x;
			Client.connect.send("player/camera:"+camx+":"+camy+":"+XolioWindow.frameW+":"+XolioWindow.frameH);
			//System.out.println("camera/"+camx+":"+camy+":"+XolioWindow.frameW+":"+XolioWindow.frameH);
		}
		
		//calculateSectorsToLoad();
		//viewRadius = Math.max(XolioWindow.frameH,XolioWindow.frameW);
	}
	
	public void loadSector(String string, int x, int y) {
		//System.out.println("sd"+string);
		Sector sec = this.sectors[x][y];
		if(sec == null)
			sec = new Sector(this,x,y);
		sec.loadData(string);
		this.setSector(x, y, sec);
		
		if(this.renderer != null)
		{
			markDirty();
		}
	}

	@Override
	public Sector getSector(int x, int y) {
		int secx = x/32;
		int secy = y/32;
		Sector gimme = sectors[secx][secy];
		if(gimme == null)
		{
			gimme = new Sector(this,secx,secy);
			//System.out.println("Null sector at "+secx+" : "+secy);
		}
		return gimme;
	}
	
	public void setTileAt(int x, int y, int d) {
		if(this.getTileAt(x, y) == d)
			return;
		String packet = "world/set:"+x+":"+y+":"+d;
		//System.out.println("Sending packet :"+packet);
		Client.connect.send(packet);
		super.setTileAt(x, y, d);
	}
	
	public void remove() {
		//accessor.kill();
		sectors = null;
		Client.connect.remoteWorld = null;
		//accessor.stop();
	}

	public void handleWorldMessage(String msg) {
		//System.out.println("rma:"+msg);
		if(msg.startsWith("info"))
		{
			String[] infos = msg.split(":");
			if(infos.length >= 4)
			{
				// world/info:name:width:height
				init(infos[1],Integer.parseInt(infos[2]),Integer.parseInt(infos[3]));
			}
		}
		if (msg.startsWith("time"))
	    {
	      String[] infos = msg.split(":");
	      this.time = Integer.parseInt(infos[1]);
	    }
		if(msg.startsWith("sector"))
		{
			String[] infos = msg.split(":");
			if(infos.length >= 4)
			{
				// world/sector:1:5:b64bullshit
				//System.out.println("load dem sectors ["+Integer.parseInt(infos[1])+":"+Integer.parseInt(infos[2])+"]");
				loadSector(new String(Base64.decodeBase64(infos[3])),Integer.parseInt(infos[1]),Integer.parseInt(infos[2]));
			}
		}
		if(msg.startsWith("set"))
		{
			String[] infos = msg.split(":");
			if(infos.length >= 4)
			{
				int x = Integer.parseInt(infos[1]);
				int y = Integer.parseInt(infos[2]);
				int d = Integer.parseInt(infos[3]);
				//System.out.println("setting tile at"+x+":"+y+" to "+d);
				if(sectorExists(x/32, y/32))
				{
					getSector(x, y).setDataAt(x, y, d);
					markDirty();
				}
			}
		}
		if(msg.startsWith("unitmove"))
		{
			//"world/unitmove:"+posX+":"+posY+":"+destX+":"+destY
			String[] infos = msg.split(":");
			{
				int posX = Integer.parseInt(infos[1]);
				int posY = Integer.parseInt(infos[2]);
				int destX = Integer.parseInt(infos[3]);
				int destY = Integer.parseInt(infos[4]);
				moveUnit(posX,posY,destX,destY);
			}
		}
		//	c.send("world/unitattack:"+attacker.posX+":"+attacker.posY+":"+attackX+":"+attackY+":"+effectiveDMG+":"+pvLeft);
		if(msg.startsWith("unitattack"))
		{
			//"world/unitmove:"+posX+":"+posY+":"+destX+":"+destY
			String[] infos = msg.split(":");
			{
				//int posX = Integer.parseInt(infos[1]);
				//int posY = Integer.parseInt(infos[2]);
				int attackX = Integer.parseInt(infos[3]);
				int attackY = Integer.parseInt(infos[4]);
				int dmg = Integer.parseInt(infos[5]);
				int pvLeft = Integer.parseInt(infos[6]);
				Unit victim = this.getUnitAt(attackX, attackY);
				if(victim != null)
				{
					addEffect(new FXDamage(victim,dmg));
					victim.setHealth(pvLeft);
				}
				
			}
		}
		if(msg.startsWith("unitboom"))
		{
			//"world/unitmove:"+posX+":"+posY+":"+destX+":"+destY
			String[] infos = msg.split(":");
			{
				int posX = Integer.parseInt(infos[1]);
				int posY = Integer.parseInt(infos[2]);
				this.deleteUnitAt(posX, posY, true);
			}
		}
		if (msg.startsWith("unitConquering"))
	    {
	      String[] infos = msg.split(":");

	      int posX = Integer.parseInt(infos[1]);
	      int posY = Integer.parseInt(infos[2]);
	      Unit u = getUnitAt(posX, posY);
	      if (u != null)
	      {
	        u.conqLeft = Integer.parseInt(infos[3]);
	      }

	    }
	    if (msg.startsWith("unitStatus"))
	    {
	      String[] infos = msg.split(":");

	      int posX = Integer.parseInt(infos[1]);
	      int posY = Integer.parseInt(infos[2]);
	      Unit u = getUnitAt(posX, posY);
	      if (u != null)
	      {
	        u.isMoving = infos[3].equals("true");
	        u.isAttacking = infos[4].equals("true");
	      }

	    }
		if(msg.startsWith("changeNation"))
		{
			//"world/unitmove:"+posX+":"+posY+":"+destX+":"+destY
			String[] infos = msg.split(":");
			{
				int posX = Integer.parseInt(infos[1]);
				int posY = Integer.parseInt(infos[2]);
				int n = Integer.parseInt(infos[3]);
				InteractiveTile it = this.getInteractiveTile(posX, posY);
				if(it != null)
				{
					if(it instanceof TileConquerable)
					{
						TileConquerable tc = (TileConquerable)it;
						tc.nation = n;
						Sector sec = getSector(posX,posY);
						sec.modified();
						this.markDirty();
					}
					else
						System.out.println("Warning : not conquerable tile at "+"["+posX+":"+posY+"]");
				}
				else
					System.out.println("Warning : no interactive tile at "+"["+posX+":"+posY+"]");
			}
		}
	}
	
	public void addEffect(FXBase fx)
	{
		if(fxManager != null)
			fxManager.addEffect(fx);
	}
}