package io.xol.dop.game.pathfinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.xol.dop.game.World;
import io.xol.dop.game.tiles.Tile;
import io.xol.dop.game.units.Unit;

//(c) 2014 XolioWare Interactive

public class Pathfinder {

	/* 
	 	Xolio A* Pathfinding bitchez.
	 */
	
	static int[] bestPath;
	
	static List<Node> openList = new ArrayList<Node>();
	static List<Node> closedList = new ArrayList<Node>();
	
	public static AcceptablePath pathFind(World w, Unit u, int destX, int destY)
	{
		openList.clear();
		closedList.clear();
		long timeStart = System.currentTimeMillis();
		//openList.add(new int[]{u.posX,u.posY});
		Node endNode = new Node(destX, destY, null, 0);
		if(endNode.isObstacle(w, u))
			return null;
		Node cNode = new Node(u.posX,u.posY,null,0);
		if(Node.distance(cNode, endNode) > 100)
			return null;
		while(cNode.x != destX || cNode.y != destY && System.currentTimeMillis() - timeStart < 100l)
		{
			testNode(cNode.x+1,cNode.y,cNode,w,u,endNode, cNode);
			testNode(cNode.x-1,cNode.y,cNode,w,u,endNode, cNode);
			testNode(cNode.x,cNode.y+1,cNode,w,u,endNode, cNode);
			testNode(cNode.x,cNode.y-1,cNode,w,u,endNode, cNode);
			if(openList.size() <= 0)
			{
				//System.out.println("No solution !");
				return null;
			}
			else
			{
				Collections.sort(openList, new Comparator<Node>() {
			        @Override
			        public int compare(Node n1, Node n2)
			        {
			            return n1.quality-n2.quality;
			        }
			    });
				Node bestNode = openList.get(0);
				openList.remove(bestNode);
				closedList.add(bestNode);
				//System.out.println(bestNode.toString()+":"+openList.size());
				cNode = bestNode;
			}
		}
		AcceptablePath path = new AcceptablePath(cNode);
		return path;
	}

	private static void testNode(int i, int y, Node parent,World w, Unit u, Node endNode, Node cNode) {
		Node n = new Node(i,y,parent,0);
		//System.out.println(n.x+":"+n.y+":"+n.isObstacle(w, u));
		if(!n.isObstacle(w, u) && !closedList.contains(n))
		{
			//System.out.println("lol");
			if(openList.contains(n))
			{
				openList.remove(n);
				n.parent = cNode;
				openList.add(n);
				n.quality = Node.distance(n, endNode) + Tile.getTileByID(w.getTileAt(i, y)).getPassageCost(u);
			}
			else
			{
				n.quality = Node.distance(n, endNode) + Tile.getTileByID(w.getTileAt(i, y)).getPassageCost(u);
				openList.add(n);
			}
		}
	}
}
