package de.lemonpie.beddocontrol.ui;

import java.util.ResourceBundle;

import de.lemonpie.beddocontrol.model.card.Card;
import de.lemonpie.beddocontrol.model.card.CardSymbol;
import de.lemonpie.beddocontrol.model.card.CardValue;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class BoardCardController
{
	@FXML private AnchorPane mainPane;
	@FXML private HBox hboxHeart;
	@FXML private HBox hboxDiamonds;
	@FXML private HBox hboxSpades;
	@FXML private HBox hboxCross;

	private Stage stage;
	private Image icon;
	private ResourceBundle bundle;
	private Controller controller;
	private int boardCardIndex;

	public void init(Stage stage, Image icon, ResourceBundle bundle, Controller controller, int boardCardIndex)
	{
		this.stage = stage;
		this.icon = icon;
		this.bundle = bundle;
		this.controller = controller;
		this.boardCardIndex = boardCardIndex;
		
		mainPane.setStyle("-fx-background-color: #212121");
		
		prefill();
	}
	
	private void prefill()
	{
		hboxHeart.getChildren().clear();
		hboxDiamonds.getChildren().clear();
		hboxSpades.getChildren().clear();
		hboxCross.getChildren().clear();
		
		for(CardValue currentValue : CardValue.values())
		{
			if(currentValue != CardValue.BACK)
			{
				addImageViewForCard(new Card(CardSymbol.HEART, currentValue), hboxHeart);
				addImageViewForCard(new Card(CardSymbol.DIAMONDS, currentValue), hboxDiamonds);
				addImageViewForCard(new Card(CardSymbol.SPADES, currentValue), hboxSpades);
				addImageViewForCard(new Card(CardSymbol.CROSS, currentValue), hboxCross);
			}
		}
	}
	
	private void addImageViewForCard(Card card, HBox parent)
	{
		Image image = controller.getImageForCard(card);
		ImageView imageView = new ImageView(image);
		imageView.setFitHeight(50);
		imageView.setFitWidth(40);
		imageView.setOnMouseClicked((e)->
		{
			controller.overridBoardCard(boardCardIndex, card);
			stage.close();
		});
		parent.getChildren().add(imageView);
	}
}