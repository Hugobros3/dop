package io.xol.dop.game.units;

//(c) 2014 XolioWare Interactive

import java.util.ArrayList;
import java.util.List;

import io.xol.dop.game.World;
import io.xol.dop.game.client.bits.NationsInfo;
import io.xol.dop.game.client.renderer.NumbersRenderer;
import io.xol.dop.game.client.renderer.WorldRenderer;
import io.xol.dop.game.pathfinder.AcceptablePath;
import io.xol.dop.game.pathfinder.Pathfinder;
import io.xol.dop.game.server.Server;
import io.xol.dop.game.tiles.InteractiveTile;
import io.xol.dop.game.tiles.TileConquerable;
import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.locale.Localizer;
import io.xol.engine.misc.ColorsTools;

public abstract class Unit implements Cloneable{

	// all units values
	
	public static int unitTypesC = 0;
	public static final Unit[] unitTypes = new Unit[256];
	public static final int[] unitCosts = new int[256];
	public int id = 0;

	public Unit(String name2) {
		pv = maxPV;
		this.name = name2;
	}

	public static void initUnits()
	{
		addUnit(0,new UnitInfantry("inf"), 1000); 
		addUnit(1,new UnitBazooka("bazook"), 2500); 
		addUnit(2,new UnitCar("car"), 4000); 
		System.out.println(unitTypesC+" unit types.");
	}
	
	private static void addUnit(int id, Unit u, int cost) {
		u.id = unitTypesC;
		unitTypes[id] = u;
		unitCosts[id] = cost;
		unitTypesC++;
	}
	
