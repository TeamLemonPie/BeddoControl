package de.lemonpie.beddocontrol.ui;

import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.model.card.Card;
import de.lemonpie.beddocontrol.model.card.CardSymbol;
import de.lemonpie.beddocontrol.model.card.CardValue;
import de.lemonpie.beddocontrol.network.command.send.BoardCardSetSendCommand;
import de.tobias.logger.Logger;
import de.tobias.utils.nui.NVC;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import tools.AlertGenerator;

import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

public class BoardCardController extends NVC
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

	private Controller controller;
	private int boardCardIndex;

	public BoardCardController(Window owner, Controller controller, int boardCardIndex)
	{
		this.controller = controller;
		this.boardCardIndex = boardCardIndex;
		load("de/lemonpie/beddocontrol/ui", "BoardCardGUI");
		applyViewControllerToStage().initOwner(owner).initModality(Modality.WINDOW_MODAL);
	}

	@Override
	public void init()
	{
		mainPane.setStyle("-fx-background-color: #212121");

		prefill();
	}

	@Override
	public void initStage(Stage stage)
	{
		stage.setWidth(650);
		stage.setHeight(290);
		stage.getIcons().add(ImageHandler.getIcon());
		stage.setTitle("Override Board Card");
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
				overrideBoardCard(boardCardIndex, card);
				closeStage();
			});
		}
		parent.getChildren().add(imageView);
	}

	private void overrideBoardCard(int index, Card card)
	{
		try
		{
			controller.getSocket().write(new BoardCardSetSendCommand(index, card));
		}
		catch(SocketException e)
		{
			Logger.error(e);
			AlertGenerator.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred", e.getMessage(), ImageHandler.getIcon(), getContainingWindow(), null, false);
		}
	}
}