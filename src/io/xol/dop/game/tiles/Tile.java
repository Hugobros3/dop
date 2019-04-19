package io.xol.dop.game.tiles;

//(c) 2014 XolioWare Interactive

import io.xol.dop.game.units.Unit;

public class Tile {
	
	public static int tileTypesC = 0;
	public static final Tile tileTypes[] = new Tile[256];
	
	//Definitions of all those tiles
	public static Tile WATER = new Tile(0,"Water",TileType.WATER,0,"ocean_0",false,1,null);
	public static Tile PLAINS = new TileLand(1,"Plains",TileType.GROUND,1,"grass",false,2,null);
	public static Tile PINEFOREST = new Tile(2,"Forest",TileType.GROUND,6,"pine",false,8,null);
	public static Tile ROAD = new Tile(3,"Road",TileType.GROUND,2,"road",false,1,null);
	public static Tile MOUNTAIN = new Tile(4,"Mountain",TileType.MOUNTAIN,7,"mount",false,16,null);
	public static Tile VILLAGE = new Tile(5,"Village",TileType.GROUND,8,"village",false,4,new TileVillage());
	public static Tile TOWN = new Tile(6,"Town",TileType.GROUND,8,"town",false,3, new TileTown());
	public static Tile BRIDGE = new Tile(7,"Bridge",TileType.BRIDGE,3,"pont",false,2,null);
	public static Tile BEACH = new Tile(8,"Beach",TileType.SHORE,4,"beach",false,3,null);
	public static Tile FIELDS = new Tile(9,"Fields",TileType.GROUND,1,"farmland",false,2,null);
	public static Tile FARM = new Tile(10,"Farm",TileType.GROUND,1,"farm",false,3,null);
	public static Tile HQ = new Tile(11,"HeadQuarters",TileType.GROUND,1,"hq",true,4,null);
	public static Tile FACTORY = new Tile(12,"Factory",TileType.GROUND,8,"factory",true,3,new TileFactory());
	public static Tile PLANT_POWER = new Tile(13,"Power plant",TileType.GROUND,1,"powerplant",false,3,null);
	public static Tile RAILWAY = new Tile(14,"Railway",TileType.GROUND,2,"railroad",false,2,null);
	/*
	public static Tile BARRACK = new Tile(9,"Barrack",TileType.GROUND,1);
	public static Tile TRAINING = new Tile(10,"Training center",TileType.GROUND,1);
	public static Tile ACADEMY = new Tile(11,"Military Academy",TileType.GROUND,1);
	public static Tile PLANT_AV = new Tile(13,"Aerial vehicles plant",TileType.GROUND,1);
	public static Tile SHIPYARD = new Tile(15,"Shipyard",TileType.GROUND,1);
	public static Tile PORT = new Tile(16,"Port",TileType.GROUND,1);
	public static Tile FIELDS = new Tile(17,"Fields",TileType.GROUND,1);
	public static Tile FARM = new Tile(18,"Farm",TileType.GROUND,1);
	public static Tile RAILWAY = new Tile(19,"Railway",TileType.GROUND,1);
	public static Tile STATION = new Tile(20,"Train station",TileType.GROUND,1);
	public static Tile AIRFIELD = new Tile(21,"Airfield runway",TileType.GROUND,1);
	public static Tile HANGAR = new Tile(22,"Airfield hangar",TileType.GROUND,1);
	public static Tile POWER_RELAY = new Tile(23,"Power relay",TileType.GROUND,1);
	public static Tile TELECOM = new Tile(24,"Telecomunications",TileType.GROUND,1);
	public static Tile WALL = new Tile(25,"Wall",TileType.MOUNTAIN,1);
	public static Tile WATCHTOWER = new Tile(26,"Watch tower",TileType.MOUNTAIN,1);
	public static Tile CHECKPOINT = new Tile(27,"Wall checkpoint",TileType.GROUND,1);
	public static Tile TOWN_HALL = new Tile(28,"Town hall",TileType.GROUND,1);
	public static Tile CONTROL = new Tile(29,"Control Tower",TileType.GROUND,1);*/
	// each tiles data :
	int id;
	String name;
	String renderName;
	int renderType;
	TileType type;
	int travellingBuff = 0;
	boolean canInteract = false;
	int speed;
	InteractiveTile interactive;
	// Constructor
	public Tile(int id, String name,TileType type, int renderType,String renderName, boolean canInteract, int speed, InteractiveTile it) {
		this.id = id;
		this.name = name;
		this.renderType = renderType;
		this.type = type;
		this.renderName = renderName;
		this.canInteract = canInteract;
		this.speed = speed;
		if(it != null)
		{
			interactive = it;
			interactive.tile = this;
		}
		Tile.tileTypes[id] = this;
		tileTypesC++;
	}
	
	// It's okay not to give the render Data
	
	public static Tile getTileByID(int i)
	{
		return tileTypes[i];
	}
	
	public int getId()
	{
		return id;
	}
	
	public TileType getType()
	{
		return type;
	}
	
	public int getRenderType()
	{
		return renderType;
	}
	
	public String getTextureName()
	{
		return renderName;
	}
	
	public String getTileName()
	{
		return name;
	}
	
	public boolean canInteract()
	{
		return canInteract;
	}
	
	public boolean canUnitPass(Unit u)
	{
		if(type.equals(TileType.MOUNTAIN) || type.equals(TileType.WATER))
			return false;
		return true;
	}
	
	public int getPassageCost(Unit u)
	{
		return speed;
	}
	
	public boolean hasInteractiveTile()
	{
		return interactive != null;
	}
	
	public InteractiveTile getInteractiveTile()
	{
		return interactive.getClone();
	}
	
	public int getLightLevel()
	{
		if(this.hasInteractiveTile())
			return 5;
		return 0;
	}
}
