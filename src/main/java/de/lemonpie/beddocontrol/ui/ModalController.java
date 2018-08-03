package de.lemonpie.beddocontrol.ui;

import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ModalController
{
	@FXML
	private Label labelMessage;

	public void init(Stage stage, StringProperty message)
	{
		labelMessage.textProperty().bind(message);
		stage.setOnCloseRequest(Event::consume);
	}
}