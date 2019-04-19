package io.xol.dop.game;

import io.xol.dop.game.common.nations.Nation;
import io.xol.dop.game.server.game.ServerGame;
import io.xol.dop.game.tiles.InteractiveTile;
import io.xol.dop.game.tiles.Tile;
import io.xol.dop.game.tiles.TileConquerable;
import io.xol.dop.game.tiles.TileTown;
import io.xol.dop.game.tiles.TileVillage;
import io.xol.dop.game.units.Unit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Time;

import org.lwjgl.opengl.GL11;

//(c) 2014 XolioWare Interactive

public class Sector {
	
	// A sector defines a 32x32 part of the map.
	// It's used for saving and splitting the game data into little loadable chunks.
	// Just like minecraft does in fact.
	
	//List<Unit> units = new ArrayList<Unit>();
	
	int data[][];
	//used for daynight cycle
	public byte lightLevel[][] = new byte[32][32];
	
	Unit[] units = new Unit[1024];
	
	InteractiveTile[] interactiveTiles = new InteractiveTile[1024];
	
	public long lastUpdate = 0;
	
	public int secx,secy;
	public World world;
	
	boolean needReRender = true;
	public int displayL = -1;
	
	public Sector(World w,int x,int y)
	{
		world = w;
		secx = x;
		secy = y;
		data = new int[32][32];
	}
	
	public int getDataAt(int x,int y)
	{
		if(secx < 0 || secy < 0 )
		{
			System.out.println("fatal error, < 0 sector !!!!!!!!");
			return 0 ;
		}
		return data[x%32][y%32];
	}
	public void setDataAt(int x,int y,int d)
	{
		if(x < 0 || y < 0)
		{
			//System.out.println("big bug : "+x+":"+y);
			return;
		}
		data[x-secx*32][y-secy*32] = d;
		lastUpdate = System.currentTimeMillis();
		needReRender = true;
	}

