package de.lemonpie.beddocontrol.model.listener;

import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.model.PlayerState;
import de.lemonpie.beddocontrol.model.card.Card;

public interface PlayerListener
{
	void nameDidChange(Player player, String name);

	void twitchNameDidChange(Player player, String twitchName);

	void stateDidChange(Player player, PlayerState state);

	/**
	 * Update player card.
	 *
	 * @param index index of card (0 based)
	 * @param card  new card
	 */
	void cardDidChangeAtIndex(Player player, int index, Card card);

	void chipsDidChange(Player player, int chips);

	void readerIdDidChange(Player player, int readerId);

	void winProbabilityDidChange(Player player, int value);
}
