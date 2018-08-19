package de.lemonpie.beddocontrol.ui;

import de.lemonpie.beddocontrol.listener.BoardListener;
import de.lemonpie.beddocontrol.listener.PlayerListListener;
import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.model.PlayerState;
import de.lemonpie.beddocontrol.model.card.Card;
import de.lemonpie.beddocontrol.model.listener.PlayerListener;
import de.lemonpie.beddocontrol.network.command.send.player.PlayerOpSendCommand;
import de.lemonpie.beddocontrol.network.listener.PlayerListenerImpl;
import de.tobias.logger.Logger;
import javafx.scene.control.Alert.AlertType;
import tools.AlertGenerator;

import java.net.SocketException;

public class ControllerListenerImpl implements BoardListener, PlayerListener, PlayerListListener
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
	public void cardDidChangeAtIndex(int index, Card card)
	{
		switch(index)
		{
			case 0:
				controller.imageViewBoard1.setImage(ImageHandler.getImageForCard(card));
				break;
			case 1:
				controller.imageViewBoard2.setImage(ImageHandler.getImageForCard(card));
				break;
			case 2:
				controller.imageViewBoard3.setImage(ImageHandler.getImageForCard(card));
				break;
			case 3:
				controller.imageViewBoard4.setImage(ImageHandler.getImageForCard(card));
				break;
			case 4:
				controller.imageViewBoard5.setImage(ImageHandler.getImageForCard(card));
				break;
			default:
				break;
		}
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
	public void boardReaderIdDidChange(int index, int readerId, int oldReaderId)
	{
		String style = readerId == -3 ? "-fx-border-color: #CC0000; -fx-border-width: 2" : "-fx-border-color: #48DB5E; -fx-border-width: 2";
		switch(index)
		{
			case 0:
				controller.textFieldBoard1.setText(String.valueOf(readerId));
				controller.textFieldBoard1.setStyle(style);
				break;
			case 1:
				controller.textFieldBoard2.setText(String.valueOf(readerId));
				controller.textFieldBoard2.setStyle(style);
				break;
			case 2:
				controller.textFieldBoard3.setText(String.valueOf(readerId));
				controller.textFieldBoard3.setStyle(style);
				break;
			case 3:
				controller.textFieldBoard4.setText(String.valueOf(readerId));
				controller.textFieldBoard4.setStyle(style);
				break;
			case 4:
				controller.textFieldBoard5.setText(String.valueOf(readerId));
				controller.textFieldBoard5.setStyle(style);
				break;
			default:
				break;
		}
	}

	@Override
	public void smallBlindDidChange(int newValue)
	{
	}

	@Override
	public void bigBlindDidChange(int newValue)
	{
	}

	@Override
	public void anteDidChange(int newValue)
	{
	}
}