	public void load(File loadme) {
		try{
			InputStream ips = new FileInputStream(loadme);
			InputStreamReader ipsr = new InputStreamReader(ips, "UTF-8");
			BufferedReader br = new BufferedReader(ipsr);
			String completeText = "";
			String line;
			while ((line = br.readLine()) != null) {
				completeText+=line+"\n";
			}
			loadData(completeText);
			ips.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void save(File saveme)
	{
		try {
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveme)));
			out.write(saveData());
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void render()
	{
		GL11.glCallList(displayL);
	}
	
	public void renderEntities()
	{
		for(Unit u : units)
		{
			if(u != null)
				u.render();
		}
	}

	public boolean needsRenderUpdate() {
		return needReRender;
	}
	public void updated(){
		needReRender = false;
	}
	public void modified(){
		lastUpdate = System.currentTimeMillis();
		needReRender = true;
		//System.out.println("marked dirt lel"+secx+":"+secy);
	}

	//Units data
	
	public void addUnit(Unit u,int x,int y) {
		if(units[(x%32)*32+y%32] != null)
			System.out.println("Warning ! Unit at ["+x+":"+y+"] already exists and is being replaced !");
		units[(x%32)*32+y%32] = u;
	}
	
	public Unit getUnit(int x, int y)
	{
		//System.out.println(x+":"+y);
		return units[(x%32)*32+y%32];
	}

	public void setUnit(int x, int y, Unit u) {
				units[(x%32)*32+y%32] = u;
	}
	
	// Interactive tiles
	
	public void addInteractiveTile(InteractiveTile t,int x,int y) {
		if(interactiveTiles[(x%32)*32+y%32] != null)
			System.out.println("Warning ! InteractiveTile at ["+x+":"+y+"] already exists and is being replaced !");
		interactiveTiles[(x%32)*32+y%32] = t;
	}
	
	public InteractiveTile getInteractiveTile(int x, int y)
	{
		//System.out.println(x+":"+y);
		return interactiveTiles[(x%32)*32+y%32];
	}

	public void setInteractiveTile(int x, int y, InteractiveTile t) {
		
		interactiveTiles[(x%32)*32+y%32] = t;
	}
	
	public void loadData(String string) {
		//reset it anyway
		units = new Unit[1024];
		interactiveTiles = new InteractiveTile[1024];
		String loading = "";
		int row = 0;
		for(String line : string.split("\n"))
		{
			if(!line.startsWith("#"))
			{
				if(line.startsWith("!"))
				{
					loading = line.replace("!", "");
					row = 0;
				}
				else
				{
					if(loading.equals("overworld"))
					{
						int i = 0;
						for(String bit : line.split(" "))
						{
							int tid;
							String dataP = null;
							if(bit.contains(":"))
							{
								tid = Integer.parseInt(bit.split(":")[0]);
								dataP = bit.split(":")[1];
							}
							else
								tid = Integer.parseInt(bit);
							if(Tile.getTileByID(tid).hasInteractiveTile())
							{
								InteractiveTile it = Tile.getTileByID(tid).getInteractiveTile();
								if(dataP != null)
									it.load(dataP);
								it.init(row+this.secx*32, i+this.secy*32, this.world);
								this.addInteractiveTile(it, row, i);
							}
							data[row][i] = tid;
							i++;
						}
						row++;
					}
					if(loading.equals("units"))
					{
						if(line.contains(":"))
						{
							try
							{
								String[] split = line.split(":");
								Unit u = Unit.makeUnit(Integer.parseInt(split[0]));
								u.init(Integer.parseInt(split[1]),Integer.parseInt(split[2]),this.world);
								u.direction = split[3].equals("l");
								u.nation = Integer.parseInt(split[4]);
								u.pv = Integer.parseInt(split[5]);
								if(split.length > 6) // if metadata is present
									u.loadMeta(split[6]);
								addUnit(u,Integer.parseInt(split[1]),Integer.parseInt(split[2]));
								//System.out.println("loaded unit"+u.toString());
							}
							catch(IndexOutOfBoundsException e)
							{
								System.out.println("Error while loading sector : missing a unit data :"+line);
							}
						}
					}
					/*if(loading.equals("tilesdata"))
					{
						if(line.contains(":"))
						{
							try
							{
								String[] split = line.split(":");
								InteractiveTile u = InteractiveTile.makeIT(Integer.parseInt(split[0]));
								u.init(Integer.parseInt(split[1]),Integer.parseInt(split[2]),this.world);
								if(split.length > 3) // if metadata is present
									u.load(split[4]);
								addInteractiveTile(u,Integer.parseInt(split[1]),Integer.parseInt(split[2]));
								//System.out.println("loaded unit"+u.toString());
							}
							catch(IndexOutOfBoundsException e)
							{
								System.out.println("Error while loading sector : missing a unit data :"+line);
							}
						}
					}*/
				}
			}
		}
		world.computeLight();
	}

	@SuppressWarnings("deprecation")
	public String saveData()
	{
		String toSave = "";
		toSave+=("# Sector Data Saving format v1.1"+"\n");
		toSave+=("# Copyright 2014 XolioWare Interactive"+"\n");
		toSave+=("# File written on "+new Time(System.currentTimeMillis()).toGMTString()+"\n");
		//overworld data
		toSave+=("!overworld\n");
		for(int x = 0; x < 32;x++)
		{
			for(int y = 0; y < 32;y++)
			{
				int tid = data[x][y];
				InteractiveTile it = getInteractiveTile(x,y);
				if(it != null)
					toSave+=(tid+":"+it.save()+" ");
				else
					toSave+=(tid+" ");
			}
			toSave+=("\n");
		}
		//units
		toSave+=("!units\n");
		for(Unit u : units)
		{
			if(u != null)
			{
				//System.out.println("saved unit"+u.toString());
				toSave+=u.id+":"+u.posX+":"+u.posY+":";
				toSave+=(u.direction ? "l" : "r")+":";
				toSave+=u.nation+":";
				toSave+=u.pv+":";
				toSave+=u.saveMeta()+"\n";
			}
		}
		/*
		//it
		toSave+=("!tilesdata\n");
		for(InteractiveTile u : interactiveTiles)
		{
			if(u != null)
			{
				//System.out.println("saved unit"+u.toString());
				toSave+=u.id+":"+u.posX+":"+u.posY+":";
				toSave+=u.save()+"\n";
			}
		}
		*/
		return toSave;
	}

	public void tick(int tickCounter,ServerGame game) {
		//Ticks all units in the sector
		for(int i = 0; i < 32*32; i++)
		{
			Unit u = getUnit(i/32, i%32);
			if(u != null && u.lastTick != tickCounter)
			{
				u.tick(tickCounter);
			}
			InteractiveTile it = getInteractiveTile(i/32, i%32);
			if(it != null)
			{
				it.tick();
				if(it instanceof TileConquerable && ((TileConquerable) it).nation != -1)
				{
					Nation n = game.nations[((TileConquerable) it).nation];
					if(n != null)
					{
						if(it instanceof TileTown)
							n.population+= ((TileTown) it).population;
						if(it instanceof TileVillage)
							n.population+= ((TileVillage) it).population;
						n.ownedBuildings++;
					}
				}
			}
		}
	}
}
