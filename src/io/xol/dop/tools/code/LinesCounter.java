package io.xol.dop.tools.code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

//(c) 2014 XolioWare Interactive

public class LinesCounter {

	static int totalFiles = 0;
	
	public static void main(String[] a)
	{
		System.out.println("Calculating the complete amount of codelines in the project...");
		System.out.println(computeLines(new File("./src/"))+" lines of code, in "+totalFiles+" files.");
	}

	private static int computeLines(File d) {
		int count = 0;
		if(d.isDirectory())
		{
			for(File f : d.listFiles())
			{
				count += computeLines(f);
			}
		}
		else
		{
			totalFiles++;
			try {
				InputStream ips = new FileInputStream(d);
				InputStreamReader ipsr = new InputStreamReader(ips, "UTF-8");
				BufferedReader br = new BufferedReader(ipsr);
				while ((br.readLine()) != null) {
					count++;
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return count;
	}
}
