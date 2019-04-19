package io.xol.dop.game.client.scenes;

import io.xol.dop.game.World;
import io.xol.dop.game.WorldRemote;
import io.xol.dop.game.client.Client;
import io.xol.dop.game.client.FastConfig;
import io.xol.dop.game.client.bits.Camera;
import io.xol.dop.game.client.bits.ChatPanel;
import io.xol.dop.game.client.bits.ControlBits;
import io.xol.dop.game.client.bits.Cursor;
import io.xol.dop.game.client.bits.EditorBits;
import io.xol.dop.game.client.fx.EffectsManager;
import io.xol.dop.game.client.fx.FXBase;
import io.xol.dop.game.client.bits.GameNet;
import io.xol.dop.game.client.bits.GuiTopBar;
import io.xol.dop.game.client.bits.NationsInfo;
import io.xol.dop.game.client.net.ServerConnection;
import io.xol.dop.game.client.renderer.WorldRenderer;
import io.xol.dop.game.client.subscenes.MessageBoxSubscene;
import io.xol.dop.game.client.subscenes.NationsSubscene;
import io.xol.dop.game.client.subscenes.NotPauseSubscene;
import io.xol.dop.game.common.VersionInfo;
import io.xol.dop.game.mode.GameMode;
import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.base.XolioWindow;
import io.xol.engine.base.font.BitmapFont;
import io.xol.engine.base.font.FontRenderer;
import io.xol.engine.scene.Scene;

//(c) 2014 XolioWare Interactive

public class GameScene extends Scene {

	//World
	public boolean isRemote = false;
	public World world = null;
	//server info
	public String address = "localhost";
	public String serverName = "Error";
	public String serverMotd = "Error";
	public String serverVersion = "Error";
	public String gameName = "Error";
	public int gtuh = 0;
	public GameMode gameMode;
	public int cuco = -1;
	public int maxco = -1;
	long lastUpdate = -1;
	//bits
	ChatPanel chat;
	public GuiTopBar topBar;
	public Camera cam;
	public GameNet net;
	public EditorBits editor;
	public ControlBits control;
	public NationsInfo nations;
	public Cursor cursor;
	
	public GameScene(XolioWindow eng, String address)
	{
		super(eng);
		isRemote = true;
		if(!address.contains(":"))
			return;
		this.address = address;
		String ip = address.split(":")[0];
		int port = Integer.parseInt(address.split(":")[1]);
		Client.connect = new ServerConnection(ip,port);
		topBar = new GuiTopBar(this);
		cam = new Camera(this);
		net = new GameNet(this);
		cursor = new Cursor(this);
		chat = new ChatPanel(this);
	}

