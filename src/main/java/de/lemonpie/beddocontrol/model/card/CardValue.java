package de.lemonpie.beddocontrol.model.card;

public enum CardValue{

	BACK("B", 0),
	TWO("2", 2),
	THREE("3", 3),
	FOUR("4", 4),
	FIVE("5", 5),
	SIX("6", 6),
	SEVEN("7", 7),
	EIGHT("8", 8),
	NINE("9", 9),
	TEN("10", 10),
	JACK("B", 11),
	QUEEN("D", 12),
	KING("K", 13),
	ACE("A", 14);

	private String name;
	private int weight;

	CardValue(String name, int weight) {
		this.name = name;
		this.weight = weight;
	}

	public int getWeight() {
		return weight;
	}

	public static CardValue fromWeight(int weight) {
		for (CardValue currentValue : values()) {
			if (currentValue.weight == weight) {
				return currentValue;
			}
		}
		return null;
	}

	public static CardValue get(String name) {
		for (CardValue currentValue : values()) {
			if (currentValue.name.equals(name)) {
				return currentValue;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return name;
	}
}
