package de.lemonpie.beddocontrol.model.listener;

import de.lemonpie.beddocontrol.model.card.Card;

public interface PlayerListener {
    void nameDidChange(String name);

    void twitchNameDidChange(String twitchName);

    /**
     * Update player card.
     *
     * @param index index of card (0 based)
     * @param card  new card
     */
    void cardDidChangeAtIndex(int index, Card card);

    void chipsDidChange(int chips);
}
