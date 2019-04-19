package io.xol.dop.game.client.subscenes;

//(c) 2014 XolioWare Interactive

import java.util.ArrayList;
import java.util.List;

import io.xol.dop.game.client.Client;
import io.xol.dop.game.client.FastConfig;
import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.base.XolioWindow;
import io.xol.engine.base.font.BitmapFont;
import io.xol.engine.base.font.FontRenderer;
import io.xol.engine.gui.KeyButtonDrawer;
import io.xol.engine.scene.Scene;
import io.xol.engine.scene.SubScene;

public class OptionsSubscene extends SubScene{

	boolean background;
	
	int currentOptionItem = 1;
	List<OptionItem> options = new ArrayList<OptionItem>();
	
	public OptionsSubscene(Scene p, boolean background) {
		super(p);
		this.background = background;
		options.add(new OptionItemSeparator("Graphical stuff"));
		options.add(new OptionItemBoolean("renderDenseTiles","true"));
		options.add(new OptionItemBoolean("fullScreen","false"));
		options.add(new OptionItemFullscreen("fullScreenResolution"));
		//options.add(new OptionItemBoolean("test2","true"));
		//options.add(new OptionItemBoolean("test3","true"));
		//options.add(new OptionItemMultiChoice("statut",new String[] {"oklm","en chien","fragilisé","sur l'autoroute de la loose","Tony Brn"}));
		options.add(new OptionItemSeparator("System stuff"));
		options.add(new OptionItemBoolean("noUpdates","false"));
		//options.add(new OptionItemNumeric("int",3,0,10));
		//options.add(new OptionItemPercentage("percent",50));
	}

	public void update()
	{
		if(background)
			ObjectRenderer.renderColoredRect(XolioWindow.frameW/2, XolioWindow.frameH/2, XolioWindow.frameW, XolioWindow.frameH, 0, "000000", 0.3f);
		FontRenderer.drawTextUsingSpecificFontRVBA(32,XolioWindow.frameH -64 ,0,48,"Options",BitmapFont.EDITUNDO,1f,1f,0.5f,1f);
		int scroll = 0;
		for(OptionItem item : options)
		{
			item.render(XolioWindow.frameW, XolioWindow.frameH-scroll*32-128, scroll == currentOptionItem);
			scroll++;
		}
		drawRightedText("Back",12,48,16,1f,1,1,1f);
		KeyButtonDrawer.drawButtonForKeyRightSide(60, 20,Client.clientConfig.getIntProp("BACKKEY", "14") , 1);
		
	}
	
	public boolean onKeyPress(int k) {
		if(k == FastConfig.keyDown)
		{
			currentOptionItem++;
			if(currentOptionItem < options.size()-1 && !(options.get(currentOptionItem) instanceof OptionItemUsefull))
				currentOptionItem++;
			while(currentOptionItem >= options.size())
				currentOptionItem--;
		}
		if(k == FastConfig.keyUp)
		{
			currentOptionItem--;
			if( currentOptionItem > 1 && !(options.get(currentOptionItem) instanceof OptionItemUsefull))
				currentOptionItem--;
			while(currentOptionItem < 1)
				currentOptionItem++;
		}
		
		if(k == FastConfig.keyLeft
				|| k == FastConfig.keyRight
				|| k == FastConfig.keyStart)
		{
			OptionItem item = options.get(currentOptionItem);
			if(item instanceof OptionItemUsefull)
			{
				((OptionItemUsefull) item).onEntry(k != FastConfig.keyLeft);
			}
		}
		
		if(k == FastConfig.keyBack)
			close();
		return false;
	}
	
	public boolean onClick(int posx,int posy,int button)
	{
		return false;
	}
	
	void close()
	{
		for(OptionItem item : options)
		{
			if(item instanceof OptionItemUsefull)
				((OptionItemUsefull)item).save();
		}
		Client.clientConfig.save();
		FastConfig.load();
		if(Client.isIngame())
		{
			if(Client.getGame().world != null && Client.getGame().world.renderer != null)
			{
				Client.getGame().world.globalReRender();
			}
			
		}
		parent.eng.switchResolution();
		parent.setSubscene(null);
	}
	
	// render shit
	
	void drawCenteredText(String t,float height,int basesize,float r,float v,float b,float a)
	{
		FontRenderer.drawTextUsingSpecificFontRVBA(XolioWindow.frameW / 2 - FontRenderer.getTextLengthUsingFont(basesize, t, BitmapFont.SMALLFONTS)/2,
				height, 0, basesize,t,BitmapFont.SMALLFONTS,a,r,v,b);
	}
	
