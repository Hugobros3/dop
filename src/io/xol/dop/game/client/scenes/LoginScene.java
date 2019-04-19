package io.xol.dop.game.client.scenes;

import io.xol.dop.game.client.Client;
import io.xol.dop.game.client.FastConfig;
import io.xol.dop.game.common.VersionInfo;
import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.base.XolioWindow;
import io.xol.engine.base.font.BitmapFont;
import io.xol.engine.base.font.FontRenderer;
import io.xol.engine.gui.ClickableButton;
import io.xol.engine.gui.FocusableObjectsHandler;
import io.xol.engine.gui.InputText;
import io.xol.engine.locale.Localizer;
import io.xol.engine.misc.HttpRequestThread;
import io.xol.engine.scene.Scene;

//(c) 2014 XolioWare Interactive

public class LoginScene extends Scene implements io.xol.engine.misc.HttpRequester{

	FocusableObjectsHandler guiHandler = new FocusableObjectsHandler();
	
	boolean logging_in = false;
	boolean autologin = false;
	int timer = 0;
	
	String message = "";
	
	public LoginScene(XolioWindow XolioWindow) {
		super(XolioWindow);
		
		//new HttpRequestThread(this, "lel", "http://facebook.com", "");
	
		//login
		guiHandler.add(new InputText());
		guiHandler.getInputText(0).focus = true;
		//pass
		guiHandler.add(new InputText());
		//ok
		guiHandler.add(new ClickableButton(0,0,Localizer.getText("gui.login"),BitmapFont.EDITUNDO,1));
		//check for auto-login data
		if(Client.getConfig().getProp("autologin", "ko").equals("ok"))
		{
			guiHandler.getInputText(0).setText(Client.getConfig().getProp("user", ""));
			guiHandler.getInputText(1).setText(Client.getConfig().getProp("pass", ""));
			autologin = true;
		}
		
	}

	public void update() {
		timer++;
		if(guiHandler.getButton(2).clicked)
		{
			guiHandler.getButton(2).clicked = false;
			connect();
		}
		
		ObjectRenderer.renderTexturedRect(XolioWindow.frameW / 2,XolioWindow.frameH / 2, XolioWindow.frameW, XolioWindow.frameH,0f, 0f, XolioWindow.frameW / 2, XolioWindow.frameH / 2, 128f,"gui/loginbg");
		
		ObjectRenderer.renderTexturedRect(XolioWindow.frameW / 2,XolioWindow.frameH / 2 +180, 512,512,"despotism");
		
		guiHandler.getButton(2).setPos(XolioWindow.frameW/2-245,XolioWindow.frameH/2-80);
		
		guiHandler.getInputText(0).drawWithBackGround(XolioWindow.frameW/2-250,XolioWindow.frameH/2+40, 32, BitmapFont.SMALLFONTS, 500);
		guiHandler.getInputText(1).drawWithBackGroundPassworded(XolioWindow.frameW/2-250,XolioWindow.frameH/2-40, 32, BitmapFont.SMALLFONTS, 500);
		
		FontRenderer.drawTextUsingSpecificFont(XolioWindow.frameW/2-250,XolioWindow.frameH/2+80,0, 32, Localizer.getText("gui.username"),BitmapFont.SMALLFONTS);
		FontRenderer.drawTextUsingSpecificFont(XolioWindow.frameW/2-250,XolioWindow.frameH/2+0,0, 32, Localizer.getText("gui.password"),BitmapFont.SMALLFONTS);
		
		int decal_lb = guiHandler.getButton(2).draw();
		
		FontRenderer.drawTextUsingSpecificFont(XolioWindow.frameW/2-230+decal_lb,XolioWindow.frameH/2-90,0, 32, Localizer.getText("gui.noaccount"),BitmapFont.SMALLFONTS);
		FontRenderer.drawTextUsingSpecificFont(XolioWindow.frameW/2-250,XolioWindow.frameH/2-150,0, 32, Localizer.getText("gui.passsaved1"),BitmapFont.SMALLFONTS);
		FontRenderer.drawTextUsingSpecificFont(XolioWindow.frameW/2-250,XolioWindow.frameH/2-150-18,0, 32, Localizer.getText("gui.passsaved2"),BitmapFont.SMALLFONTS);
		
		if(logging_in)
		{
			FontRenderer.drawTextUsingSpecificFontRVBA(XolioWindow.frameW/2-230+decal_lb,XolioWindow.frameH/2-120,0, 32, message,BitmapFont.SMALLFONTS,1,1,0,0);
		}
		
		if(autologin)
		{
			int seconds = 5;
			String autologin2 = Localizer.getText("gui.autologin", new String[]{(seconds-timer/100-1)+""});
			FontRenderer.drawTextUsingSpecificFontRVBA(XolioWindow.frameW/2-FontRenderer.getTextLengthUsingFont(32, autologin2, BitmapFont.SMALLFONTS)/2,XolioWindow.frameH/2-120,0, 32,autologin2 ,BitmapFont.SMALLFONTS,1,0,1,0);
			//System.out.println("timer"+timer);
			if(timer > seconds*60)
			{
				connect();
				autologin = false;
			}
		}
		
		FontRenderer.drawTextUsingSpecificFont(12,12,0, 32, "Copyright 2014-2114 XolioWare Interactive - DoP Version "+VersionInfo.get(),BitmapFont.SMALLFONTS);

		super.update();
	}
	
