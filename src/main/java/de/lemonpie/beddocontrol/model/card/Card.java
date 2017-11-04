package de.lemonpie.beddocontrol.model.card;


public class Card implements Comparable<Object> {
	private CardSymbol symbol;
	private CardValue value;

	public static final Card EMPTY = new Card();

	public Card(CardSymbol symbol, CardValue value) {
		this.symbol = symbol;
		this.value = value;
	}

	private Card() {
		this.symbol = CardSymbol.BACK;
		this.value = CardValue.BACK;
	}

	public CardSymbol getSymbol() {
		return symbol;
	}

	public CardValue getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Card other = (Card) obj;
		if (symbol != other.symbol)
			return false;
		if (value != other.value)
			return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
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
	public String toString() {
		return "Card [symbol=" + symbol + ", value=" + value + "]";
	}

	@Override
	public int compareTo(Object obj) {
		Card other = (Card) obj;
		return Integer.compare(this.value.getWeight(), other.getValue().getWeight());
	}

	public static Card fromString(String cardCode) {
		String[] parts = cardCode.split("-");
		if (parts.length == 2) {
			CardSymbol symbol = CardSymbol.get(parts[0]);
			CardValue value = CardValue.get(parts[1]);
			return new Card(symbol, value);
		} else {
			return Card.EMPTY;
		}
	}
}
