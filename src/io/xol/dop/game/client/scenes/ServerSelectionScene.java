package io.xol.dop.game.client.scenes;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import io.xol.dop.game.client.Client;
import io.xol.dop.game.client.FastConfig;
import io.xol.dop.game.mode.GameModeType;
import io.xol.engine.base.InputAbstractor;
import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.base.XolioWindow;
import io.xol.engine.base.font.BitmapFont;
import io.xol.engine.base.font.FontRenderer;
import io.xol.engine.gui.ClickableButton;
import io.xol.engine.gui.FocusableObjectsHandler;
import io.xol.engine.gui.InputText;
import io.xol.engine.gui.KeyButtonDrawer;
import io.xol.engine.misc.HttpRequestThread;
import io.xol.engine.misc.HttpRequester;
import io.xol.engine.scene.Scene;

//(c) 2014 XolioWare Interactive

public class ServerSelectionScene extends Scene implements HttpRequester {

	FocusableObjectsHandler guiHandler = new FocusableObjectsHandler();
	int bgscroll = 0;
	boolean autologin;
	private boolean movedInList = false;
	
	public ServerSelectionScene(XolioWindow XolioWindow,int bg, boolean a) {
		super(XolioWindow);
		bgscroll = bg;
		
		guiHandler.add(new InputText());
		guiHandler.getInputText(0).setFocus(true);
		guiHandler.add(new ClickableButton(0,0,"Connect",BitmapFont.EDITUNDO,1));
		
		autologin = a;
		
		String lastServer = Client.clientConfig.getProp("last-server","");
		if(!lastServer.equals("")){
			guiHandler.getInputText(0).setText(lastServer);
			//System.out.println("ls-load:"+autologin);
		}
		new HttpRequestThread(this,"serversList","http://dop.xol.io/api/listServers.php","").start();
		//debug :c
		/*
		servers.add(new ServerData("localhost",30410));
		servers.add(new ServerData("xol.io",30410));*/
	}
	
	public void update()
	{
		if(autologin && !guiHandler.getInputText(0).text.equals(""))
			login();
		//bg
		bgscroll++;
		if(bgscroll >= 256)
			bgscroll = 0;
		ObjectRenderer.renderTexturedRect(XolioWindow.frameW / 2+bgscroll-128,XolioWindow.frameH / 2+bgscroll-128, XolioWindow.frameW*2f, XolioWindow.frameH*2f,0f, 0f, XolioWindow.frameW / 2 *2f, XolioWindow.frameH / 2 *2f, 128f,"gui/menubgyellow");
		//title
		FontRenderer.drawTextUsingSpecificFontRVBA(32,XolioWindow.frameH - 32*(1+getScreenSizeMultiplier()),0,32+getScreenSizeMultiplier()*16,"Select a server",BitmapFont.EDITUNDO,1f,0.8f,0.8f,0.2f);
		//gui
		int txtbox = XolioWindow.frameW-50-guiHandler.getButton(1).getWidth()*2-25;
		guiHandler.getInputText(0).drawWithBackGround(25,XolioWindow.frameH -50*(1+getScreenSizeMultiplier()), 32, BitmapFont.SMALLFONTS, txtbox);
		guiHandler.getButton(1).setPos(txtbox+50,XolioWindow.frameH -46*(1+getScreenSizeMultiplier()));
		guiHandler.getButton(1).draw();
		if(guiHandler.getButton(1).clicked)
			login();
		//tooltips
		drawRightedText("Back",12,48,16,1f,1,1,1f);
		KeyButtonDrawer.drawButtonForKeyRightSide(60, 20,Client.clientConfig.getIntProp("BACKKEY", "14") , 1);
		updateServers();
		super.update();
	}

	//Controls handling
	public boolean onKeyPress(int k) {
		if(k == FastConfig.keyTab)
			guiHandler.next();
		else if(k == 47 && (InputAbstractor.isKeyDown(29) || InputAbstractor.isKeyDown(157)))
		{
			Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
			if(clip.isDataFlavorAvailable(DataFlavor.stringFlavor))
			{
				String newTxt = null;
				try {
					newTxt = (String) clip.getData(DataFlavor.stringFlavor);
				} catch (UnsupportedFlavorException | IOException e) {
					e.printStackTrace();
				}
				if(newTxt != null)
					guiHandler.getInputText(0).text = newTxt;
			}
		}
		else if(k == 14 && (InputAbstractor.isKeyDown(29) || InputAbstractor.isKeyDown(157)))
		{
			guiHandler.getInputText(0).text = "";
		}
		else if(k == FastConfig.keyStart)
			login();
		else if(k == 63)
			new HttpRequestThread(this,"serversList","http://dop.xol.io/api/listServers.php","").start();
		else if(k == 64)
			f6();
		else if(k == FastConfig.keyBack)
			this.eng.changeScene(new MainMenuScene(eng,bgscroll));
		else if(k == FastConfig.keyUp)
		{
			movedInList = true;
			currentServer--;
		}
		else if(k == FastConfig.keyDown)
		{
			movedInList = true;
			currentServer++;
		}
		else
			guiHandler.handleInput(k);
		return false;
	}

