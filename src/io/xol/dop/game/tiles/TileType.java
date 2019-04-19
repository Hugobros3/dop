package io.xol.dop.game.tiles;

//(c) 2014 XolioWare Interactive

public enum TileType {
	
	// Our main tile types - defines wich types of units can go over them.
    WATER, GROUND, AIR, SHORE, MOUNTAIN, UNPASSABLE,BRIDGE;

    
    public boolean hasGroundBorders()
    {
    	if(this.equals(GROUND))
    		return true;
    	if(this.equals(MOUNTAIN))
    		return true;
    	if(this.equals(UNPASSABLE))
    		return true;
    	return false;
    }
}
