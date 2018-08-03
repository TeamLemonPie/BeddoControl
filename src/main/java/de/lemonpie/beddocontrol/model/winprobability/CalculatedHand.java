package de.lemonpie.beddocontrol.model.winprobability;

import de.lemonpie.beddocontrol.model.HandType;
import de.lemonpie.beddocontrol.model.card.Card;

import java.util.ArrayList;

public class CalculatedHand
{
	private HandType handType;
	private ArrayList<Card> highestCards;

	public CalculatedHand(HandType handType, ArrayList<Card> highestCards)
	{
		this.handType = handType;
		this.highestCards = highestCards;
	}

	public CalculatedHand(HandType handType, Card highestCard)
	{
		this.handType = handType;
		this.highestCards = new ArrayList<>();
		this.highestCards.add(highestCard);
	}

	public HandType getHandType()
	{
		return handType;
	}

	public ArrayList<Card> getHighestCard()
	{
		return highestCards;
	}

	@Override
	public String toString()
	{
		return "CalculatedHand [handType=" + handType + ", highestCards=" + highestCards + "]";
	}
}