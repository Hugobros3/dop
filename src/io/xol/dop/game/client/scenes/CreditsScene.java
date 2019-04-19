package io.xol.dop.game.client.scenes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.xol.dop.game.client.FastConfig;
import io.xol.engine.base.XolioWindow;
import io.xol.engine.base.font.BitmapFont;
import io.xol.engine.base.font.FontRenderer;
import io.xol.engine.scene.Scene;

//(c) 2014 XolioWare Interactive

public class CreditsScene extends Scene{

	List<String> credits = new ArrayList<String>();
	float scrollY = 0;
	
	
	public CreditsScene(XolioWindow XolioWindow) {
		super(XolioWindow);
		//Load credits file
		File f = new File(System.getProperty("user.dir") + "/"+"legal/credits.txt");
		try {
			InputStream ips = new FileInputStream(f);
			InputStreamReader ipsr = new InputStreamReader(ips, "UTF-8");
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;
			while ((ligne = br.readLine()) != null) {
				credits.add(ligne);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//done
	}
	
	public void update()
	{
		//title
		int index = 0;
		for(String s : credits)
		{
			index++;
			drawCenteredText(s,-index*getScreenSizeMultiplier()+scrollY*getMult()+2,getScreenSizeMultiplier(),"FFFFFF",1,BitmapFont.SMALLFONTS);
			if(scrollY < 4200)
				scrollY+=0.005;
			//System.out.println(scrollY);
		}
		//drawCenteredText(errorMessage2,XolioWindow.frameH-XolioWindow.frameH/3,32,"FF0000",1,BitmapFont.SMALLFONTS);
		super.update();
	}
	
	int getMult()
	{
		if(XolioWindow.frameW > 1200)
			return 2;
		return 1;
	}
	
	int getScreenSizeMultiplier()
	{
		if(XolioWindow.frameW > 1200)
			return 64;
		return 32;
	}
	
	//Controls handling
	public boolean onKeyPress(int k) {
		if(k == FastConfig.keyStart)
			this.eng.changeScene(new MainMenuScene(eng,0));
		return false;
	}
	
	void drawCenteredText(String t,float height,int basesize,String hex,float a,BitmapFont f)
	{
		FontRenderer.drawTextUsingSpecificFontHex(XolioWindow.frameW / 2 - FontRenderer.getTextLengthUsingFont(basesize, t, f)/2,
				height, 0, basesize,t,f,hex,a);
	}
	void drawCenteredText(String t,float height,int basesize,float r,float v,float b,float a)
	{
		FontRenderer.drawTextUsingSpecificFontRVBA(XolioWindow.frameW / 2 - FontRenderer.getTextLengthUsingFont(basesize, t, BitmapFont.EDITUNDO)/2,
				height, 0, basesize,t,BitmapFont.EDITUNDO,a,r,v,b);
	}
}
