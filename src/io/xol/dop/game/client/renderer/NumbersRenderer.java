package io.xol.dop.game.client.renderer;

import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.misc.ColorsTools;

//(c) 2014 XolioWare Interactive

public class NumbersRenderer {

	public static void renderNumerString(String str, int x, int y,boolean transp)
	{
		//str= "347";
		int a = 0;
		for(char c : str.toCharArray())
		{
			int z = (int)c;
			z-=48;
			if(z < 0 || z > 10)
				z = 10;
			//System.out.println(c+":"+(int)c);
			//ObjectRenderer.renderTexturedRect(x, y, 16, 16, 0, 0, 8, 8, 32, "fx/numbers");
			ObjectRenderer.renderTexturedRotatedRectRVBA(x+a*12, y, 16, 16, 0, z%4*1/4f, (z/4)*1/4f, z%4*1/4f+1/4f,(z/4)*1/4f+ 1/4f, "fx/numbers", 1, 1, 1, transp ? 0.5f : 1f);
			a++;
		}
	}

	public static void renderNumerStringColor(String str, int x, int y,String color, float a) {
		int rgb[] = ColorsTools.hexToRGB(color);
		for(char c : str.toCharArray())
		{
			int z = (int)c;
			z-=48;
			if(z < 0 || z > 10)
				z = 10;
			//System.out.println(c+":"+(int)c);
			//ObjectRenderer.renderTexturedRect(x, y, 16, 16, 0, 0, 8, 8, 32, "fx/numbers");
			ObjectRenderer.renderTexturedRotatedRectRVBA(x+a*12, y, 16, 16, 0, z%4*1/4f, (z/4)*1/4f, z%4*1/4f+1/4f,(z/4)*1/4f+ 1/4f, "fx/numbers", rgb[0]/255f, rgb[1]/255f, rgb[2]/255f, a);
			a++;
		}
	}
}
