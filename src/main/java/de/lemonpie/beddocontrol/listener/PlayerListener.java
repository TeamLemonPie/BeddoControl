package de.lemonpie.beddocontrol.listener;

import de.lemonpie.beddocommon.model.card.Card;
import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.model.PlayerState;

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

	void winProbabilityDidChange(Player player, int value);

	void isHighlightedDidChange(Player player, boolean value);

	void manageCardIdDidChange(Player player, int value);
}
