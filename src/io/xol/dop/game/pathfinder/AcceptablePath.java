package io.xol.dop.game.pathfinder;

import java.util.ArrayList;
import java.util.List;

//(c) 2014 XolioWare Interactive

public class AcceptablePath {

	//This class just takes care of bringing back a path and the relative information to it !
	
	public int[] path;
	public int distance;
	public int sumWeight;
	
	public AcceptablePath(Node node)
	{
		List<Integer> pathList = new ArrayList<Integer>();
		while(node != null)
		{
			pathList.add(node.y);
			pathList.add(node.x);
			distance+=1;
			sumWeight+=node.weight;
			node = node.parent;
		}
		distance-=1;
		path = new int[pathList.size()];
		int i = 0;
		for(Integer e : pathList)
			path[pathList.size()-(i++)-1] = e.intValue();

		//System.out.println("Done !"+path.length+"");
		
		/*String endPath = cNode.getPath();
		int[] pathToReturn = new int[endPath.split(" ").length*2];
		int i = 0;
		for(String lol : endPath.split(" "))
		{
			if(lol.contains(":"))
			{
				pathToReturn[i*2] = Integer.parseInt(lol.split(":")[0]);
				pathToReturn[i*2+1] = Integer.parseInt(lol.split(":")[1]);
				i++;
			}
				//System.out.println("mdr");
		}*/
	}
}
