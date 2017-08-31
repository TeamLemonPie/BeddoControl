package de.lemonpie.beddocontrol.model;

public enum PlayerState
{
	ACTIVE("Aktiv"), 
	OUT_OF_ROUND("Fold"), 
	OUT_OF_GAME("Deaktiviert");
	
	private String name;

	private PlayerState(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
}