	public boolean onKeyPress(int k)
	{
		if(k == FastConfig.keyTab)
			guiHandler.next();
		else if(k == FastConfig.keyStart)
			connect();
		else if(k == FastConfig.keyBack)
			autologin = false;
		else
			guiHandler.handleInput(k);
		return true;
	}
	void connect()
	{
		if(guiHandler.getInputText(0).text.equals("OFFLINE"))
		{
			Client.offline = true;
			Client.username = "OfflineUser"+(int)(Math.random()*1000);
			this.eng.changeScene(new MainMenuScene(eng,0));
		}
		else
		{
			logging_in = true;
			//String rslt = HttpRequests.sendPost("http://dop.xol.io/api/login.php", "user="+guiHandler.getInputText(0).text+"&pass="+guiHandler.getInputText(1).text);
			new HttpRequestThread(this, "login", "http://dop.xol.io/api/login.php", "user="+guiHandler.getInputText(0).text+"&pass="+guiHandler.getInputText(1).text).start();

			//System.out.println("Session key : "+rslt);
		}
	}
	public boolean onClick(int posx,int posy,int button)
	{
		//new HttpRequestThread(this, "lel", "http://dop.xol.io/api/serverTokenObtainer.php", "username="+Client.username+"&sessid="+Client.session_key).start();

		if(button == 0)
			guiHandler.handleClick(posx, posy);
		return true;
	}
	public void handleHttpRequest(String info, String result) {
		//System.out.println("Request "+info+" got answered: "+result);
		if(info.equals("login"))
		{
			if(result == null)
			{
				if(autologin)
					this.eng.changeScene(new MainMenuScene(eng,0));
				message = "Can't connect to server.";
				return;
			}
			if(result.startsWith("ok"))
			{
				String session = result.split(":")[1];
				Client.username = guiHandler.getInputText(0).text;
				Client.session_key = session;
				Client.getConfig().setProp("autologin", "ok");
				Client.getConfig().setProp("user", guiHandler.getInputText(0).text);
				Client.getConfig().setProp("pass", guiHandler.getInputText(1).text);
				this.eng.changeScene(new MainMenuScene(eng,0));
			}
			else if(result.startsWith("ko"))
			{
				String reason = result.split(":")[1];
				if(reason.equals("notpremium"))
					message = Localizer.getText("gui.notpremium");
				else if(reason.equals("invalidcredentials"))
					message = Localizer.getText("gui.wrongpw");
			}
			else
			{
				message = Localizer.getText("gui.unknowerror");
			}
		}
	}
}
