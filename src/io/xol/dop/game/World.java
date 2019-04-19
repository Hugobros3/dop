package io.xol.dop.game;

import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Time;

import javax.imageio.ImageIO;

import io.xol.dop.game.client.fx.EffectsManager;
import io.xol.dop.game.client.renderer.WorldRenderer;
import io.xol.dop.game.server.Server;
import io.xol.dop.game.tiles.InteractiveTile;
import io.xol.dop.game.tiles.Tile;
import io.xol.dop.game.units.Unit;
import io.xol.engine.misc.ColorsTools;
import io.xol.engine.misc.ConfigFile;

//(c) 2014 XolioWare Interactive

public class World {

	public int width = 0; // width in sectors
	public int height = 0; // height in sectors
	public Sector[][] sectors;// All sectors containing the map data
	public String name;
	boolean client = true;
	public ConfigFile worldcfg;
	
	public int time = 0; // 0 to 2400, counts time !
	
	// server-specific shit
	String gameName;
	
	// client-specific shit
	public WorldRenderer renderer; 
	public EffectsManager fxManager;
	boolean needRenderUpdate = false;
	
	public World(){
		// A null initializer. No idea wtf it does... nor why needed...
	}
	
	public World(int w, int h, String n,boolean c) {
		//Creating a new map for editor
		width = w;
		height = h;
		name = n;
		client = c;
		sectors = new Sector[w][h];
		loadWorld();
		if(c)
		{
			renderer = new WorldRenderer(this);
			renderer.updateRender();
		}
	}
	public World(String n,boolean c,String gameName) {
		//That's the initializator of the server
		this.gameName = gameName;
		client = c;
		ConfigFile loadcfg = new ConfigFile(getPrefix()+"/level.cfg");
		width = loadcfg.getIntProp("width", "10");
		height = loadcfg.getIntProp("height", "10");
		name = n;
		sectors = new Sector[width][height];
		loadWorld();
	}
	public World(String n,boolean c) {
		//When loading an already existing map
		ConfigFile loadcfg = new ConfigFile(getPrefix()+"/level.cfg");
		width = loadcfg.getIntProp("width", "10");
		height = loadcfg.getIntProp("height", "10");
		name = n;
		client = c;
		sectors = new Sector[width][height];
		loadWorld();
		if(c)
		{
			renderer = new WorldRenderer(this);
			renderer.updateRender();
		}
	}
	//Saving/loading
	@SuppressWarnings("deprecation")
	private void loadWorld() {
		checkFolders(); // Checks and create them
		//load or create world config file
		worldcfg = new ConfigFile(getPrefix()+"/level.cfg");
		worldcfg.setProp("LastOpened", new Time(System.currentTimeMillis()).toGMTString());
		worldcfg.setProp("width", width);
		worldcfg.setProp("height", height);
		worldcfg.setProp("name", name);
		worldcfg.save();
		//Loop load any Sector file
		File dir = new File(System.getProperty("user.dir") + "/" + getPrefix() + "/sectors/");
		for(File loadme : dir.listFiles())
		{
			if(loadme.getName().endsWith(".sector"))
			{
				String[] coords = loadme.getName().split("[.]");
				int secx = Integer.parseInt(coords[0]);
				int secy = Integer.parseInt(coords[1]);
				Sector load = new Sector(this,secx,secy);
				load.load(loadme);
				sectors[secx][secy] = load;
			}
		}
		computeLight();
	}

	public void saveWorld()
	{
		System.out.println("Saving world \""+name+"\"...");
		worldcfg.setProp("name", name);
		worldcfg.save();
		for(int x = 0; x < width;x++)
		{
			for(int y = 0; y < height; y++)
			{
				Sector sec = sectors[x][y];
				if(sec != null)
				{
					File saveme = new File(System.getProperty("user.dir") + "/" + getPrefix() + "/sectors/"+x+"."+y+".sector");
					sec.save(saveme);
				}
			}
		}
	}
	//Tiles direct access
	public int getTileAt(int x, int y) {
		if (x < 0 || y < 0)
			return 0;
		if (x > width*32 - 1 || y > height*32 - 1)
			return 0;
		Sector sector = getSector(x,y);
		
		return sector.getDataAt(x, y);
	}

