package io.xol.dop.tools.code;

import io.xol.engine.misc.FoldersUtils;

import java.io.File;

//(c) 2014 XolioWare Interactive

public class Releaser {

	static String[] toRelease = {
			"client.jar",
			"updater.jar",
			"version.txt",
			"DoP.exe",
			"readme.txt",
			"levels/",
			"res/",
			"legal/",
			"lib/"
	};
	
	public static void main(String[] args) {
		try{
			System.out.println("DoP releaser script/app 1.2, will make a release/ folder and pack everything needed inside.");
			System.out.println("Will start in 5s in case you'd want to cancel.");
			Thread.sleep(5000);
			//ok !
			File releaseFolder = new File("./release/");
			if(releaseFolder.exists())
			{
				System.out.println("Deleting previous release/ folder...");
				FoldersUtils.deleteFolder(releaseFolder);
			}
			System.out.print("Making a new folder ...");
			releaseFolder.mkdir();
			System.out.println("done !");
			System.out.println("Adding all needed things for release...");
			for(String add : toRelease)
			{
				System.out.println("Doing "+add);
				if(add.endsWith("/"))
					FoldersUtils.copyFolder(new File("./"+add), new File( "./release/"+add));
				else
					FoldersUtils.copyFile(new File("./"+add), new File( "./release/"+add));
			}
			System.out.println("Done !");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}


}
