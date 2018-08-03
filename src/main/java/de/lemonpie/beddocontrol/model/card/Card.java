package de.lemonpie.beddocontrol.model.card;


import java.util.Objects;

public class Card implements Comparable<Object>
{
	private CardSymbol symbol;
	private CardValue value;

	public static final Card EMPTY = new Card();

	public Card(CardSymbol symbol, CardValue value)
	{
		this.symbol = symbol;
		this.value = value;
	}

	private Card()
	{
		this.symbol = CardSymbol.BACK;
		this.value = CardValue.BACK;
	}

	public CardSymbol getSymbol()
	{
		return symbol;
	}

	public CardValue getValue()
	{
		return value;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof Card)) return false;
		Card card = (Card) o;
		return symbol == card.symbol &&
				value == card.value;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(symbol, value);
	}

	public String getName()
	{
		if(this == EMPTY)
		{
			return "back";
		}

		return symbol + "-" + value;
	}

	@Override
	public String toString()
	{
		return "Card [symbol=" + symbol + ", value=" + value + "]";
	}

	@Override
	public int compareTo(Object obj)
	{
		Card other = (Card) obj;
		return Integer.compare(this.value.getWeight(), other.getValue().getWeight());
	}

	public static Card fromString(String cardCode)
	{
		String[] parts = cardCode.split("-");
		if(parts.length == 2)
		{
			CardSymbol symbol = CardSymbol.get(parts[0]);
			CardValue value = CardValue.get(parts[1]);
			return new Card(symbol, value);
		}
		else
		{
			return Card.EMPTY;
		}
	}
}