	//Takes care of connecting to a server
	private void login() {
		String ip = guiHandler.getInputText(0).text;
		if(ip.length() == 0)
			return;
		Client.clientConfig.setProp("last-server", ip);
		if(!ip.contains(":"))
			ip = ip.concat(":30410");
		Client.clientConfig.save();
		this.eng.changeScene(new GameScene(eng,ip));
	}

	public boolean onClick(int posx,int posy,int button)
	{
		guiHandler.handleClick(posx, posy);
		return true;
	}
	void drawRightedText(String t,float decx,float height,int basesize,float r,float v,float b,float a)
	{
		FontRenderer.drawTextUsingSpecificFontRVBA(XolioWindow.frameW - decx -FontRenderer.getTextLengthUsingFont(basesize, t, BitmapFont.EDITUNDO),
				height, 0, basesize,t,BitmapFont.EDITUNDO,a,r,v,b);
	}
	int getScreenSizeMultiplier()
	{
		if(XolioWindow.frameW > 1200)
			return 2;
		return 1;
	}
	//Load-save server's list
	List<ServerData> servers = new ArrayList<ServerData>();
	int currentServer = 0;
	int oldServer = 0;
	
	private void updateServers() {
		if(servers.size() == 0)
			return;
		
		if(currentServer < 0)
			currentServer =0;
		if(currentServer > servers.size()-1)
			currentServer = servers.size()-1;
		
		if(movedInList)
		{
			movedInList = false;
			guiHandler.getInputText(0).text = servers.get(currentServer).ip+(servers.get(currentServer).port == 30410 ? "" : servers.get(currentServer).port);
		}
		
		int posy = XolioWindow.frameH-100*(1+getScreenSizeMultiplier());
		int i = 0;
		for(ServerData sd : servers)
		{
			sd.render(posy-i*70,i == currentServer);
			i++;
		}
	}
	
	private void f6()
	{
		int index = 0;
		for(ServerData sd : servers)
		{
			try{
				ServerData lel = new ServerData(sd.ip,sd.port);
				servers.set(index, lel);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			index++;
		}
	}
	
	//Sub-class for server data
	public class ServerData extends Thread{
		public String ip;
		public int port;
		String name = "Loading...";
		String description = "Loading...";
		String gameMode = "Loading...";
		String version = "Loading...";
		boolean infoLoaded = false;
		boolean infoError = false;
		long connectStart;
		long ping = 42;
		public ServerData(String ip,int port)
		{
			this.ip = ip;
			this.port = port;
			this.setName("ServerData updater "+ip+port);
			this.start();
		}

		public void render(int posy,boolean focus)
		{
			int offset = focus ? 32 : 0;
			ObjectRenderer.renderTexturedRect(52, posy, 64, 64, 0, 0+offset, 31, 32+offset, 64f, "gui/server_data");
			int width = XolioWindow.frameW-52-32-64;
			ObjectRenderer.renderTexturedRect(36+width/2, posy, width, 64, 16, 0+offset, 31, 32+offset, 64f, "gui/server_data");
			ObjectRenderer.renderTexturedRect(width+64, posy, 33*2, 64, 31, 0+offset, 64, 32+offset, 64f, "gui/server_data");
			//text
			FontRenderer.drawTextUsingSpecificFont(28,posy,0,32,(infoError ? "#FF0000" : "")+name+" #AAAAAA- "+ip+(port == 30410 ? "" : ":"+port)+"- "+version+(infoError ? "" : " - "+ping+"ms"),BitmapFont.SMALLFONTS);
			if(infoLoaded)
				FontRenderer.drawTextUsingSpecificFont(28,posy-26,0,32,(infoError ? "#FF0000" : "")+description + " #AAAAAA ("+gameMode+")",BitmapFont.SMALLFONTS);
			
		}
		
		public synchronized void run()
		{
			try{
			connectStart = System.currentTimeMillis();
			Socket socket = new Socket(ip,port);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF("info");
			ping = System.currentTimeMillis() - connectStart;
			String mdr = "";
			while(!mdr.startsWith("info/done"))
			{
				mdr = in.readUTF();
				if(mdr.startsWith("info/"))
				{
					String data[] = mdr.replace("info/", "").split(":");
					if(data[0].equals("name"))
						name = data[1];
					if(data[0].equals("version"))
						version = data[1];
					if(data[0].equals("motd"))
						description = data[1];
					if(data[0].equals("game"))
						gameMode = GameModeType.getType(Integer.parseInt(data[3])).makeGameModeByType().getName();
				}
				//System.out.println("server_prompter:"+mdr);
			}
			infoLoaded = true;
			in.close();
			out.close();
			socket.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				description = "Couldn't update.";
				gameMode = "Couldn't update.";
				version = "Unkwnow version";
				infoError = true;
				infoLoaded = true;
			}
		}
	}

	@Override
	public void handleHttpRequest(String info, String result) {
		//Will load fucking servers !
		servers.clear();
		if(info.equals("serversList"))
		{
			try{
				for(String line : result.split(";"))
				{
					String address = line.split(":")[2];
					servers.add(new ServerData(address,30410));
				}
			}
			catch(Exception e)
			{
				
			}
		}
	}
}