	void drawRightedText(String t,float decx,float height,int basesize,float r,float v,float b,float a)
	{
		FontRenderer.drawTextUsingSpecificFontRVBA(XolioWindow.frameW - decx -FontRenderer.getTextLengthUsingFont(basesize, t, BitmapFont.EDITUNDO),
				height, 0, basesize,t,BitmapFont.EDITUNDO,a,r,v,b);
	}
	
	void drawLeftedText(String t,float decx,float height,int basesize,float r,float v,float b,float a)
	{
		FontRenderer.drawTextUsingSpecificFontRVBA(decx /*-FontRenderer.getTextLengthUsingFont(basesize, t, BitmapFont.EDITUNDO),*/,
				height, 0, basesize,t,BitmapFont.EDITUNDO,a,r,v,b);
	}
	
	// sub-classes definitions
	
	abstract class OptionItem {
		public abstract void render(int width, int posY, boolean focus);
	}
	
	class OptionItemSeparator extends OptionItem{
		String text;
		public OptionItemSeparator(String text)
		{
			this.text = text;
		}
		
		@Override
		public void render(int width, int posY, boolean focus) {
			drawCenteredText("<"+text+">",posY,32,1,0.5f,1,1f);
		}
		
	}
	
	abstract class OptionItemUsefull extends OptionItem {
		String parameter;
		String value;
		public OptionItemUsefull(String parameter, String value)
		{
			this.parameter = parameter;
			this.value = Client.getConfig().getProp(parameter, value);
		}
		public abstract void onEntry(boolean type);
		
		public String getParameter(){
			return parameter;
		}
		public String getValue(){
			return value;
		}
		public void save()
		{
			Client.getConfig().setProp(parameter, value);
		}
	}
	
	class OptionItemBoolean extends OptionItemUsefull {
		public OptionItemBoolean(String parameter, String value) {
			super(parameter, value);
		}
		@Override
		public void onEntry(boolean type) {
			if(value.equals("true")){
				 value = "false";
				return;
			}
			else
			{
				value = "true";
			}
		}
		@Override
		public void render(int width, int posY, boolean focus) {
			drawCenteredText(parameter+" : "+value,posY,32,1,1,1,focus ? 1f : 0.5f);
		}
	}
	
	class OptionItemMultiChoice extends OptionItemUsefull {
		String values[];
		int cuVal = 0;
		public OptionItemMultiChoice(String parameter, String values[]) {
			super(parameter, values[0]);
			this.values = values;
			for(int i = 0; i < values.length; i++)
			{
				if(values[i].equals(value))
					cuVal = i;
			}
		}
		@Override
		public void onEntry(boolean type) {
			if(type)
				cuVal++;
			else
				cuVal--;
			if(cuVal < 0 || cuVal >= values.length)
				cuVal = 0;
			value = values[cuVal];
		}
		@Override
		public void render(int width, int posY, boolean focus) {
			drawCenteredText(parameter+" : "+value,posY,32,1,1,1,focus ? 1f : 0.5f);
		}
	}
	
	class OptionItemFullscreen extends OptionItemMultiChoice {
		public OptionItemFullscreen(String parameter) {
			super(parameter,XolioWindow.getDisplayModes());
		}
		
	}
	
	class OptionItemNumeric extends OptionItemUsefull {
		int min,max,num;
		public OptionItemNumeric(String parameter, int defaultv, int min, int max) {
			super(parameter, defaultv+"");
			this.min = min;
			this.max = max;
			this.num = Integer.parseInt(this.value);
		}
		@Override
		public void onEntry(boolean type) {
			if(type)
				num++;
			else
				num--;
			if(num < 0)
				num = 0;
			if(num > max)
				num = max;
			value = num+"";
		}
		@Override
		public void render(int width, int posY, boolean focus) {
			drawCenteredText(parameter+" : "+value,posY,32,1,1,1,focus ? 1f : 0.5f);
		}
	}
	
	class OptionItemPercentage extends OptionItemNumeric {
		public OptionItemPercentage(String parameter, int defaultv) {
			super(parameter, defaultv, 0, 100);
		}
		@Override
		public void render(int width, int posY, boolean focus) {
			drawCenteredText(parameter+" : "+value+"%",posY,32,1,1,1,focus ? 1f : 0.5f);
		}
	}
}
