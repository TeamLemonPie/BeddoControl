package de.lemonpie.beddocontrol.model;

public enum HandType
{
	HIGHEST_CARD(1),
	PAIR(2),
	TWO_PAIRS(3),
	THREE_OF_A_KIND(4),
	STRAIGHT(5),
	FLUSH(6),
	FULL_HOUSE(7),
	FOUR_OF_A_KIND(8),
	STRAIGHT_FLUSH(9),
	ROYAL_FLUSH(10);

	private int value;

	HandType(int value)
	{
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}
}