package io.xol.engine.gui;

//(c) 2014 XolioWare Interactive

import io.xol.engine.base.ObjectRenderer;

public class CorneredBoxDrawer {

	
	public static void drawCorneredBox(float posx,float posy,int width,int height,int cornerSize,String texture)
	{
		//corner up-left
		ObjectRenderer.renderTexturedRotatedRect(posx-width/2, posy+height/2,cornerSize*2, cornerSize*2, 0, 0, 0f, 1/4f, 1/4f, texture);
		//corner up-right
		ObjectRenderer.renderTexturedRotatedRect(posx+width/2, posy+height/2,cornerSize*2, cornerSize*2, 0, 3/4f, 0f, 1f, 1/4f, texture);
		//left
		ObjectRenderer.renderTexturedRotatedRect(posx-width/2, posy,cornerSize*2, height-cornerSize*2, 0, 0, 1/4f, 1/4f, 3/4f, texture);
		//up
		ObjectRenderer.renderTexturedRotatedRect(posx, posy+height/2,width-cornerSize*2, cornerSize*2, 0, 1/4f, 0f, 3/4f, 1/4f, texture);
		//center
		ObjectRenderer.renderTexturedRotatedRect(posx, posy,width-cornerSize*2, height-cornerSize*2, 0, 1/4f, 1/4f, 3/4f, 3/4f, texture);
		//back
		ObjectRenderer.renderTexturedRotatedRect(posx, posy-height/2,width-cornerSize*2, cornerSize*2, 0, 1/4f, 3/4f, 3/4f, 1f, texture);
		//left
		ObjectRenderer.renderTexturedRotatedRect(posx+width/2, posy,cornerSize*2, height-cornerSize*2, 0, 3/4f, 1/4f, 1f, 3/4f, texture);
		//corner up-left
		ObjectRenderer.renderTexturedRotatedRect(posx-width/2, posy-height/2,cornerSize*2, cornerSize*2, 0, 0, 3/4f, 1/4f, 1f, texture);
		//corner up-right
		ObjectRenderer.renderTexturedRotatedRect(posx+width/2, posy-height/2,cornerSize*2, cornerSize*2, 0, 3/4f, 3/4f, 1f, 1f, texture);
		
	}
}
