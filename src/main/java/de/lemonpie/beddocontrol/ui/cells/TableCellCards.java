package de.lemonpie.beddocontrol.ui.cells;

import de.lemonpie.beddocommon.model.seat.Seat;
import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.network.command.send.ClearSendCommand;
import de.lemonpie.beddocontrol.ui.Controller;
import de.lemonpie.beddocontrol.ui.ImageHandler;
import de.tobias.logger.Logger;
import fontAwesome.FontIcon;
import fontAwesome.FontIconType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import tools.AlertGenerator;

import java.net.SocketException;
import java.util.Optional;

public class TableCellCards extends TableCell<Player, Integer>
{
	private TableColumn<Player, Integer> columnCards;
	private Controller controller;

	public TableCellCards(TableColumn<Player, Integer> columnCards, Controller controller)
	{
		this.columnCards = columnCards;
		this.controller = controller;
	}

	@Override
	public void updateItem(Integer item, boolean empty)
	{
		if(!empty && item != null)
		{
			Optional<Player> playerOptional = controller.getPlayer(item);
			if(playerOptional.isPresent())
			{
				Player currentPlayer = playerOptional.get();

				HBox hboxCards = new HBox();
				hboxCards.setAlignment(Pos.CENTER);
				hboxCards.setSpacing(15);

				Image imageCardLeft = ImageHandler.getImageForCard(currentPlayer.getCardLeft());
				ImageView imageViewCardLeft = new ImageView(imageCardLeft);
				imageViewCardLeft.setFitHeight(50);
				imageViewCardLeft.fitWidthProperty().bind(columnCards.widthProperty().divide(4));
				hboxCards.getChildren().add(imageViewCardLeft);

				Image imageCardRight = ImageHandler.getImageForCard(currentPlayer.getCardRight());
				ImageView imageViewCardRight = new ImageView(imageCardRight);
				imageViewCardRight.setFitHeight(50);
				imageViewCardRight.fitWidthProperty().bind(columnCards.widthProperty().divide(4));
				hboxCards.getChildren().add(imageViewCardRight);

				Button buttonClear = new Button();
				buttonClear.setGraphic(new FontIcon(FontIconType.TIMES, 16, Color.BLACK));
				buttonClear.setStyle("-fx-background-color: #CCCCCC;");
				buttonClear.setTooltip(new Tooltip("Clear Cards"));
				buttonClear.setOnAction((e) -> {
					try
					{
						Optional<Seat> seat = controller.getSeats().getSeatByPlayerId(currentPlayer.getId());
						if(seat.isPresent())
						{
							controller.getSocket().write(new ClearSendCommand(seat.get().getId()));
						}
					}
					catch(SocketException e1)
					{
						Logger.error(e1);
						AlertGenerator.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred", e1.getMessage(), ImageHandler.getIcon(), controller.getContainingWindow(), null, false);
					}
				});
				hboxCards.getChildren().add(buttonClear);
				HBox.setMargin(buttonClear, new Insets(0, 0, 0, 10));

				setGraphic(hboxCards);
			}
			else
			{
				setGraphic(null);
			}
		}
		else
		{
			setGraphic(null);
		}
	}
}