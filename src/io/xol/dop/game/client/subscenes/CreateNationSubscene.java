package io.xol.dop.game.client.subscenes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.codec.binary.Base64;

import io.xol.dop.game.client.Client;
import io.xol.dop.game.client.FastConfig;
import io.xol.dop.game.common.nations.NationsColors;
import io.xol.engine.base.ObjectRenderer;
import io.xol.engine.base.TexturesHandler;
import io.xol.engine.base.XolioWindow;
import io.xol.engine.base.font.BitmapFont;
import io.xol.engine.base.font.FontRenderer;
import io.xol.engine.gui.ClickableButton;
import io.xol.engine.gui.CorneredBoxDrawer;
import io.xol.engine.gui.DummyFocusable;
import io.xol.engine.gui.FocusableObjectsHandler;
import io.xol.engine.gui.InputText;
import io.xol.engine.misc.ColorsTools;
import io.xol.engine.misc.FoldersUtils;
import io.xol.engine.scene.Scene;
import io.xol.engine.scene.SubScene;

//(c) 2014 XolioWare Interactive

public class CreateNationSubscene extends SubScene{

	FocusableObjectsHandler guiHandler = new FocusableObjectsHandler();
	boolean createOrEdit = false;
	
	public InputText nationName = new InputText();
	public InputText nationDescription = new InputText();
	public InputText nationPassword = new InputText();
	public DummyFocusable color = new DummyFocusable();
	public DummyFocusable fileChooser = new DummyFocusable();
	
	public ClickableButton create = new ClickableButton(0, 0, "Create Nation", BitmapFont.SMALLFONTS, 1);
	
	public int selectedColor;
	File selectedFile = null;
	
	public CreateNationSubscene(Scene p) {
		super(p);
		nationName.focus = true;
		guiHandler.add(nationName);
		guiHandler.add(nationDescription);
		guiHandler.add(nationPassword);
		guiHandler.add(color);
		guiHandler.add(fileChooser);
		guiHandler.add(create);
		createOrEdit = true;
	}

	public void update()
	{
		int boxw = XolioWindow.frameW-150;
		int boxh = XolioWindow.frameH-150;
		//Drawing
		CorneredBoxDrawer.drawCorneredBox(XolioWindow.frameW/2, XolioWindow.frameH/2, boxw, boxh, 8, "gui/nationsSelectBG");
		FontRenderer.drawTextUsingSpecificFontHex(90, XolioWindow.frameH-120, 0, 32, createOrEdit ? "CREATE A NATION" : "EDIT A NATION", BitmapFont.EDITUNDO, "00A0FF", 1);
		// gui
		int decal = 0;
		decal = FontRenderer.drawTextUsingSpecificFontHex(90, XolioWindow.frameH-155, 0, 32, "Nation name :", BitmapFont.SMALLFONTS, "FFFFFF", 1);
		nationName.drawWithBackGround(90+decal+10,  XolioWindow.frameH-155, 32, BitmapFont.SMALLFONTS, boxw-decal-30);
		decal = FontRenderer.drawTextUsingSpecificFontHex(90, XolioWindow.frameH-155-48, 0, 32, "Nation description :", BitmapFont.SMALLFONTS, "FFFFFF", 1);
		nationDescription.drawWithBackGround(90+decal+10,  XolioWindow.frameH-155-48, 32, BitmapFont.SMALLFONTS, boxw-decal-30);
		decal = FontRenderer.drawTextUsingSpecificFontHex(90, XolioWindow.frameH-155-96, 0, 32, "Nation password :", BitmapFont.SMALLFONTS, "FFFFFF", 1);
		nationPassword.drawWithBackGround(90+decal+10,  XolioWindow.frameH-155-96, 32, BitmapFont.SMALLFONTS, boxw-decal-30);
		
		decal = FontRenderer.drawTextUsingSpecificFontHex(90, XolioWindow.frameH-155-96-48, 0, 32, "Nation color :", BitmapFont.SMALLFONTS, "FFFFFF", 1);
		decal = FontRenderer.drawTextUsingSpecificFontHex(90+decal, XolioWindow.frameH-155-96-48, 0, 32, " #"+ColorsTools.rgbToHex(NationsColors.getColor(selectedColor))+""+NationsColors.getColorName(selectedColor)+"#FFFFFF (Arrows to change when selected)", BitmapFont.SMALLFONTS, "00A0FF", color.focus ? 1f : 0.5f);
		
		decal = FontRenderer.drawTextUsingSpecificFontHex(90, XolioWindow.frameH-155-96-96, 0, 32, "Nation flag :", BitmapFont.SMALLFONTS, "FFFFFF", 1);
		decal = FontRenderer.drawTextUsingSpecificFontHex(90+decal, XolioWindow.frameH-155-96-96, 0, 32, (selectedFile == null ) ? "<Select File>" : selectedFile.getAbsolutePath(), BitmapFont.SMALLFONTS, "FFFFFF", fileChooser.focus ? 1f : 0.5f);
		
		ObjectRenderer.drawLine(80,180,XolioWindow.frameW-80,180, 2, 0, 0, 0, 0.5f);
		
		FontRenderer.drawTextUsingSpecificFontHex(90, 220, 0, 32, "When you create a new nation you automatically become it's leader.", BitmapFont.SMALLFONTS, "00A0FF", 0.8f);
		FontRenderer.drawTextUsingSpecificFontHex(90, 190, 0, 32, "If the password is blank, anyone can freely join your nation.", BitmapFont.SMALLFONTS, "00A0FF", 0.8f);
		
		create.setPos(boxw-create.getWidth(), 100);
		create.draw();
		
		int mul = 4;
		if(selectedFile != null)
			ObjectRenderer.renderTexturedRect(150,125, 24*mul, 16*mul, 0, 0, 24, 32, 32, "cache/upload_temp");
		
		FontRenderer.drawTextUsingSpecificFontHex(205, 130, 0, 32, "#"+ColorsTools.rgbToHex(NationsColors.getColor(selectedColor))+nationName.text, BitmapFont.SMALLFONTS, "00A0FF", 1);
		FontRenderer.drawTextUsingSpecificFontHex(205, 100, 0, 32, nationDescription.text, BitmapFont.SMALLFONTS, "FFFFFF", 1);
		
		//FontRenderer.drawTextUsingSpecificFontHex(90+decal+30, 80, 0, 32, "Join or create a nation to play", BitmapFont.SMALLFONTS, "00A0FF", 1);
		if(create.clicked)
		{
			create.clicked = false;
			tryCreating();
		}
	}
	
