package de.lemonpie.beddocontrol.network.listener;

import de.lemonpie.beddocommon.network.client.ControlSocket;
import de.lemonpie.beddocontrol.listener.PlayerListener;
import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.model.PlayerState;
import de.lemonpie.beddocontrol.model.card.Card;
import de.lemonpie.beddocontrol.network.command.send.player.*;

import java.net.SocketException;

public class PlayerListenerImpl implements PlayerListener
{

	private ControlSocket socket;

	public PlayerListenerImpl(ControlSocket socket)
	{
		this.socket = socket;
	}

	@Override
	public void nameDidChange(Player player, String name)
	{
		PlayerNameSendCommand cmd = new PlayerNameSendCommand(PlayerNameSendCommand.NameType.NAME, player.getId(), name);
		try
		{
			socket.write(cmd);
		}
		catch(SocketException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void stateDidChange(Player player, PlayerState state)
	{
		PlayerStateSendCommand cmd = new PlayerStateSendCommand(player.getId(), state);
		try
		{
			socket.write(cmd);
		}
		catch(SocketException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void twitchNameDidChange(Player player, String twitchName)
	{
		PlayerNameSendCommand cmd = new PlayerNameSendCommand(PlayerNameSendCommand.NameType.TWITCH, player.getId(), twitchName);
		try
		{
			socket.write(cmd);
		}
		catch(SocketException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void cardDidChangeAtIndex(Player player, int index, Card card)
	{
	}

	@Override
	public void chipsDidChange(Player player, int chips)
	{
		PlayerChipsSendCommand cmd = new PlayerChipsSendCommand(player.getId(), chips);
		try
		{
			socket.write(cmd);
		}
		catch(SocketException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void winProbabilityDidChange(Player player, int value)
	{
	}

	@Override
	public void isHighlightedDidChange(Player player, boolean value)
	{
		PlayerHighlightSendCommand cmd = new PlayerHighlightSendCommand(player.getId(), value);
		try
		{
			socket.write(cmd);
		}
		catch(SocketException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void manageCardIdDidChange(Player player, int value)
	{
		PlayerManageCardSendCommand cmd = new PlayerManageCardSendCommand(player.getId(), value);
		try
		{
			socket.write(cmd);
		}
		catch(SocketException e)
		{
			e.printStackTrace();
		}
	}
}