	public void update() {
		net.update();
		if(this.subscene == null)
			cam.update();
		chat.update();
		//world
		if(world != null)
		{
			if(isRemote)
			{
				WorldRemote remoteWorld = (WorldRemote) world;
				//net
				if(remoteWorld.connected)
				{
					if(remoteWorld.renderer == null) 
					{
						// Create the renderer and fx manager once the world is connected !
						remoteWorld.fxManager = new EffectsManager(world);
						remoteWorld.renderer = new WorldRenderer(world); 
					}
					remoteWorld.updateView(cam.cameraX, cam.cameraY);
				}
				if(remoteWorld.markClean())
					remoteWorld.renderer.updateRender();
				gameInfo();
			}
			//render
			if(world.renderer != null)
			{
				//System.out.println("rendering");
				world.renderer.render(cam.cameraX, cam.cameraY);
			}
			if(world.fxManager != null)
				world.fxManager.renderEffects(cam.cameraX*32, cam.cameraY*32);
		}
		else
		{
			//While we wait for connection we display a bit information about the status...
			ObjectRenderer.renderTexturedRectAlpha(XolioWindow.frameW/2, XolioWindow.frameH/2, XolioWindow.frameW/1.5f, XolioWindow.frameH/1.5f, "misc/greytex", 0.5f);
			FontRenderer.drawTextUsingSpecificFont(XolioWindow.frameW-XolioWindow.frameW/1.5f, XolioWindow.frameH/2, 0, 32, Client.connect.connectionStatus, BitmapFont.SMALLFONTS);
		}
		if(subscene == null)
			cursor.update();
		if(editor != null)
			editor.update();
		if(nations != null)
		{
			if(NationsInfo.playerNation == -1 && subscene == null)
			{
				subscene = new NationsSubscene(this,false);
				((NationsSubscene)subscene).updateData(nations);
			}

			nations.renderNationsPanel();
			if(NationsInfo.playerNation != -1 && control != null)
				control.update();
		}
		//gui
		gameInfo();
		chat.draw(10);
		//debug monitor
		FontRenderer.drawText(0, 0, 0, 12,"camX:" + cam.cameraX + ",camY:" + cam.cameraY + ",cx:"+ (cursor.cursorX+cam.cameraX) + ",cy:" + (cursor.cursorY+cam.cameraY)+",secX:"+(cursor.cursorX+cam.cameraX)/32 + ",secY:" + (cursor.cursorY+cam.cameraY)/32+" "+XolioWindow.getFPS()+"fps - DoP "+VersionInfo.version);
		//debug crade
		/*if(InputAbstractor.isKeyDown(205))
			addEffect(new FXDamage());
			//addEffect(new FXSmoke(Mouse.getX()+cam.cameraX*32,Mouse.getY()+cam.cameraY*32));*/
		super.update();
	}

	public void addEffect(FXBase fx)
	{
		if(world != null && world.fxManager != null)
			world.fxManager.addEffect(fx);
	}
	
	public boolean onKeyPress(int k) {
		if(subscene != null)
			return subscene.onKeyPress(k);
		if(chat.chatting)
		{
			chat.key(k);
			return true;
		}
		if(k == 18)
			this.setSubscene(new MessageBoxSubscene(this,new String[]{"Erreur !","Vous n'avez pas pensé à tout, il reste surement des bugs à éradiquer.","Allez donc bosser un peu, on croirait voir Dean Hall."},null));
		if(control != null && control.onKeyPress(k))
			return false;
		if(editor != null)
			return editor.onKeyPress(k);
		if(k == FastConfig.keyBack)
			this.setSubscene(new NotPauseSubscene(this));
		else
			chat.key(k);
		return false;
	}
	
	public boolean onScroll(int dx)
	{
		if(subscene != null)
			return subscene.onWheel(dx);
		if(editor != null)
			editor.onWheel(dx);
		return true;
	}
	
	public boolean onClick(int posx,int posy,int button)
	{
		if(subscene != null)
			return subscene.onClick(posx, posy, button);
		if(posy/32 <= (int) (Math.ceil((XolioWindow.frameH-64)/32f)-1*getScreenSizeMultiplier()))
		{
			if(editor != null)
				return editor.handle(button,posx,posy);
			if(NationsInfo.playerNation != -1 && control != null)
				return control.handle(button,posx,posy);
		}
		else
		{
			if(topBar != null)
				topBar.handleClick(posx,posy,button);
			//System.out.println("click in up");
		}
		return false;
	}
	
	private void gameInfo() {
		//update
		if(System.currentTimeMillis()-lastUpdate > 5000)
		{
			//System.out.println("debug: auto-updating dop server shit thingy");
			Client.connect.send("info");
			Client.connect.send("player/info");
			Client.connect.send("player/nation");
			lastUpdate = System.currentTimeMillis();
		}
		topBar.update();
	}
	
	public void backToMenu()
	{
		if(isRemote)
		{
			if(world != null)
				((WorldRemote)world).remove();
			Client.connect.send("co/off");
			Client.connect.close();
		}
		this.eng.changeScene(new MainMenuScene(eng,0));
	}
	
	public void handleHttpRequest(String info, String result) {
		System.out.println("Request "+info+" got answered: "+result);
	}
	
	int getScreenSizeMultiplier()
	{
		if(XolioWindow.frameW > 1200)
			return 2;
		return 1;
	}
}
