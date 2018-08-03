package de.lemonpie.beddocontrol.model.card;

public enum CardSymbol
{

	BACK("Back", "B"),
	HEART("He", "H"),
	CROSS("Kr", "C"),
	SPADES("Pi", "P"),
	DIAMONDS("Ka", "K");

	private String shortName;
	private String shortCode;

	CardSymbol(String shortName, String shortCode)
	{
		this.shortName = shortName;
		this.shortCode = shortCode;
	}

	public static CardSymbol get(String name)
	{
		for(CardSymbol cardSymbol : values())
		{
			if(cardSymbol.shortName.equals(name))
			{
				return cardSymbol;
			}
		}
		return null;
	}

	public String getShortName()
	{
		return shortName;
	}

	public String getShortCode()
	{
		return shortCode;
	}

	@Override
	public String toString()
	{
		return shortName;
	}
}
