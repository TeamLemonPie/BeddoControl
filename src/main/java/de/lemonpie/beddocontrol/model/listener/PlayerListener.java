package de.lemonpie.beddocontrol.model.listener;

import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.model.card.Card;

public interface PlayerListener {
    void nameDidChange(Player player, String name);

    void twitchNameDidChange(Player player, String twitchName);

    /**
     * Update player card.
     *
     * @param index index of card (0 based)
     * @param card  new card
     */
    void cardDidChangeAtIndex(Player player, int index, Card card);

    void chipsDidChange(Player player, int chips);
}
