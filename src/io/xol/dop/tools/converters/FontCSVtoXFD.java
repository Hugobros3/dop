package io.xol.dop.tools.converters;

//(c) 2014 XolioWare Interactive

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
import java.sql.Time;
import java.util.Scanner;

public class FontCSVtoXFD {

	@SuppressWarnings("deprecation")
	public static void main(String[] args){
		System.out.println("FontCSVtoXFD - tool by XolioWare Interactive");
		System.out.println("It converts CBFG .csv bitmap font config files to the format used by the game.");
		System.out.println("Please input the name of the font you want to convert.");
		System.out.println("Ex : \"arial\" will convert res/textures/font/arial.csv to XFD format.");
		Scanner in = new Scanner(System.in);
		System.out.print("> ");
		String name = in.nextLine();
		in.close();
		if(name == null || name.equals(""))
		{
			System.out.println("Syntax error");
		}
		else
		{
			int fontData[] = new int[256];
			int glyphs = 0;
			try {
				InputStream ips = new FileInputStream(new File(
						System.getProperty("user.dir") + "/res/textures/font/" + name + ".csv"));
				InputStreamReader ipsr = new InputStreamReader(ips, "UTF-8");
				BufferedReader br = new BufferedReader(ipsr);
				String ligne;
				while ((ligne = br.readLine()) != null) {
					if(!ligne.equals(""))
					{
						if(ligne.contains("Char") && ligne.contains("Base Width"))
						{
							String[] parse = ligne.split(" ");
							if(parse[0].equals("Char"))
							{
								fontData[Integer.parseInt(parse[1])] = Integer.parseInt(parse[3].split(",")[1]);
								glyphs++;
							}
						}
					}
				}
				br.close();
				// now save converted file !
				System.out.println("Read info about "+glyphs+" characters, saving' em.");
				int j = 0;
				try {
					Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(System.getProperty("user.dir") + "//res/textures/font/" + name + ".xfd"), "UTF-8"));
					out.write("#Font converted by CSV2XFD on "+new Time(System.currentTimeMillis()).toGMTString()+"\n");
					
					for(int i : fontData)
					{
						out.write(j+":"+i+"\n");
						j++;
					}
					out.close();
				}catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("Saved "+j+" chars.");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
