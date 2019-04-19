package io.xol.dop.game.pathfinder;

import io.xol.dop.game.World;
import io.xol.dop.game.tiles.Tile;
import io.xol.dop.game.units.Unit;

//(c) 2014 XolioWare Interactive

public class Node {

	public int x;
	public int y;
	public Node parent;
	public int quality;
	public int weight;
	
	public Node(int x, int y, Node parent, int quality)
	{
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.quality = quality;
	}
	
	public static int distance(Node n1, Node n2)
	{
		return (int) Math.sqrt((n1.x - n2.x) * (n1.x - n2.x) + (n1.y - n2.y) * (n1.y - n2.y) );
	}
	
	public boolean isObstacle(World w,Unit u)
	{
		weight = Tile.getTileByID(w.getTileAt(x, y)).getPassageCost(u);
		return !Tile.getTileByID(w.getTileAt(x, y)).canUnitPass(u) || (parent != null && w.getUnitAt(x, y) != null);
	}
	
	@Override
	public boolean equals(Object o)
	{
		//System.out.println("equals working");
		if(!(o instanceof Node))
			return false;
		Node n = (Node)o;
		return (n.x == x && n.y == y);
	}
	
	public String getPath()
	{
		return getPathInternal("");
	}

	private String getPathInternal(String string) {
		string+=x+":"+y+" ";
		if(parent == null)
			return string;
		return parent.getPathInternal(string);
	}
}