	public void setTileAt(int x, int y, int d) {
		
		if (x < 0 || y < 0)
			return;
		if (x > width*32 - 1 || y > height*32 - 1)
			return;
		Sector sec = getSector(x,y);
		sec.setDataAt(x, y, d);
		//Update neightbours sectors on edge editing
		if(x/32+1 < width && x % 32 == 31 && sectorExists(x/32+1,y/32))
			getSector(x+1,y).modified();
		if(y/32+1 < height && y % 32 == 31 && sectorExists(x/32,y/32+1))
			getSector(x,y+1).modified();
		if(x/32-1 >= 0 && x % 32 == 0 && sectorExists(x/32-1,y/32))
			getSector(x-1,y).modified();
		if(y/32-1 >= 0 && y % 32 == 0 && sectorExists(x/32,y/32-1))
			getSector(x,y-1).modified();
		
		if(client)
			renderer.updateRender();
	}
	
	// Units access
	
	public Unit getUnitAt(int x, int y) {
		if (x < 0 || y < 0)
			return null;
		if (x > width*32 - 1 || y > height*32 - 1)
			return null;
		Sector sector = getSector(x,y);
		
		return sector.getUnit(x, y);
	}
	
	// Sectors access
	
	public boolean sectorExists(int x, int y)
	{
		if(x >= width || y >= height)
			return false;
		Sector gimme = sectors[x][y];
		if(gimme == null)
			return false;
		return true;
	}
	
	public Sector getSector(int x, int y) {
		int secx = x/32;
		int secy = y/32;
		Sector gimme = sectors[secx][secy];
		if(gimme == null)
		{
			gimme = new Sector(this,secx,secy);
			sectors[secx][secy] = gimme;
			System.out.println("Created sector at "+secx+" : "+secy);
		}
		return gimme;
	}

	public void setSector(int secx, int secy,Sector newd) {
		sectors[secx][secy] = newd;
		//Update neightbours too
		if(secx+1 < width && sectorExists(secx+1,secy))
			getSector((secx+1)*32,(secy)*32).modified();
		if(secy+1 < height && sectorExists(secx,secy+1))
			getSector((secx)*32,(secy+1)*32).modified();
		if(secx-1 >= 0 && sectorExists(secx-1,secy))
			getSector((secx-1)*32,(secy)*32).modified();
		if(secy-1 >= 0 && sectorExists(secx,secy-1))
			getSector((secx)*32,(secy-1)*32).modified();
	}

	public synchronized void markDirty()
	{
		needRenderUpdate = true;
	}
	
	public synchronized boolean markClean()
	{
		boolean was = needRenderUpdate;
		needRenderUpdate = false;
		return was;
	}
	
	//Util
	private String getPrefix()
	{
		if(this.client)
			return "levels/"+name;
		else
			return "games/"+gameName+"/level";
	}
	private void checkFolders()
	{
		//check basic structure
		File folders = new File(System.getProperty("user.dir") + "/levels/");
		if(!folders.exists())
			folders.mkdir();
		//check if level folder exists
		File folder = new File(System.getProperty("user.dir") + "/" + getPrefix());
		if(!folder.exists())
			folder.mkdir(); // make it
		//check if level's sectors folder exists
		File foldersec = new File(System.getProperty("user.dir") + "/" + getPrefix() + "/sectors");
		if(!foldersec.exists())
			foldersec.mkdir(); // make it
	}

