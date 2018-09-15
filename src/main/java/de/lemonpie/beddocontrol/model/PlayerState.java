package de.lemonpie.beddocontrol.model;

public enum PlayerState
{
	ACTIVE("Active", "activate"),
	OUT_OF_ROUND("Fold", "fold"),
	OUT_OF_GAME("Deactivated", "deactivate");

	private String name;
	private String midiActionName;

	PlayerState(String name, String midiActionName)
	{
		this.name = name;
		this.midiActionName = midiActionName;
	}

	public String getName()
	{
		return name;
	}

	public String getMidiActionName()
	{
		return midiActionName;
	}
}