	private void tryCreating() {
		if(!nationName.text.equals(""))
		{
			byte[] data = null;
			if(selectedFile != null)
			{
				try {
					data = Files.readAllBytes(selectedFile.toPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			String initMSG = "player/makeNation:"+nationName.text+":"+(nationDescription.text.equals("") ? "null" : nationDescription.text)+
					":"+(nationPassword.text.equals("") ? "null" : nationPassword.text)+":"+selectedColor+":"+(selectedFile == null ? "none" : Base64.encodeBase64String(data));
			System.out.println(initMSG);
			Client.connect.send(initMSG);
		}
		else
		{
			parent.setSubscene(new MessageBoxSubscene(parent,new String[]{"Err...","Seems like you forgot a few essential things..."},this));
		}
	}

	//Controls handling
	public boolean onKeyPress(int k) {
		if(k == Client.clientConfig.getIntProp("TABKEY", "15"))
			guiHandler.next();
		else if(fileChooser.focus && k == FastConfig.keyStart)
		{
			showFileChooser();
		}
		else if(color.focus)
		{
			if( k == FastConfig.keyUp)
				selectedColor--;
			else if( k == FastConfig.keyDown)
				selectedColor++;
			
			if(selectedColor < 0)
				selectedColor = 0;
			if(selectedColor >= NationsColors.getMaxColors())
				selectedColor = NationsColors.getMaxColors()-1;
		}
		
		else if(k == Client.clientConfig.getIntProp("BACKKEY", "14"))
			parent.destroySubscene();
		else
			guiHandler.handleInput(k);
		return false;
	}

	public void showFileChooser()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setSelectedFile(new File(System.getProperty("user.dir")+"/"));
		fileChooser.setFileFilter(new FileFilter(){
			 @Override
	         public boolean accept(File f)
			 {
				 String fName = f.getName().toUpperCase();
				 if (fName.endsWith(".PNG") || f.isDirectory()) {
				 	return true;
				 } else {
					 return false;   
				 }}
			 @Override
			 public String getDescription() {
				 return "DoP Requires 24x16 flags in .png format";
			 }});
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
        	File image = fileChooser.getSelectedFile();
        	try{
	        	BufferedImage logo = ImageIO.read(image);
	        	if(logo.getWidth() == 24 && logo.getHeight() == 16)
	        	{
	        		selectedFile = image;
	        		TexturesHandler.freeTexture("cache/upload_temp");
	        		File temp = new File("./res/textures/cache/upload_temp.png");
	        		if(temp.exists())
	        			temp.delete();
	        		FoldersUtils.copyFile(selectedFile, temp);
	        	}
	        	else
	        		System.out.println("Wrong dimensions !");
        	}
        	catch(Exception e)
        	{
        		e.printStackTrace();
        	}
        }
	}
	
	public boolean onClick(int posx,int posy,int button)
	{
		guiHandler.handleClick(posx, posy);
		return true;
	}
}