	public void moveUnit(int posX, int posY, int destX, int destY) {
		if(sectorExists(destX/32,destY/32))
		{
			Sector depSec = getSector(posX,posY);
			Unit u = depSec.getUnit(posX, posY);
			
			Unit u2 = depSec.getUnit(destX, destY);
			
			if(u != null)
			{
				if(u2 == null)
				{
					depSec.setUnit(posX, posY, null);
					
					if(posX != destX)
						u.direction = posX > destX;
					
					u.posX = destX;
					u.posY = destY;
					
					Sector destSec = getSector(destX,destY);
					destSec.setUnit(destX, destY, u);
					//System.out.println("unit moved : "+posX+":"+posY+" -> "+destX+":"+destY);
					if(!client)
					{
						Server.handler.dispatchWorldUpdate(posX,posY,"world/unitmove:"+posX+":"+posY+":"+destX+":"+destY);
						Server.handler.dispatchWorldUpdate(destX, destY,"world/unitmove:"+posX+":"+posY+":"+destX+":"+destY);
						/*for(ServerClient c : Server.handler.clients)
						{
							if(c.isViewingSector(destX, destY) || c.isViewingSector(posX, posY))
							{
								//System.out.println("world/unitmove:"+posX+":"+posY+":"+destX+":"+destY);
								c.send("world/unitmove:"+posX+":"+posY+":"+destX+":"+destY);
							}
						}*/
					}
					else
					{
						u.animX = (posX-destX)*32;
						u.animY = (posY-destY)*32;
					}
				}
				else
				{
					// Show "STOP" message
				}
			}
		}
	}

	public boolean unitAttack(Unit attacker, int attackX, int attackY) {
		if(sectorExists(attackX/32,attackY/32))
		{
			Sector sec = getSector(attackX,attackY);
			if(sec != null)
			{
				Unit target = sec.getUnit(attackX, attackY);
				if(target != null)
				{
					float dmg = attacker.getDamage(target);
					dmg = dmg/attacker.getMaxHealth()*attacker.getHealth();
					//System.out.println("dmg to inflict : "+dmg+" pv ="+attacker.getHealth());
					int effectiveDMG = target.dealDamage((int)dmg, attacker);
					int pvLeft = target.getHealth();
					if(pvLeft <= 0)
						attacker.targetEliminated();
					attacker.updateUnitOnClient();
					if(!client)
					{
						Server.handler.dispatchWorldUpdate(attackX, attackY,"world/unitattack:"+attacker.posX+":"+attacker.posY+":"+attackX+":"+attackY+":"+effectiveDMG+":"+pvLeft);
						/*for(ServerClient c : Server.handler.clients)
						{
							if(c.isViewingSector(attackX, attackY))
							{
								c.send("world/unitattack:"+attacker.posX+":"+attacker.posY+":"+attackX+":"+attackY+":"+effectiveDMG+":"+pvLeft);
							}
						}*/
					}
					else
					{
						System.out.println("clientside attack ?");
					}
					return true;
				}
			}
		}
		return false;
	}

	public void deleteUnitAt(int x, int y, boolean animate)
	{
		setUnitAt(x,y,null);
		if(!client && animate)
		{
			Server.handler.dispatchWorldUpdate(x, y,"world/unitboom:"+x+":"+y);
			/*for(ServerClient c : Server.handler.clients)
			{
				if(c.isViewingSector(x, y))
				{
					c.send("world/unitboom:"+x+":"+y);
				}
			}*/
		}
	}
	
	public void setUnitAt(int x, int y, Unit unit) {
		if (x < 0 || y < 0)
			return;
		if (x > width*32 - 1 || y > height*32 - 1)
			return;
		Sector sector = getSector(x,y);
		sector.setUnit(x, y, unit);
	}
	
	public InteractiveTile getInteractiveTile(int x, int y) {
		if (x < 0 || y < 0)
			return null;
		if (x > width*32 - 1 || y > height*32 - 1)
			return null;
		Sector sector = getSector(x,y);
		return sector.getInteractiveTile(x, y);
	}

	public void globalReRender() {
		for(int x = 0; x < width;x++)
		{
			for(int y = 0; y < height; y++)
			{
				Sector sec = sectors[x][y];
				if(sec != null)
				{
					sec.needReRender = true;
				}
			}
		}
		this.markDirty();
	}

