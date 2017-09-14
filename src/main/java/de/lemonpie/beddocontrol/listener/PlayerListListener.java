package de.lemonpie.beddocontrol.listener;

import de.lemonpie.beddocontrol.model.Player;

public interface PlayerListListener {

    void addPlayerToList(Player player);

    void removePlayerFromList(Player player);
}
