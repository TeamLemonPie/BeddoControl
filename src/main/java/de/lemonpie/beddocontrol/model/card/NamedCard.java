package de.lemonpie.beddocontrol.model.card;

public class NamedCard extends Card {

    private final Color color;
    private final CardType type;

    public NamedCard(Color color, CardType type) {
        this.color = color;
        this.type = type;
    }

    public Color getColor() {
        return color;
    }

    public CardType getType() {
        return type;
    }

    @Override
    public String name() {
        return color.toString() + "-" + type.toString();
    }
}
