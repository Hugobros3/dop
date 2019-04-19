package io.xol.engine.misc;

import java.lang.reflect.Field;

//(c) 2014 XolioWare Interactive

public class NativesLoader {
	public static void load()
	{
		String OS = System.getProperty("os.name").toLowerCase();
		System.out.println("Loading natives for OS : "+OS);
		setLibraryPath("lib/lwjgl/native/"+OSHelper.getOS());
	}
	
	private static void setLibraryPath(String path) {
		try{
	    System.setProperty("java.library.path", path);
	    System.out.println("Set lib path to : "+path);
	    //set sys_paths to null so that java.library.path will be reevalueted next time it is needed
	    final Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
	    sysPathsField.setAccessible(true);
	    sysPathsField.set(null, null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
