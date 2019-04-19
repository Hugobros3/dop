package io.xol.engine.sound;

import java.io.File;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.codecs.CodecJOgg;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryJavaSound;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

//(c) 2014 XolioWare Interactive

public class SoundManager {

	public static SoundSystem sndSystem;
	
	public static void playBGM(String zik,boolean loop)
	{
		sndSystem.backgroundMusic("zik_"+zik, "./res/sound/zik/"+zik+".ogg", loop);
	}
	
	public static void stopBGM(String zik)
	{
		//sndSystem.
	}
	
	public static void playSFX(String sfx)
	{
		playSFXWav(sfx);
	}
	
	public static void playSFXWav(String sfx)
	{
		try{
		sndSystem.quickPlay(false,/*"file://"+System.getProperty("user.dir")+"/res/sound/sfx/"+sfx+".wav"*/sfx+".wav",false,0,0,0,SoundSystemConfig.ATTENUATION_NONE,0);
		}
		catch(Exception e)
		{
			
		}
	}
	
	public static void playSFXOgg(String sfx)
	{
		sndSystem.newSource(false, "sfx_"+sfx,"res/sound/sfx/"+sfx+".ogg", false,0,0,0,SoundSystemConfig.ATTENUATION_NONE,0);
	}
	
	public static void stopAnySound(String zik)
	{
		/*for(String s : sndSystem.)
		{
			if(sndSystem.playing(s))
				sndSystem.removeSource(s);
		}*/
	}
	
	static void removeUnusedSources()
	{
		
	}
	
	@SuppressWarnings("deprecation")
	public static void init(){
		try{
			System.out.println("Initializing sound library...");
			//SoundSystemConfig.setLogger(null);
			//TODO : Make this crap shut up
			SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
			SoundSystemConfig.addLibrary(LibraryJavaSound.class);
			sndSystem = new SoundSystem();
			boolean openAL = true;
			if(openAL)
				sndSystem.switchLibrary(LibraryLWJGLOpenAL.class);
			else
				sndSystem.switchLibrary(LibraryJavaSound.class);
			//Load codecs
			SoundSystemConfig.setCodec("wav", CodecWav.class);
			SoundSystemConfig.setCodec("ogg", CodecJOgg.class);
			//Load sounds
			int count_sfx = 0;
			for(File f : new File("./res/sound/sfx/").listFiles())
			{
				if(f.isFile())
				{
					String name = (String) f.getName().subSequence(0, f.getName().length()-0);
					sndSystem.loadSound(f.toURL(), name);
					//System.out.println("Loading sound "+f.toURL()+" as "+name);
					count_sfx++;
				}
			}
			System.out.println(count_sfx+" sounds loaded.");
		}
		catch(Exception e)
		{
			System.out.println("Fatal error happened during sound library loading !");
			e.printStackTrace();
		}
	}
	
	public static void close(){
		sndSystem.cleanup();
	}
}
