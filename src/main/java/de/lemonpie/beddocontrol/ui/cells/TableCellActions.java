package de.lemonpie.beddocontrol.ui.cells;

import java.util.Optional;

import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.model.PlayerState;
import de.lemonpie.beddocontrol.ui.Controller;
import fontAwesome.FontIconType;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;

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
			buttonActivate.setGraphic(controller.getFontIcon(FontIconType.PLAY, 16, Color.BLACK));
			buttonActivate.setStyle("-fx-background-color: #48DB5E;");
			buttonActivate.setTooltip(new Tooltip("Activate"));
			buttonActivate.setOnAction((e) -> {
				((Player)getTableRow().getItem()).setPlayerState(PlayerState.ACTIVE);
				controller.getTableView().refresh();
			});
			hboxButtons.getChildren().add(buttonActivate);

			Button buttonOutOfRound = new Button();
			buttonOutOfRound.setGraphic(controller.getFontIcon(FontIconType.PAUSE, 16, Color.BLACK));
			buttonOutOfRound.setStyle("-fx-background-color: orange;");
			buttonOutOfRound.setTooltip(new Tooltip("Fold"));
			buttonOutOfRound.setOnAction((e) -> {
				((Player)getTableRow().getItem()).setPlayerState(PlayerState.OUT_OF_ROUND);
				controller.getTableView().refresh();
			});
			hboxButtons.getChildren().add(buttonOutOfRound);

			Button buttonOutOfGame = new Button();
			buttonOutOfGame.setGraphic(controller.getFontIcon(FontIconType.POWER_OFF, 16, Color.BLACK));
			buttonOutOfGame.setStyle("-fx-background-color: #CC0000;");
			buttonOutOfGame.setTooltip(new Tooltip("Deactivate"));
			buttonOutOfGame.setOnAction((e) -> {
				((Player)getTableRow().getItem()).setPlayerState(PlayerState.OUT_OF_GAME);
				controller.getTableView().refresh();
			});
			hboxButtons.getChildren().add(buttonOutOfGame);

			Button buttonDelete = new Button();
			buttonDelete.setGraphic(controller.getFontIcon(FontIconType.TRASH, 16, Color.BLACK));
			buttonDelete.setStyle("-fx-background-color: #CCCCCC;");
			buttonDelete.setTooltip(new Tooltip("Delete"));
			buttonDelete.setOnAction((e) -> {
				Player player = ((Player)getTableRow().getItem());
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Spieler " + player.getId() + " löschen");
				alert.setHeaderText("");
				alert.initOwner(controller.getStage());
				alert.initModality(Modality.APPLICATION_MODAL);
				alert.setContentText("Do you really want to delete player " + player.getId() + "?");

				Optional<ButtonType> result = alert.showAndWait();
				if(result.get() == ButtonType.OK)
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