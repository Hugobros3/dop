package io.xol.dop.game.pathfinder;

import io.xol.engine.base.ObjectRenderer;

//(c) 2014 XolioWare Interactive

public class PathRenderer {

	public static void renderPath(AcceptablePath acceptablePath,int decalX, int decalY) {
		int lastcx = -1;
		int lastcy = -1;
		for(int i = 0; i < acceptablePath.path.length/2; i++)
		{
			//System.out.println("mdr"+i+"-"+acceptablePath.path[i*2]+" - "+ acceptablePath.path[i*2+1]+" :: "+scene.cam.cameraX+" : "+scene.cam.cameraY);
			int cx = acceptablePath.path[i*2];
			int cy = acceptablePath.path[i*2+1];
			
			if(i < acceptablePath.path.length/2-1)
			{
				int nx = acceptablePath.path[i*2+2];
				int ny = acceptablePath.path[i*2+3];
				if(lastcx != -1)
				{	
					//So not first !
					if(lastcx == cx-1)
					{
						if(nx == cx+1)
							ObjectRenderer.renderTexturedRotatedRectAlpha(cx*32+decalX, cy*32+decalY,32,32, 0, 0/64f, 0/64f, 16/64f, 16/64f, "misc/arrows", 1f);
						if(ny == cy+1)
							ObjectRenderer.renderTexturedRotatedRectAlpha(cx*32+decalX, cy*32+decalY,32,32, 0, 48/64f, 16/64f, 64/64f, 32/64f, "misc/arrows", 1f);
						if(ny == cy-1)
							ObjectRenderer.renderTexturedRotatedRectAlpha(cx*32+decalX, cy*32+decalY,32,32, 0, 48/64f, 0/64f, 64/64f, 16/64f, "misc/arrows", 1f);
					}
					if(lastcx == cx+1)
					{
						if(nx == cx-1)
							ObjectRenderer.renderTexturedRotatedRectAlpha(cx*32+decalX, cy*32+decalY,32,32, 0, 0/64f, 0/64f, 16/64f, 16/64f, "misc/arrows", 1f);
						if(ny == cy+1)
							ObjectRenderer.renderTexturedRotatedRectAlpha(cx*32+decalX, cy*32+decalY,32,32, 0, 32/64f, 16/64f, 48/64f, 32/64f, "misc/arrows", 1f);
						if(ny == cy-1)
							ObjectRenderer.renderTexturedRotatedRectAlpha(cx*32+decalX, cy*32+decalY,32,32, 0, 32/64f, 0/64f, 48/64f, 16/64f, "misc/arrows", 1f);
					}
					if(lastcy == cy-1)
					{
						if(ny == cy+1)
							ObjectRenderer.renderTexturedRotatedRectAlpha(cx*32+decalX, cy*32+decalY,32,32, 0, 16/64f, 0/64f, 32/64f, 16/64f, "misc/arrows", 1f);
						if(nx == cx+1)
							ObjectRenderer.renderTexturedRotatedRectAlpha(cx*32+decalX, cy*32+decalY,32,32, 0, 32/64f, 0/64f, 48/64f, 16/64f, "misc/arrows", 1f);
						if(nx == cx-1)
							ObjectRenderer.renderTexturedRotatedRectAlpha(cx*32+decalX, cy*32+decalY,32,32, 0, 48/64f, 0/64f, 64/64f, 16/64f, "misc/arrows", 1f);
					}
					if(lastcy == cy+1)
					{
						if(ny == cy-1)
							ObjectRenderer.renderTexturedRotatedRectAlpha(cx*32+decalX, cy*32+decalY,32,32, 0, 16/64f, 0/64f, 32/64f, 16/64f, "misc/arrows", 1f);
						if(nx == cx+1)
							ObjectRenderer.renderTexturedRotatedRectAlpha(cx*32+decalX, cy*32+decalY,32,32, 0, 32/64f, 16/64f, 48/64f, 32/64f, "misc/arrows", 1f);
						if(nx == cx-1)
							ObjectRenderer.renderTexturedRotatedRectAlpha(cx*32+decalX, cy*32+decalY,32,32, 0, 48/64f, 16/64f, 64/64f, 32/64f, "misc/arrows", 1f);
					}
					//debug
					//ObjectRenderer.renderTexturedRectAlpha(cx*32+decalX, cy*32+decalY,32,32, "misc/path",0.5f);
				}
			}
			else
			{
				//ObjectRenderer.renderTexturedRectAlpha(cx*32+decalX, cy*32+decalY,32,32, "misc/path",1f);
				if(lastcx == cx-1)
					ObjectRenderer.renderTexturedRotatedRectAlpha(cx*32+decalX, cy*32+decalY,32,32, 0, 0/64f, 32/64f, 16/64f, 48/64f, "misc/arrows", 1f);
				if(lastcx == cx+1)
					ObjectRenderer.renderTexturedRotatedRectAlpha(cx*32+decalX, cy*32+decalY,32,32, 0, 0/64f, 16/64f, 16/64f, 32/64f, "misc/arrows", 1f);
				if(lastcy == cy+1)
					ObjectRenderer.renderTexturedRotatedRectAlpha(cx*32+decalX, cy*32+decalY,32,32, 0, 16/64f, 32/64f, 32/64f, 48/64f, "misc/arrows", 1f);
				if(lastcy == cy-1)
					ObjectRenderer.renderTexturedRotatedRectAlpha(cx*32+decalX, cy*32+decalY,32,32, 0, 16/64f, 16/64f, 32/64f, 32/64f, "misc/arrows", 1f);
				
			}
			
			lastcx = cx;
			lastcy = cy;
		}
	}
}
