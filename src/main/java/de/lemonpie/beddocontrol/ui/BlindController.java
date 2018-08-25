package de.lemonpie.beddocontrol.ui;

import de.lemonpie.beddocommon.network.client.ControlSocket;
import de.lemonpie.beddocontrol.model.Board;
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

	private interface ValueHandler
	{
		void handle(int value) throws SocketException;
	}

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
		set(textFieldSmallBlind, "a small blind", value -> {
			board.setAnte(value);
			socket.write(new SmallBlindSendCommand(value));
		});
	}

	@FXML
	public void setBigBlind()
	{
		set(textFieldBigBlind, "a big blind", value -> {
			board.setAnte(value);
			socket.write(new BigBlindSendCommand(value));
		});
	}

	@FXML
	public void setAnte()
	{
		set(textFieldAnte, "an ante", value -> {
			board.setAnte(value);
			socket.write(new AnteSendCommand(value));
		});
	}

	private void set(TextField textField, String textPart, ValueHandler handler)
	{
		textField.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
		String valueText = textField.getText().trim();

		if(valueText.equals(""))
		{
			AlertGenerator.showAlert(Alert.AlertType.WARNING, "Warning", "", "Please enter " + textPart + " value", ImageHandler.getIcon(), getContainingWindow(), null, false);
			return;
		}

		final int value = Integer.parseInt(valueText);
		try
		{
			handler.handle(value);
			textField.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");
		}
		catch(SocketException e)
		{
			Logger.error(e);
			AlertGenerator.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred", e.getMessage(), ImageHandler.getIcon(), getContainingWindow(), null, false);
		}

	}
}
