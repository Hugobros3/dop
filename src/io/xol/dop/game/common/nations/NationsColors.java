package io.xol.dop.game.common.nations;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

//(c) 2014 XolioWare Interactive

public class NationsColors {

	//Default values
	static String[] defaultColorNames = {
		"Magma red",
		"Orange Fire",
		"Yellow butter",
		"Blue lagoon",
		"Acid green",
		"Dark purple",
		"Burgundy",
		"Red skin",
		"Pale skin",
		"Ocean blue",
		"Lemon green",
		"Barbie pink",
		"Turquoise",
		"Blue sky",
		"Grey sky",
		"Concrete grey",
		"Coal",
		"khaki",
		"Night blue",
		"Forest green",
		"Sand yellow",
		"Dark turquoise",
		"Black",
		"Light purple"
	};
	
	static int defaultMaxColors = 24;
	
	static int[] defaultColors = new int[defaultMaxColors];
	//Custom values
	static String[] customColorNames;
	static int customMaxColors;
	static int[] customColors;
	//Loaders
	public static void loadDefaultColorScheme()
	{
		defaultColors = loadColorScheme("./res/textures/nations/colors.png");
	}
	
	public static void loadCustomColorScheme(String p)
	{
		customColors = loadColorScheme(p);
	}
	
	static int[] loadColorScheme(String path)
	{
		try{
			File f = new File(path);
			if(!f.exists())
				return null;
			BufferedImage colorsMap = ImageIO.read(f);
			int nationsAmount = colorsMap.getWidth()/16;
			
			//System.out.println("Debug : loading image file "+path);
			//System.out.println("Debug : Size is "+nationsAmount);
			
			int[] colors = new int[nationsAmount];
			
			for(int i = 0; i < colorsMap.getWidth()/16-1;i++)
			{
				colors[i] = colorsMap.getRGB(i*16, 0);
			}
			
			customMaxColors = nationsAmount;
			System.out.println("Nation colors loaded.");
			return colors;
		}
		catch(Exception e)
		{
			System.out.println("Fatal error happened during nations colors image file loading.");
			System.out.println("Path was : "+path);
			e.printStackTrace();
		}
		return null;
	}
	
	public static int getMaxColors()
	{
		return defaultMaxColors;
	}
	
	public static String getColorName(int c)
	{
		return defaultColorNames[c];
	}

	public static int getColor(int i) {
		return defaultColors[i];
	}
			
}
