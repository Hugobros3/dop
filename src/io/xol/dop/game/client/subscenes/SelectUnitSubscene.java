package io.xol.dop.game.client.subscenes;

import io.xol.dop.game.client.FastConfig;
import io.xol.dop.game.client.bits.NationsInfo;
import io.xol.dop.game.client.scenes.GameScene;
import io.xol.dop.game.units.Unit;
import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.base.XolioWindow;
import io.xol.engine.base.font.BitmapFont;
import io.xol.engine.base.font.FontRenderer;
import io.xol.engine.gui.CorneredBoxDrawer;
import io.xol.engine.scene.Scene;
import io.xol.engine.scene.SubScene;

//(c) 2014 XolioWare Interactive

public class SelectUnitSubscene extends SubScene {

	int changed = 0;
	int sel = 0;
	
	public SelectUnitSubscene(Scene p) {
		super(p);
		changed = XolioWindow.frameW*XolioWindow.frameH;
	}

	//Controls handling
	public boolean onKeyPress(int k) {
		if(k == FastConfig.keyBack)
			parent.destroySubscene();
		if(k == FastConfig.keyUp) // "DOWNKEY", "208"
			sel--;
		if(k == FastConfig.keyDown) // 
			sel++;
		if(k == FastConfig.keyStart) // 
		{
			if(parent instanceof GameScene)
			{
				GameScene s = ((GameScene)parent);
				if(s.control != null)
					s.control.selectedUnit(sel);
			}
			parent.destroySubscene();
		}
		return false;
	}
	
	public void update()
	{
		if(sel < 0 || sel >= Unit.unitTypesC)
			sel = 0;
		if(changed != XolioWindow.frameW*XolioWindow.frameH)
		{
			// Changed size
			changed = XolioWindow.frameW*XolioWindow.frameH;
		}
		int boxw = XolioWindow.frameW - 200;
		int boxh = XolioWindow.frameH - 200;
		CorneredBoxDrawer.drawCorneredBox(XolioWindow.frameW/2, XolioWindow.frameH/2, boxw, boxh, 8, "gui/catalogue");
		ObjectRenderer.renderTexturedRect(XolioWindow.frameW/2+5, XolioWindow.frameH/2, boxw-6, boxh-5, 0, 0, boxw-6, (boxh-5)/2, 32, "gui/catalogue3");
		FontRenderer.drawTextUsingSpecificFontHex(100, XolioWindow.frameH-100, 0, 32, "New unit", BitmapFont.EDITUNDO, NationsInfo.getColorHex(), 1);
		//
		int cu = 0;
		while(cu < Unit.unitTypesC)
		{
			Unit u = Unit.makeUnit(cu);
			u.nation = NationsInfo.playerNation;
			if(u.nation < 0 || u.nation > NationsInfo.nations.length)
				u.nation = 0;
			if(cu == sel)
				ObjectRenderer.renderColoredRect(135+(boxw-64)/2, XolioWindow.frameH-120-cu*36, boxw-15, 34, 0, 0.3f,0.3f,0.3f, 0.5f);
			u.draw(125, XolioWindow.frameH-120-cu*36,false,false);
			//ObjectRenderer.renderTexturedRect(120, XolioWindow.frameH-130-cu*24, 32, 32, "units/car");
			boolean canBuy = Unit.unitCosts[u.id] <= NationsInfo.funds;
			
			FontRenderer.drawTextUsingSpecificFontHex(145, XolioWindow.frameH-135-cu*36, 0, 32, u.getCompleteName() + (canBuy ? "" : " - Not enought funds"), BitmapFont.SMALLFONTS, "FFFFFF", canBuy ? 1f : 0.5f);
			
			FontRenderer.drawTextUsingSpecificFontHex(XolioWindow.frameW-115-FontRenderer.getTextLengthUsingFont(32, Unit.unitCosts[u.id]+"$",  BitmapFont.SMALLFONTS)
					, XolioWindow.frameH-135-cu*36, 0, 32, Unit.unitCosts[u.id]+"$", BitmapFont.SMALLFONTS, "FFFFFF", canBuy ? 1f : 0.5f);
			cu++;
		}
		super.update();
	}
}
