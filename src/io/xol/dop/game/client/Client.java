package io.xol.dop.game.client;

//(c) 2014 XolioWare Interactive

import java.io.IOException;

import io.xol.dop.game.World;
import io.xol.dop.game.client.net.ServerConnection;
import io.xol.dop.game.client.renderer.TileRenderer;
import io.xol.dop.game.client.scenes.GameScene;
import io.xol.dop.game.common.VersionInfo;
import io.xol.dop.game.common.nations.NationsColors;
import io.xol.dop.game.units.Unit;
import io.xol.engine.base.XolioWindow;
import io.xol.engine.locale.Localizer;
import io.xol.engine.misc.ConfigFile;
import io.xol.engine.misc.IconLoader;
import io.xol.engine.misc.NativesLoader;
import io.xol.engine.scene.Scene;
import io.xol.engine.sound.SoundManager;

public class Client{
	public static ConfigFile clientConfig = new ConfigFile("config/client.cfg");
	
	public static String username = "";
	public static String session_key = "";
	
	public static ServerConnection connect;
	
	public static boolean shouldUpdate = false;

	public static boolean offline = false;
	
	public static XolioWindow windows;
	
	public static void main(String[] args) {
		System.out.println("DoP "+VersionInfo.get()+" by XolioWare Interactive");
		FastConfig.load();
		NativesLoader.load();
		Localizer.loadLocale(FastConfig.locale);
		Unit.initUnits();
		//TextKeys.init();
		windows = new XolioWindow("Despotism Of Power",-1,-1);
		windows.run();
	}

	public static void onStart() {
		IconLoader.load();
		TileRenderer.generateTexture();
		TileRenderer.loadTexture();
		SoundManager.init();
		World.loadTimeCycleColors();
		NationsColors.loadDefaultColorScheme();
	}

	public static void onClose() {
		if(connect != null)
			connect.interrupt();
		SoundManager.close();
		clientConfig.save();
		if(shouldUpdate)
		{
			System.out.println("Starting updater app");
			try {
				Runtime.getRuntime().exec("java -jar updater.jar -auto");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
	}
	
	public static ConfigFile getConfig()
	{
		return clientConfig;
	}
	
	public static boolean isIngame()
	{
		if(windows == null)
		{
			System.out.println("kernel panic");
			return false;
		}
		Scene scene = windows.getCurrentScene();
		return scene instanceof GameScene;
	}
	
	public static GameScene getGame()
	{
		return (GameScene)windows.getCurrentScene();
	}
}
