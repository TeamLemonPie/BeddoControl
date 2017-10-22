package de.lemonpie.beddocontrol.ui.cells;

import java.net.SocketException;
import java.util.Optional;

import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.network.command.send.ClearSendCommand;
import de.lemonpie.beddocontrol.ui.Controller;
import fontAwesome.FontIconType;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import logger.Logger;
import tools.AlertGenerator;

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
				hboxCards.setSpacing(10);

				Image imageCardLeft = controller.getImageForCard(currentPlayer.getCardLeft());
				ImageView imageViewCardLeft = new ImageView(imageCardLeft);
				imageViewCardLeft.setFitHeight(38);
				imageViewCardLeft.fitWidthProperty().bind(columnCards.widthProperty().divide(4));
				hboxCards.getChildren().add(imageViewCardLeft);

				Image imageCardRight = controller.getImageForCard(currentPlayer.getCardRight());
				ImageView imageViewCardRight = new ImageView(imageCardRight);
				imageViewCardRight.setFitHeight(38);
				imageViewCardRight.fitWidthProperty().bind(columnCards.widthProperty().divide(4));
				hboxCards.getChildren().add(imageViewCardRight);

				Button buttonClear = new Button();
				buttonClear.setGraphic(controller.getFontIcon(FontIconType.TIMES, 16, Color.BLACK));
				buttonClear.setStyle("-fx-background-color: #CCCCCC;");
				buttonClear.setTooltip(new Tooltip("Clear Cards"));
				buttonClear.setOnAction((e) -> {
					try
					{
						controller.getSocket().write(new ClearSendCommand(currentPlayer.getReaderId()));
					}
					catch(SocketException e1)
					{
						Logger.error(e1);
						AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e1.getMessage(), controller.getIcon(), controller.getStage(), null, false);
					}
				});
				hboxCards.getChildren().add(buttonClear);

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