	public static Unit makeUnit(int type)
	{
		Unit u = unitTypes[type];
		if(u == null)
			System.out.println("null unit :c");
		Unit returnme = null;
		try {
			returnme = (Unit) u.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return returnme;
	}

	// general unit specs
	
	public String name = "undefined";
	
	public String getDescription(){
		return Localizer.getText("unit."+name+".desc");
	}
	
	public String getCompleteName(){
		//return this.name;
		return Localizer.getText("unit."+name+".name");
	}
	
	public int getDamage(Unit target)
	{
		return 5; // Numbler of raw damage inflicted to any unit
	}
	
	public int getTicksPerMove(int tileID)
	{
		return 2; // Number of ticks to wait before moving
	}
	
	public int getMinRange()
	{
		return 1;
	}
	
	public int getMaxRange()
	{
		return 3;
	}
	
	// load-save meta for special units
	
	public abstract String saveMeta();
	public abstract void loadMeta(String meta);
	
	// instance-specific
	
	public int nation = -1;
	
	public int pv = 20;
	public int maxPV = 20;
	public int fuel = -1;
	
	public World world;
	public int posX = 0;
	public int posY = 0;
	
	//Values for movement
	public int ticksBeforeMove = 0;
	public int[] path;
	public int pathStep = 0;
	
	//Values for attacking, if != -1 will attack unit when tick decount is over
	public int attackCooldown = 0;
	public int attackX=-1;
	public int attackY=-1;
	
	//Values for conquering
	public int conqX=-1;
	public int conqY=-1;
	
	//Just graphic.
	public boolean direction = false;
	public int conqLeft = -1;
	
	 // This is for move animations
	public int animX = 0;
	public int animY = 0;
	
	// Dirty workarround to avoid moving a unit twice during a tick ( if it decals to right and gets ticked again )
	public int lastTick = 0;
	
	public boolean selected = false;
	public boolean isMoving = false;
	public boolean isAttacking = false;
	
	// init
	public void init(int x,int y, World w)
	{
		posX = x;
		posY = y;
		world = w;
	}
	
	// render
	
	public void render()
	{
		draw(posX*32+16, posY*32+16,true,true);
	}
	
	public void draw(int posx, int posy,boolean over,boolean inGame)
	{
		// Sat 0%, contrast +70, light+80
		int rgb[] = ColorsTools.rgbSplit(NationsInfo.getColor(nation));
		if(direction)
		{
			//System.out.println(rgb[0]/255f+":"+rgb[1]/255f+":"+rgb[2]/255f);
			ObjectRenderer.renderTexturedRotatedRectRVBA(posx+animX, posy+animY, 32, 32, 0, 0, 0, 1, 1, "units/"+name,1f,1f,1f,1f);
			ObjectRenderer.renderTexturedRotatedRectRVBA(posx+animX, posy+animY, 32, 32, 0, 0, 0, 1, 1, "units/"+name+"2",rgb[0]/255f,rgb[1]/255f,rgb[2]/255f,1f);
		}
		else
		{
			ObjectRenderer.renderTexturedRotatedRectRVBA(posx+animX, posy+animY, 32, 32, 0, 1, 0, 0, 1, "units/"+name,1f,1f,1f,1f);
			ObjectRenderer.renderTexturedRotatedRectRVBA(posx+animX, posy+animY, 32, 32, 0, 1, 0, 0, 1, "units/"+name+"2",rgb[0]/255f,rgb[1]/255f,rgb[2]/255f,1f);
			//ObjectRenderer.renderTexturedRect(posx+animX, posy+animY, 32, 32, 16, 0, 0, 16, 16, "units/"+name);
		}
		//ObjectRenderer.renderTexturedRect(posx+animX, posy+animY, 32, 32, 16, 0, 0, 16, 16, "units/"+name);
		int animSpeed = 2;
		if(animX > 0)
			animX-=animSpeed;
		if(animY > 0)
			animY-=animSpeed;
		if(animX < 0)
			animX+=animSpeed;
		if(animY < 0)
			animY+=animSpeed;
	    if (inGame)
	    {
	      int div = 45;
	      if ((WorldRenderer.renderTicksCounter / div % 2 == 0) && (isMoving()))
	        ObjectRenderer.renderTexturedRotatedRect(posx + 8 + this.animX, posy - 8 + this.animY, 16.0F, 16.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, "units/busy/moving");
	      else if ((WorldRenderer.renderTicksCounter / div % 2 == 0) && (isAttacking()))
	        ObjectRenderer.renderTexturedRotatedRect(posx + 8 + this.animX, posy - 8 + this.animY, 16.0F, 16.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, "units/busy/fighting");
	      else if ((WorldRenderer.renderTicksCounter / div % 2 == 0) && (isConquering()))
	        ObjectRenderer.renderTexturedRotatedRect(posx + 8 + this.animX, posy - 8 + this.animY, 16.0F, 16.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, "units/busy/conquering");
	      else
	        NumbersRenderer.renderNumerString(this.pv+"", posx + this.animX, posy - 10 + this.animY, over);
	    }
	}

	private boolean isMoving()
	{
		return this.isMoving;
	}
	
	// Gameplay
	
	public void requestMove(int toX, int toY)
	{
		AcceptablePath pathfind = Pathfinder.pathFind(world, this, toX, toY);
		if(pathfind != null)
		{
			path = pathfind.path;
			pathStep = 0;
			ticksBeforeMove = this.getTicksPerMove(world.getTileAt(posX, posY));
		}
	}
	
	public void tick(int tick)
	{
		//Stops unit from ticking twice while updating
		lastTick = tick;
		//Movement part
		if(ticksBeforeMove > 0)
		{
			ticksBeforeMove--;
			if(ticksBeforeMove == 0)
			{
				if(path.length/2 <= pathStep+1)
				{
					pathStep = 0;
					path = null;
				}
				else
				{
					//System.out.println("movin:"+ this.getTicksPerMove(w.getTileAt(path[pathStep*2],path[pathStep*2+1])));
					ticksBeforeMove = this.getTicksPerMove(world.getTileAt(path[pathStep*2],path[pathStep*2+1]));
					pathStep++;
					world.moveUnit(posX,posY,path[pathStep*2],path[pathStep*2+1]);
				}
			}
			updateUnitOnClient();
			return;
		}
		//Attacking part
		if(isAttacking())
		{
			if(attackCooldown <= 0)
			{
				if(world.unitAttack(this,attackX,attackY))
				{
					attackCooldown = this.getAttackCooldown();
					//System.out.println("Unit attacked ! New cooldown : "+attackCooldown);
				}
			}
			else
			{
				attackCooldown--;
			}
			return;
		}
		//Conquering part
		if(isConquering())
		{
			InteractiveTile it = world.getInteractiveTile(conqX, conqY);
			if(it != null)
			{
				if(it instanceof TileConquerable && ((TileConquerable) it).isConquerable(this))
				{
					TileConquerable tc = (TileConquerable)it;
					tc.conquerTick(this);
					if(tc.nation == this.nation)
						requestConquer(-1,-1);
					return;
				}
				else
				{
					requestConquer(-1,-1);
					System.out.println("Warning : was trying to conquer a tile that is not supposed to be");
				}
			}
			else
			{
				requestConquer(-1,-1);
				System.out.println("Warning : was trying to conquer a tile with no extended info on it.");
			}
		}
	}

	private int getAttackCooldown() { // Numbers of ticks to wait between two fires
		return 2;
	}

	public int dealDamage(int pv, Unit attacker)
	{
		if(!this.isAttacking() && attacker.isAttackableBy(this))
		{
			//When attacked, reply !
			int decal = 1;
			if(attacker.posX < this.posX || (attacker.posX == posX && attacker.posY < posY))
				decal = 0;
			this.requestAttack(attacker,decal);
			//System.out.println(this.toString()+" attacked, replying to : "+attacker.toString());
		}
		this.pv-=pv;
		//System.out.println(attacker.toString()+" attacking : "+this.toString()+" : "+pv+" HP dealt, "+this.pv+" left.");
		if(this.pv <= 0)
			this.die();
		//System.out.println();
		return pv;
	}
	
	private void die() {
		world.deleteUnitAt(posX, posY, true);
		//System.out.println(this.toString()+" dead.");
	}

	public int getHealth() {
		return pv;
	}

	public void setHealth(int p) {
		this.pv = p;
	}
	
	public int getMaxHealth() {
		return maxPV;
	}
	
	public int attack(Unit target)
	{
		if(target != null)
		{
			return target.dealDamage(this.getDamage(target), this);
		}
		return 0;
	}

	public int[] getAttackableCoords(int targetX, int targetY)
	{
		List<Integer> coords = new ArrayList<Integer>();
		if(this.getMinRange() > 0)
		{
			//ystem.out.println("cc 2");
			for(int i = getMinRange(); i <= getMaxRange(); i++)
			{
				//System.out.println("cc"+i);
				for(int y = i; y >= -i; y--)
				{
					int cx;
					int cy = targetY+y;
					if(y != -i && y != i)
					{
						cx = targetX+(i-Math.abs(y));
						if(cx != posX || cy != posY)
						{
							coords.add(cx);
							coords.add(cy);
						}
						cx = targetX-(i-Math.abs(y));
						if(cx != posX || cy != posY)
						{
							coords.add(cx);
							coords.add(cy);
						}
					}
					else
					{
						cx = targetX;
						coords.add(cx);
						coords.add(cy);
					}
				}
			}
		}
		if(coords.size() > 0)
		{
			int[] array = new int[coords.size()];
			for(int a = 0; a < array.length; a++)
			{
				array[a] = coords.get(a);
			}
			//System.out.println("Returning a "+array.length+" length array");
			return array;
		}
		return null;
	}
	
	public int[] getReallyAttackableCoords(int targetX, int targetY)
	{
		List<Integer> coords = new ArrayList<Integer>();
		int[] potential = getAttackableCoords(targetX,targetY);
		for(int i = 0; i < potential.length/2; i++)
		{
			Unit u = world.getUnitAt(potential[i*2], potential[i*2+1]);
			if(u != null && u.isAttackableBy(this))
			{
				coords.add(u.posX);
				coords.add(u.posY);
			}
		}
		if(coords.size() > 0)
		{
			int[] array = new int[coords.size()];
			for(int a = 0; a < array.length; a++)
			{
				array[a] = coords.get(a);
			}
			//System.out.println("Returning a "+array.length+" length array");
			return array;
		}
		return null;
	}
	
	public boolean isAttackableBy(Unit u)
	{
		return u.nation != this.nation;
	}

	public boolean isAttacking()
	{
		return (this.isAttacking) || (this.attackX != -1);
	}
	
	public void requestAttack(Unit target, int cooldown) {
		if(target != null)
		{
			this.attackX = target.posX;
			this.attackY = target.posY;
			this.attackCooldown = this.getAttackCooldown() + cooldown;
		}
	}
	
	public String toString()
	{
		return "[Unit type="+name+", posX="+posX+", posY="+posY+", worldname="+world.name+"]";
	}

	public void targetEliminated() {
		//System.out.println(toString()+"'s target dead, stopping attacking.");
		this.attackX = -1;
		this.attackY = -1;
	}
	
	//Conquering
	
	public boolean isConquering()
	{
		return conqX != -1 || conqLeft != -1;
	}
	
	public void requestConquer(int tx, int ty)
	{
		conqX = tx;
		conqY = ty;
	}

	public void updateUnitOnClient()
	{
	    Server.handler.dispatchWorldUpdate(this.posX, this.posY, "world/unitStatus:" + this.posX + ":" + this.posY + ":" + (this.ticksBeforeMove > 0 ? "true" : "false") + ":" + (isAttacking() ? "true" : "false"));
	}
	
	public String[] getToolTipText() {
		if(isConquering())
			return new String[] {getCompleteName(),getHealth()+"HP",NationsInfo.getNation(nation),"Conquering tile : "+conqLeft+" points left."};
		return new String[] {getCompleteName(),getHealth()+"HP",NationsInfo.getNation(nation)};
	}
}