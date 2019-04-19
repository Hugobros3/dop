package io.xol.dop.game.client;

//(c) 2014 XolioWare Interactive

public class FastConfig {

	// This classfile holds cached values that are used very often in the game runtime, allowing for speed improvements in some areas like rendering.
	
	// Controls keys
	public static int keyDown = 208;
	public static int keyUp = 200;
	public static int keyLeft = 203;
	public static int keyRight = 205;
	
	public static int keyStart = 28;
	public static int keyBack = 1;
	public static int keyTab = 15;
	
	public static int keyFill = 33;
	public static int keyDebug = 31;
	//Render values
	public static boolean renderDenseTiles = true;
	//System values
	public static String locale = "en";
	
	public static void load()
	{
		keyDown = Client.clientConfig.getIntProp("DOWNKEY", keyDown);
		keyUp = Client.clientConfig.getIntProp("UPKEY", keyUp);
		keyLeft = Client.clientConfig.getIntProp("LEFTKEY", keyLeft);
		keyRight = Client.clientConfig.getIntProp("RIGHTKEY", keyRight);
		keyStart = Client.clientConfig.getIntProp("STARTKEY", keyStart);
		keyBack = Client.clientConfig.getIntProp("BACKKEY", keyBack);
		keyTab = Client.clientConfig.getIntProp("TABKEY", keyTab);
		keyFill = Client.clientConfig.getIntProp("FILLKEY", keyFill);
		keyDebug = Client.clientConfig.getIntProp("DEBUGKEY", keyDebug);
		
		renderDenseTiles = Client.clientConfig.getBooleanProp("renderDenseTiles", renderDenseTiles);
		
		locale = Client.clientConfig.getProp("locale", locale);
	}
}
