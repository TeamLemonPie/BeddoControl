package de.lemonpie.beddocontrol.midi.listener;

import de.lemonpie.beddocontrol.listener.PlayerListener;
import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.model.PlayerState;
import de.lemonpie.beddocontrol.model.card.Card;

public class MidiPlayerListener implements PlayerListener
{
	@Override
	public void nameDidChange(Player player, String name)
	{

	}

	@Override
	public void twitchNameDidChange(Player player, String twitchName)
	{

	}

	@Override
	public void stateDidChange(Player player, PlayerState state)
	{

	}

	@Override
	public void cardDidChangeAtIndex(Player player, int index, Card card)
	{

	}

	@Override
	public void chipsDidChange(Player player, int chips)
	{

	}

	@Override
	public void winProbabilityDidChange(Player player, int value)
	{

	}

	@Override
	public void isHighlightedDidChange(Player player, boolean value)
	{

	}

	@Override
	public void manageCardIdDidChange(Player player, int value)
	{

	}
}
