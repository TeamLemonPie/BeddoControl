package de.lemonpie.beddocontrol.listener;

import de.lemonpie.beddocontrol.model.Player;

public interface PlayerListListener {

    void addPlayer(Player player);

    void removePlayer(Player player);
}
