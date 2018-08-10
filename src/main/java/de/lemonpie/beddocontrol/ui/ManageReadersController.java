package de.lemonpie.beddocontrol.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.ResourceBundle;

@SuppressWarnings("unused")
public class ManageReadersController
{
	@FXML
	private AnchorPane mainPane;

	@FXML
	private VBox hboxSeats;

	@FXML
	private VBox hboxBoard;

	private Window stage;
	private Image icon;
	private ResourceBundle bundle;
	private Controller controller;

	public void init(Window stage, Image icon, ResourceBundle bundle, Controller controller)
	{
		this.stage = stage;
		this.icon = icon;
		this.bundle = bundle;
		this.controller = controller;

		initSeats();
		initBoard();
	}

	private void initSeats()
	{
		for(int i = 1; i <= 7; i++)
		{
			hboxSeats.getChildren().add(getReaderHBox(i, i));
		}
	}

	private void initBoard()
	{
		for(int i = 1; i <= 5; i++)
		{
			hboxBoard.getChildren().add(getReaderHBox(i, i));
		}
	}

	private HBox getReaderHBox(int ID, int value)
	{
		Label labelSeatNumber = new Label(String.valueOf(ID));
		labelSeatNumber.setStyle("-fx-font-size: 16; -fx-font-weight: bold");
		labelSeatNumber.setPrefWidth(40);

		TextField textFieldReaderID = new TextField(String.valueOf(value));
		textFieldReaderID.setPrefWidth(40);

		HBox hbox = new HBox();
		hbox.getChildren().addAll(labelSeatNumber, textFieldReaderID);
		return hbox;
	}
}