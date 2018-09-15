package de.lemonpie.beddocontrol.ui;

import de.lemonpie.beddocommon.model.card.Card;
import de.lemonpie.beddocontrol.listener.PlayerListListener;
import de.lemonpie.beddocontrol.listener.PlayerListener;
import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.model.PlayerState;
import de.lemonpie.beddocontrol.network.command.send.player.PlayerOpSendCommand;
import de.lemonpie.beddocontrol.network.listener.PlayerListenerImpl;
import de.tobias.logger.Logger;
import javafx.scene.control.Alert.AlertType;
import tools.AlertGenerator;

import java.net.SocketException;

public class ControllerListenerImpl implements PlayerListener, PlayerListListener
{
	private Controller controller;

	public ControllerListenerImpl(Controller controller)
	{
		this.controller = controller;
	}

	// PlayerList Listener
	@Override
	public void addPlayerToList(Player player)
	{
		player.addListener(new PlayerListenerImpl(controller.socket));
		player.addListener(this);
	}

	@Override
	public void removePlayerFromList(Player player)
	{
		try
		{
			controller.socket.write(new PlayerOpSendCommand(player.getId()));
			controller.refreshTableView();
		}
		catch(SocketException e1)
		{
			Logger.error(e1);
			AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e1.getMessage(), ImageHandler.getIcon(), controller.getContainingWindow(), null, false);
		}
	}

	@Override
	public void nameDidChange(Player player, String name)
	{
		controller.getTableView().refresh();
	}

	@Override
	public void twitchNameDidChange(Player player, String twitchName)
	{
		controller.getTableView().refresh();
	}

	@Override
	public void cardDidChangeAtIndex(Player player, int index, Card card)
	{
		controller.getTableView().refresh();
	}

	@Override
	public void chipsDidChange(Player player, int chips)
	{
		controller.getTableView().refresh();
	}

	@Override
	public void stateDidChange(Player player, PlayerState state)
	{
		controller.getTableView().refresh();
	}

	@Override
	public void winProbabilityDidChange(Player player, int value)
	{
		controller.getTableView().refresh();
	}

	@Override
	public void isHighlightedDidChange(Player player, boolean value)
	{
		controller.getTableView().refresh();
	}

	@Override
	public void manageCardIdDidChange(Player player, int value)
	{
	}
}
