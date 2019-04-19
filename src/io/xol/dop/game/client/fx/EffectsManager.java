package io.xol.dop.game.client.fx;

import io.xol.dop.game.World;

//(c) 2014 XolioWare Interactive

public class EffectsManager {

	public World world;
	
	FXBase[] effects = new FXBase[2048];
	int effects_count = 0;
	
	public EffectsManager(World w)
	{
		world = w;
	}
	
	public void addEffect(FXBase fx)
	{
		//System.out.println("adding fx ("+effects_count+")");
		if(effects_count > effects.length-2)
			return;
		effects[effects_count] = fx;
		effects_count++;
	}
	
	public void purgeEffects()
	{
		for(int i = 0; i < effects_count; i++)
		{
			FXBase fx = effects[i];
			if(fx == null || fx.isDead())
			{
				effects[i] = effects[effects_count-1];
				effects[effects_count-1] = null;
				effects_count--;
			}
		}
	}
	
	public void cleanEffects()
	{
		effects = new FXBase[2048];
	}
	
	public void renderEffects(int camX, int camY)
	{
		purgeEffects();
		for(int i = 0; i < effects_count; i++)
		{
			//System.out.println("render fx");
			FXBase fx = effects[i];
			if(fx != null && !fx.isDead())
			{
				fx.render(camX, camY);
			}
		}
	}
}
