package io.xol.dop.tools.tests;
/*
import java.util.ArrayList;
import java.util.List;

import io.xol.dop.game.mode.GameModeType;*/
import io.xol.engine.misc.HttpRequester;

//(c) 2014 XolioWare Interactive

public class RandomTests implements HttpRequester{
	
	public static void main(String[] a)
	{
		float lol = 40.408f;
		float sum = 0;
		for(int i = 1; i< 10; i++)
		{
			sum+=lol*Math.pow(1.15,i);
			System.out.println((45+i)+": "+lol*Math.pow(1.15,i)+" -> "+sum);
		}
		System.out.println(sum);
		/*
		int posX = 3;
		int posY = 3;
		
		//List<Integer> coords = new ArrayList<Integer>();
		int i = 2;
		
		for(int y = i; y >= -i; y--)
		{
			
			if(y != i && y != -i)
			{
				System.out.println("Going x:"+(posX+(i-y))+" y:"+(posY+y));
				System.out.println("Going x:"+(posX-(i-y))+" y:"+(posY+y));
			}
			else
				System.out.println("Going x:"+(posX)+" y:"+(posY+y));
		}**/
		
		//for(int x = 0; )
		
		//String hexColor = "8465B2";
		/*
		GameModeType gm = GameModeType.CONTINUOUS_GAME;
		
		System.out.println(GameModeType.getType(2)+" M'lol + "+GameModeType.getId(gm));
		*/
		//HttpRequests.HttpRequestThread lel =
		/*
		for(int i = 30; i < 38;i++)
			System.out.println("Color : "+"\u001B["+i+"m"+" trolilol ( "+i+ ")");
		
		System.out.println(ColorsTools.convertToAnsi("#FF0000Coucou !"));
		*/
		// Keyboard buttons list 
		/*for(int i = 0; i < 256; i++)
		{
			System.out.println(i+":"+Keyboard.getKeyName(i));
		}
		*/

		//Flegme.exe
		/*for(File f : new File("./res/textures/tiles/beachwip/").listFiles())
		{
			f.renameTo(new File("./res/textures/tiles/beachwip/"+f.getName().replace("grass", "beach")));
		}*/
	}

	public void handleHttpRequest(String info, String result) {
		System.out.println("Request "+info+" got answered: "+result);
	}
}
