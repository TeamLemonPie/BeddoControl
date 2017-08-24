package de.lemonpie.beddocontrol.model.card;

public abstract class Card {

    public abstract String name();


    public static Card fromString(String cardCode) {
        String[] parts = cardCode.split("-");
        if (parts.length == 2) {
            Color color = Color.get(parts[0]);
            CardType cardType = CardType.get(parts[1]);
            return new NamedCard(color, cardType);
        } else {
            return new BlankCard();
        }
    }
}
