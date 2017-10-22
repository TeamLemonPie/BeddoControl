package de.lemonpie.beddocontrol.ui.cells;

import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.model.PlayerState;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;

public class TableCellStatus extends TableCell<Player, PlayerState>
{
	@Override
	public void updateItem(PlayerState item, boolean empty)
	{
		if(!empty)
		{
			Label labelStatus = new Label(item.getName());
			labelStatus.setPadding(new Insets(5, 10, 5, 10));

			switch(item)
			{
				case ACTIVE:
					labelStatus.setStyle("-fx-background-color: #48DB5E; -fx-text-fill: black; -fx-font-weight: bold; -fx-alignment: center;");
					break;
				case OUT_OF_ROUND:
					labelStatus.setStyle("-fx-background-color: orange; -fx-text-fill: black; -fx-font-weight: bold; -fx-alignment: center");
					break;
				case OUT_OF_GAME:
					labelStatus.setStyle("-fx-background-color: #CC0000; -fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: center");
					break;
				default:
					break;
			}

			setGraphic(labelStatus);
		}
		else
		{
			setGraphic(null);
		}
	}
}