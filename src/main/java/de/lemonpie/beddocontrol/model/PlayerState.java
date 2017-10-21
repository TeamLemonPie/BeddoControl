package de.lemonpie.beddocontrol.model;

public enum PlayerState
{
	ACTIVE("Active"), 
	OUT_OF_ROUND("Fold"), 
	OUT_OF_GAME("Deactivated");
	
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