package de.lemonpie.beddocontrol.ui.cells;

import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.model.PlayerState;
import de.lemonpie.beddocontrol.ui.Controller;
import fontAwesome.FontIcon;
import fontAwesome.FontIconType;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;

import java.util.Optional;

public class TableCellActions extends TableCell<Player, PlayerState>
{
	private Controller controller;

	public TableCellActions(Controller controller)
	{
		this.controller = controller;
	}

	@Override
	public void updateItem(PlayerState item, boolean empty)
	{
		if(!empty)
		{
			HBox hboxButtons = new HBox();
			hboxButtons.setAlignment(Pos.CENTER);
			hboxButtons.setSpacing(10);

			Button buttonActivate = new Button();
			buttonActivate.setGraphic(new FontIcon(FontIconType.PLAY, 16, Color.BLACK));
			buttonActivate.setStyle("-fx-background-color: #48DB5E;");
			buttonActivate.setTooltip(new Tooltip("Activate"));
			buttonActivate.setOnAction((e) -> {
				((Player) getTableRow().getItem()).setState(PlayerState.ACTIVE);
				controller.getTableView().refresh();
			});
			hboxButtons.getChildren().add(buttonActivate);

			Button buttonOutOfRound = new Button();
			buttonOutOfRound.setGraphic(new FontIcon(FontIconType.PAUSE, 16, Color.BLACK));
			buttonOutOfRound.setStyle("-fx-background-color: orange;");
			buttonOutOfRound.setTooltip(new Tooltip("Fold"));
			buttonOutOfRound.setOnAction((e) -> {
				((Player) getTableRow().getItem()).setState(PlayerState.OUT_OF_ROUND);
				controller.getTableView().refresh();
			});
			hboxButtons.getChildren().add(buttonOutOfRound);

			Button buttonOutOfGame = new Button();
			buttonOutOfGame.setGraphic(new FontIcon(FontIconType.POWER_OFF, 16, Color.BLACK));
			buttonOutOfGame.setStyle("-fx-background-color: #CC0000;");
			buttonOutOfGame.setTooltip(new Tooltip("Deactivate"));
			buttonOutOfGame.setOnAction((e) -> {
				((Player) getTableRow().getItem()).setState(PlayerState.OUT_OF_GAME);
				controller.getTableView().refresh();
			});
			hboxButtons.getChildren().add(buttonOutOfGame);

			Button buttonHighlight = new Button();
			buttonHighlight.setGraphic(new FontIcon(FontIconType.CHECK, 16, Color.BLACK));
			buttonHighlight.setStyle("-fx-background-color: #339AF0;");
			buttonHighlight.setTooltip(new Tooltip("Highlight"));
			buttonHighlight.setOnAction((e) -> {
				Player currentPlayer = (Player) getTableRow().getItem();
				currentPlayer.setHighlighted(!currentPlayer.isHighlighted());
				controller.getTableView().refresh();
			});
			hboxButtons.getChildren().add(buttonHighlight);

			Button buttonDelete = new Button();
			buttonDelete.setGraphic(new FontIcon(FontIconType.TRASH, 16, Color.BLACK));
			buttonDelete.setStyle("-fx-background-color: #CCCCCC;");
			buttonDelete.setTooltip(new Tooltip("Delete"));
			buttonDelete.setOnAction((e) -> {
				Player player = ((Player) getTableRow().getItem());
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Delete player " + player.getId());
				alert.setHeaderText("");
				alert.initOwner(controller.getContainingWindow());
				alert.initModality(Modality.APPLICATION_MODAL);
				alert.setContentText("Do you really want to delete player " + player.getId() + "?");

				Optional<ButtonType> result = alert.showAndWait();
				if(result.isPresent() && result.get() == ButtonType.OK)
				{
					controller.getPlayerList().remove(player);
					controller.getTableView().refresh();
				}
			});
			hboxButtons.getChildren().add(buttonDelete);

			setGraphic(hboxButtons);
		}
		else
		{
			setGraphic(null);
		}
	}
}