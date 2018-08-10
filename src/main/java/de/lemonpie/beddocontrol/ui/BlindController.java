package de.lemonpie.beddocontrol.ui;

import de.lemonpie.beddocontrol.model.Board;
import de.lemonpie.beddocontrol.network.ControlSocket;
import de.lemonpie.beddocontrol.network.command.send.AnteSendCommand;
import de.lemonpie.beddocontrol.network.command.send.BigBlindSendCommand;
import de.lemonpie.beddocontrol.network.command.send.SmallBlindSendCommand;
import de.tobias.utils.nui.NVC;
import fontAwesome.FontIcon;
import fontAwesome.FontIconType;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import logger.Logger;
import tools.AlertGenerator;
import tools.NumberTextFormatter;

import java.net.SocketException;

public class BlindController extends NVC
{
	@FXML
	private TextField textFieldSmallBlind;
	@FXML
	private TextField textFieldBigBlind;
	@FXML
	private TextField textFieldAnte;
	@FXML
	private Button buttonSmallBlind;
	@FXML
	private Button buttonBigBlind;
	@FXML
	private Button buttonAnte;

	private ControlSocket socket;
	private Board board;

	public BlindController(ControlSocket socket, Board board)
	{
		this.socket = socket;
		this.board = board;

		load("de/lemonpie/beddocontrol/ui", "BlindGUI");
	}

	public void init()
	{
		textFieldSmallBlind.setTextFormatter(new NumberTextFormatter());
		textFieldSmallBlind.setOnKeyPressed(ke -> {
			if(ke.getCode().equals(KeyCode.ENTER))
			{
				setSmallBlind();
			}
		});
		textFieldBigBlind.setTextFormatter(new NumberTextFormatter());
		textFieldBigBlind.setOnKeyPressed(ke -> {
			if(ke.getCode().equals(KeyCode.ENTER))
			{
				setBigBlind();
			}
		});
		textFieldAnte.setTextFormatter(new NumberTextFormatter());
		textFieldAnte.setOnKeyPressed(ke -> {
			if(ke.getCode().equals(KeyCode.ENTER))
			{
				setAnte();
			}
		});

		buttonSmallBlind.setGraphic(new FontIcon(FontIconType.MAIL_FORWARD, 14, Color.BLACK));
		buttonBigBlind.setGraphic(new FontIcon(FontIconType.MAIL_FORWARD, 14, Color.BLACK));
		buttonAnte.setGraphic(new FontIcon(FontIconType.MAIL_FORWARD, 14, Color.BLACK));
	}

	@FXML
	public void setSmallBlind()
	{
		String smallBlindText = textFieldSmallBlind.getText().trim();
		if(smallBlindText == null || smallBlindText.equals(""))
		{
			AlertGenerator.showAlert(Alert.AlertType.WARNING, "Warning", "", "Please enter a small blind value", ImageHandler.getIcon(), getContainingWindow(), null, false);
			textFieldSmallBlind.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
			return;
		}

		final int smallBlind = Integer.parseInt(smallBlindText);
		try
		{
			board.setSmallBlind(smallBlind);
			socket.write(new SmallBlindSendCommand(smallBlind));
		}
		catch(SocketException e)
		{
			Logger.error(e);
			textFieldSmallBlind.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
			AlertGenerator.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred", e.getMessage(), ImageHandler.getIcon(), getContainingWindow(), null, false);
		}

		textFieldSmallBlind.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");
	}

	@FXML
	public void setBigBlind()
	{
		String bigBlindText = textFieldBigBlind.getText().trim();
		if(bigBlindText == null || bigBlindText.equals(""))
		{
			AlertGenerator.showAlert(Alert.AlertType.WARNING, "Warning", "", "Please enter a big blind value", ImageHandler.getIcon(), getContainingWindow(), null, false);
			textFieldBigBlind.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
			return;
		}

		final int bigBlind = Integer.parseInt(bigBlindText);
		try
		{
			board.setBigBlind(bigBlind);
			socket.write(new BigBlindSendCommand(bigBlind));
		}
		catch(SocketException e)
		{
			Logger.error(e);
			textFieldBigBlind.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
			AlertGenerator.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred", e.getMessage(), ImageHandler.getIcon(), getContainingWindow(), null, false);
		}

		textFieldBigBlind.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");
	}

	@FXML
	public void setAnte()
	{
		String anteText = textFieldAnte.getText().trim();
		if(anteText == null || anteText.equals(""))
		{
			AlertGenerator.showAlert(Alert.AlertType.WARNING, "Warning", "", "Please enter an ante value", ImageHandler.getIcon(), getContainingWindow(), null, false);
			textFieldAnte.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
			return;
		}

		final int ante = Integer.parseInt(anteText);
		try
		{
			board.setAnte(ante);
			socket.write(new AnteSendCommand(ante));
		}
		catch(SocketException e)
		{
			Logger.error(e);
			textFieldAnte.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
			AlertGenerator.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred", e.getMessage(), ImageHandler.getIcon(), getContainingWindow(), null, false);
		}

		textFieldAnte.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");
	}
}
