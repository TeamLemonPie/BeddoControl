package de.lemonpie.beddocontrol.ui;

import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.model.card.Card;
import de.lemonpie.beddocontrol.model.card.CardSymbol;
import de.lemonpie.beddocontrol.model.card.CardValue;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

@SuppressWarnings("unused")
public class BoardCardController
{
	@FXML
	private AnchorPane mainPane;
	@FXML
	private HBox hboxHeart;
	@FXML
	private HBox hboxDiamonds;
	@FXML
	private HBox hboxSpades;
	@FXML
	private HBox hboxCross;

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

	private Set<Card> getAlreadyUsedCards()
	{
		Set<Card> cards = new HashSet<>();
		for(Player currentPlayer : controller.getPlayerList())
		{
			if(currentPlayer.getCardLeft() != Card.EMPTY)
			{
				cards.add(currentPlayer.getCardLeft());
			}

			if(currentPlayer.getCardRight() != Card.EMPTY)
			{
				cards.add(currentPlayer.getCardRight());
			}
		}

		for(Card currentCard : controller.getBoard().getCards())
		{
			if(currentCard != Card.EMPTY)
			{
				cards.add(currentCard);
			}
		}

		return cards;
	}

	private void prefill()
	{
		hboxHeart.getChildren().clear();
		hboxDiamonds.getChildren().clear();
		hboxSpades.getChildren().clear();
		hboxCross.getChildren().clear();

		Set<Card> alreadyUsedCards = getAlreadyUsedCards();

		for(CardValue currentValue : CardValue.values())
		{
			if(currentValue != CardValue.BACK)
			{
				addImageViewForCard(alreadyUsedCards, new Card(CardSymbol.HEART, currentValue), hboxHeart);
				addImageViewForCard(alreadyUsedCards, new Card(CardSymbol.DIAMONDS, currentValue), hboxDiamonds);
				addImageViewForCard(alreadyUsedCards, new Card(CardSymbol.SPADES, currentValue), hboxSpades);
				addImageViewForCard(alreadyUsedCards, new Card(CardSymbol.CROSS, currentValue), hboxCross);
			}
		}
	}

	private void addImageViewForCard(Set<Card> alreadyUsedCards, Card card, HBox parent)
	{
		Image image = ImageHandler.getImageForCard(card);
		ImageView imageView = new ImageView(image);
		imageView.setFitHeight(50);
		imageView.setFitWidth(40);

		if(alreadyUsedCards.contains(card))
		{
			imageView.setOpacity(0.5);
		}
		else
		{
			imageView.setOnMouseClicked((e) ->
			{
				controller.overrideBoardCard(boardCardIndex, card);
				stage.close();
			});
		}
		parent.getChildren().add(imageView);
	}
}