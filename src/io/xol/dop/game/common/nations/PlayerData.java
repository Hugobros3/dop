package io.xol.dop.game.common.nations;

import io.xol.dop.game.server.game.ServerGame;

//(c) 2014 XolioWare Interactive

public class PlayerData {

	public static ServerGame theGame;
	
	public String name = "";
	
	String nation;
	String grade;
	
	public PlayerData(String n)
	{
		name = n;
		load();
	}
	
	private void load() {
		/*nation = na;
		grade = g;*/
	}
	
	public void save() {
		
	}
	
	public boolean hasPermission(String perm)
	{
		return getGrade().hasPerm(perm);
	}
	
	public Grade getGrade()
	{
		return theGame.getNation(nation).getGrade(grade);
	}
}
