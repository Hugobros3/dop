package io.xol.dop.game.mode;

//(c) 2014 XolioWare Interactive

public enum GameModeType {
	MULTIPLAYER_EDITOR,
	CONTINUOUS_GAME,
	TURN_BY_TURN_GAME,
	TIMED_TURNS_GAME;
	
	public static GameModeType getType(int i)
	{
		if(i < 0 || i >= values().length)
			return null;
		return values()[i];
	}
	
	public static int getId(GameModeType type)
	{
		int id = -1;
		int i = 0;
		for(GameModeType t : values())
		{
			if(t.equals(type))
				id = i;
			i++;
		}
		return id;
	}
	
	public static GameMode makeGameModeByType(GameModeType type)
	{
		GameMode newGm = null;
		if(type.equals(GameModeType.MULTIPLAYER_EDITOR))
			newGm = new GameModeMultiplayerEditor();
		if(type.equals(GameModeType.CONTINUOUS_GAME))
			newGm = new GameModeContinuousGame();
		return newGm;
	}

	public int getId() {
		return getId(this);
	}

	public GameMode makeGameModeByType() {
		return makeGameModeByType(this);
	}
}


