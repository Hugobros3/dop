package io.xol.dop.game.common.nations;

import java.util.ArrayList;
import java.util.List;

//(c) 2014 XolioWare Interactive

public class Grade {
	
	public Nation nation;
	
	public String name;
	public String parent;
	List<String> perms = new ArrayList<String>();
	
	public Grade(String n,String g, Nation na)
	{
		name = n;
		parent = g;
		nation = na;
	}
	
	public boolean hasPerm(String perm)
	{
		if(perms.contains(perm))
		{
			return true;
		}
		if(parent != null)
		{
			return nation.getGrade(parent).hasPerm(perm);
		}
		return false;
	}

	public void addPerm(String string) {
		perms.add(string);
	}
}