	public void computeLight() {
		for(int x = 0; x < width;x++)
		{
			for(int y = 0; y < height; y++)
			{
				Sector sec = sectors[x][y];
				if(sec != null)
				{
					sec.lightLevel = new byte[32][32];
				}
			}
		}
		for(int x = 0; x < width;x++)
		{
			for(int y = 0; y < height; y++)
			{
				Sector sec = sectors[x][y];
				if(sec != null)
				{
					for(int a = 0; a < 32; a++)
					{
						for(int b = 0; b < 32; b++)
						{
							int l = Tile.getTileByID(sec.getDataAt(a, b)).getLightLevel();
							if(l > 0)
							{
								//System.out.println("Found lightsource at : "+(x*32+a)+":"+(y*32+b)+":"+l);
								lightArround(x*32+a,y*32+b,l);
							}
						}
					}
				}
			}
		}
		//display shit
		/*for(int x = 0; x < width;x++)
		{
			for(int y = 0; y < height; y++)
			{
				Sector sec = sectors[x][y];
				if(sec != null)
				{
					for(int a = 0; a < 32; a++)
					{
						for(int b = 0; b < 32; b++)
						{
							System.out.print(sec.lightLevel[a][b]+" ");
						}
						System.out.print("\n");
					}
				}
			}
		}*/
	}

	private void lightArround(int targetX, int targetY, int l) {
		//Will spread light arround a point
		for(int i = 0; i <= l; i++)
		{
			//System.out.println("cc"+i);
			for(int y = i; y >= -i; y--)
			{
				int cx;
				int cy = targetY+y;
				if(y != -i && y != i)
				{
					cx = targetX+(i-Math.abs(y));
					setLightLevelAt(cx,cy,l-i);
					cx = targetX-(i-Math.abs(y));
					setLightLevelAt(cx,cy,l-i);
				}
				else
				{
					cx = targetX;
					setLightLevelAt(cx,cy,l-i);
				}
			}
		}
	}

	private void setLightLevelAt(int cx, int cy, int i) {
		if(sectorExists(cx/32,cy/32))
		{
			Sector sec = this.getSector(cx, cy);
			cx = cx%32;
			cy = cy%32;
			if(cx >= 0 && cy >= 0)
				sec.lightLevel[cx][cy] = (byte)Math.max(sec.lightLevel[cx][cy], i);
		}
	}

	public float[] getColorForTile(int cx, int cy) {
		if(sectorExists(cx/32,cy/32))
		{
			float[] baseLight = getColorForWorld();
			float multLight = 0.15f;
			Sector sec = this.getSector(cx, cy);
			cx = cx%32;
			cy = cy%32;
			int lightLevel = (int)sec.lightLevel[cx][cy];
			//float finalLight = Math.min(1,baseLight+lightLevel*multLight);
			return new float[]{
					Math.min(1,baseLight[0]+lightLevel*multLight),
					Math.min(1,baseLight[1]+lightLevel*multLight),
					Math.min(1,baseLight[2]+lightLevel*multLight)
					};
		}
		return new float[]{1f,1f,1f};
	}
	
	public float[] getColorForWorld()
	{
		return timeCycleColors[(this.time/10)%240];
	}
	
	public static float[][] timeCycleColors = new float[240][3];
	
	public static void loadTimeCycleColors()
	{
		try{
			File f = new File("./res/textures/misc/timecycle.png");
			if(!f.exists())
				return;
			BufferedImage colorsMap = ImageIO.read(f);
			for(int i = 0; i < 240;i++)
			{
				int[] rgb = ColorsTools.rgbSplit(colorsMap.getRGB(i, 0));
				timeCycleColors[i] = new float[]{rgb[0]/255f,rgb[1]/255f,rgb[2]/255f};
			}
			System.out.println("Timecycle colors loaded.");
		}
		catch(Exception e)
		{
			System.out.println("Fatal error happened during tilecycle colors image file loading.");
			e.printStackTrace();
		}
	}
}
