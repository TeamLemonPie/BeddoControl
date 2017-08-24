package de.lemonpie.beddocontrol.model.card;

public enum Color {
    PIK("Pi"),
    HERZ("He"),
    KARO("Ka"),
    KREUZ("Kr");

    private String name;

    Color(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Color get(String value) {
        for (Color color : Color.values()) {
            if (color.name.equals(value)) {
                return color;
            }
        }
        return null;
    }
}
