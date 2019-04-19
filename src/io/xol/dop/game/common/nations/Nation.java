package io.xol.dop.game.common.nations;

import io.xol.dop.game.server.game.ServerGame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

//(c) 2014 XolioWare Interactive

public class Nation {

	//public static char[] bannedCharsForFileSaving = {'.','\'','/','\"',' ','<','>'};
	public ServerGame theGame;
	
	public int id = 0;
	public int color = 0;
	public String name = "";
	public String password = "";
	public String desc = "";
	
	Map<String,Grade> grades = new HashMap<String,Grade>();
	public String default_grade = null;
	public String founder = "none";
	
	public int population = 0;
	public int funds = 0;
	
	public int ownedBuildings = 0;
	
	public Nation(int id,ServerGame game)
	{
		this.id = id;
		theGame = game;
		load();
	}
	
	public Nation(String name, int color,ServerGame game)
	{
		this.name = name;
		this.color = color;
		theGame = game;
		founder = "none";
		makeDefault();
		save();
	}

	private void makeDefault() {
		default_grade = "player";
		
		Grade player = new Grade("player",null,this);
		player.addPerm("intel.see");
		player.addPerm("orders.give");
		player.addPerm("stats.see");
		player.addPerm("perms.see");
		grades.put("player", player);
		
		Grade operator = new Grade("operator",null,this);
		operator.addPerm("sectors.manage");
		operator.addPerm("orders.give");
		operator.addPerm("perms.set");
		grades.put("operator", operator);
	}

	private void load() {
		String path = "./games/"+theGame.gameName+"/nations/"+id+".nation";
		grades.clear();
		check4Folder(path);
		try {
			InputStream ips = new FileInputStream(new File(
					System.getProperty("user.dir") + "/" + path));
			InputStreamReader ipsr = new InputStreamReader(ips, "UTF-8");
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;
			Grade addMe = null;
			while ((ligne = br.readLine()) != null) {
				//System.out.println(ligne);
				if(ligne.contains("=") && !ligne.endsWith("="))
				{
					if(ligne.split("=")[0].equals("color"))
						color = Math.min(Integer.parseInt(ligne.split("=")[1]),NationsColors.getMaxColors());
					if(ligne.split("=")[0].equals("desc"))
						desc = ligne.split("=")[1];
					if(ligne.split("=")[0].equals("name"))
						name = ligne.split("=")[1];
					if(ligne.split("=")[0].equals("default_grade"))
						default_grade = ligne.split("=")[1];
					if(ligne.split("=")[0].equals("founder"))
						founder = ligne.split("=")[1];
					if(ligne.split("=")[0].equals("password"))
						password = ligne.split("=")[1];
					if(ligne.split("=")[0].equals("funds"))
						funds = Integer.parseInt(ligne.split("=")[1]);
					if(ligne.split("=")[0].equals("population"))
						population = Integer.parseInt(ligne.split("=")[1]);
				}
				if(ligne.startsWith("#"))
				{
					if(addMe != null)
						grades.put(addMe.name, addMe);
					String parent = null;
					String name;
					if(ligne.contains(":"))
					{
						name = ligne.split(":")[0].replace("#", "");
						parent = ligne.split(":")[1];
					}
					else
						name = ligne.replace("#", "");
					addMe = new Grade(name,parent,this);
				}
				if(ligne.startsWith("+"))
				{
					if(addMe != null)
					{
						addMe.perms.add(ligne.replace("+", ""));
					}
				}
				//	props.put(ligne.split("=")[0], ligne.split("=")[1]);
			}
			if(addMe != null)
				grades.put(addMe.name, addMe);
			//System.out.println("Nation "+name+" loaded, it contains "+grades.size()+" grades.");
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void save() {
		String path = "./games/"+theGame.gameName+"/nations/"+id+".nation";
		File f = new File(path);
		if(!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		check4Folder(path);
		try {
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(System.getProperty("user.dir") + "/" + path), "UTF-8"));
			
			out.write("//Nation data saving format 1.0"+"\n");
			out.write("color="+color+"\n");
			out.write("desc="+desc+"\n");
			out.write("name="+name+"\n");
			out.write("default_grade="+default_grade+"\n");
			out.write("founder="+founder+"\n");
			out.write("password="+password+"\n");
			out.write("population="+population+"\n");
			out.write("funds="+funds+"\n");
			
			for(Grade g : grades.values())
			{
				out.write("#"+g.name);
				if(g.parent != null)
					out.write(":"+g.parent+"\n");
				else
					out.write("\n");
				for(String p : g.perms)
				{
					out.write("+"+p+"\n");
				}
			}
			
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void check4Folder(String f) {
		File file = new File(System.getProperty("user.dir") + "/" + f);
		File folder = null;
		if (!file.isDirectory())
			folder = file.getParentFile();
		if (folder != null && !folder.exists())
			folder.mkdir();
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public void delete()
	{
		String path = "./games/"+theGame.gameName+"/nations/"+id+".nation";
		File f = new File(path);
		f.delete();
	}
	
	public Grade getGrade(String name)
	{
		for(Grade g : grades.values())
		{
			if(g.name.equals(name))
				return g;
		}
		return null;
	}
}
