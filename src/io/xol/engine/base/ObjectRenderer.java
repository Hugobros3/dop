package io.xol.engine.base;

import io.xol.engine.misc.ColorsTools;

import org.lwjgl.opengl.GL11;

//(c) 2014 XolioWare Interactive

public class ObjectRenderer {

	public static void renderTexturedRect(float xpos, float ypos, float w,
			float h, String tex) {
		renderTexturedRotatedRect(xpos, ypos, w, h, 0f, 0f, 0f, 1f, 1f, tex);
	}
	
	public static void renderTexturedRectAlpha(float xpos, float ypos, float w,
			float h, String tex,float a) {
		renderTexturedRotatedRectAlpha(xpos, ypos, w, h, 0f, 0f, 0f, 1f, 1f, tex,a);
	}

	public static void renderTexturedRect(float xpos, float ypos, float w,
			float h, float tcsx, float tcsy, float tcex, float tcey,
			float texSize, String tex) {
		renderTexturedRotatedRect(xpos, ypos, w, h, 0f, tcsx / texSize, tcsy
				/ texSize, tcex / texSize, tcey / texSize, tex);
	}

	public static void renderTexturedRotatedRect(float xpos, float ypos,
			float w, float h, float rot, float tcsx, float tcsy, float tcex,
			float tcey, String tex) {
		renderTexturedRotatedRectAlpha(xpos,ypos,w,h,rot,tcsx,tcsy,tcex,tcey,tex,1f);
	}
	
	public static void renderTexturedRotatedRectAlpha(float xpos, float ypos,
			float w, float h, float rot, float tcsx, float tcsy, float tcex,
			float tcey, String tex,float a) {
		renderTexturedRotatedRectRVBA(xpos,ypos,w,h,rot,tcsx,tcsy,tcex,tcey,tex,1f,1f,1f,a);
	}
	
	public static void renderTexturedRotatedRectRVBA(float xpos, float ypos,
			float w, float h, float rot, float tcsx, float tcsy, float tcex,
			float tcey, String tex,float r, float v, float b, float a) {
		
		if(tex.contains("../"))
			TexturesHandler.bindTexture("./" + tex.replace("../", "") + ".png");
		else
			TexturesHandler.bindTexture("./res/textures/" + tex + ".png");

		//<GL11.glColor3f(1f, 1f, 1.0f);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
				GL11.GL_NEAREST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(r,v,b, a);
		//GL11.glPushMatrix();
		// draw quad
		GL11.glTranslated(xpos, ypos, 0);
		GL11.glRotatef(rot, 0, 0, 1);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(tcsx, tcey);
		GL11.glVertex2f(-w / 2, -h / 2);
		GL11.glTexCoord2d(tcex, tcey);
		GL11.glVertex2f(+w / 2, -h / 2);
		GL11.glTexCoord2d(tcex, tcsy);
		GL11.glVertex2f(+w / 2, +h / 2);
		GL11.glTexCoord2d(tcsx, tcsy);
		GL11.glVertex2f(-w / 2, +h / 2);
		GL11.glEnd();
		GL11.glRotatef(-rot, 0, 0, 1);
		GL11.glTranslated(-xpos, -ypos, 0);
		GL11.glColor4f(1f,1f,1f,1f);
	}
	
	public static void renderColoredRect(float xpos, float ypos,float w, float h, float rot, String hex, float a)
	{
		int rgb[] = ColorsTools.hexToRGB(hex);
		renderColoredRect(xpos,ypos,w,h,rot,rgb[0]/255f,rgb[1]/255f,rgb[2]/255f,a);
	}
	
	public static void renderColoredRect(float xpos, float ypos,float w, float h, float rot, float r, float v, float b, float a)
	{
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(r, v, b, a);
		GL11.glTranslated(xpos, ypos, 0);
		GL11.glRotatef(rot, 0, 0, 1);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2f(-w / 2, -h / 2);
		GL11.glVertex2f(+w / 2, -h / 2);
		GL11.glVertex2f(+w / 2, +h / 2);
		GL11.glVertex2f(-w / 2, +h / 2);
		GL11.glEnd();
		GL11.glRotatef(-rot, 0, 0, 1);
		GL11.glTranslated(-xpos, -ypos, 0);
	}
	
	public static void drawLine(int fromx, int fromy, int tox, int toy, float w, String hex, float a)
	{
		int rgb[] = ColorsTools.hexToRGB(hex);
		drawLine(fromx, fromy, tox, toy, w, rgb[0]/255f,rgb[1]/255f,rgb[2]/255f, a);
	}
	
	public static void drawLine(int fromx, int fromy, int tox, int toy, float w, float r, float v, float b, float f)
	{
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(r,v,b,f);
		GL11.glLineWidth(w);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2f(fromx, fromy);
		GL11.glVertex2f(tox, toy);
		GL11.glEnd();
	}
	
	/*
	public static void renderTexturedRectNoCenter(float xpos, float ypos,
			float w, float h,  String tex) {
		TexturesHandler.bindTexture("./res/textures/" + tex + ".png");

		//GL11.glColor3f(1f, 1f, 1.0f);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
				GL11.GL_NEAREST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glPushMatrix();
		// draw quad
		GL11.glTranslated(xpos, ypos, 0);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(0, 1);
		GL11.glVertex2f(0, 0);
		GL11.glTexCoord2d(1, 1);
		GL11.glVertex2f(w, 0);
		GL11.glTexCoord2d(1, 0);
		GL11.glVertex2f(w, h);
		GL11.glTexCoord2d(0, 0);
		GL11.glVertex2f(0, h);
		GL11.glEnd();
		GL11.glPopMatrix();
	}

	public static void renderRotatedTexturedRect(float xpos, float ypos,
			float w, float h, String tex, float f) {
		renderTexturedRotatedRect(xpos, ypos, w, h, f, 0f, 0f, 1f, 1f, tex);

	}*/

}
