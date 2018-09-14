package de.lemonpie.beddocontrol.ui.cells;

import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.ui.Controller;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;

import java.util.Optional;

public class TableCellStatus extends TableCell<Player, Integer>
{
	private Controller controller;

	public TableCellStatus(Controller controller)
	{
		this.controller = controller;
	}

	@Override
	public void updateItem(Integer item, boolean empty)
	{
		if(!empty)
		{
			Optional<Player> playerOptional = controller.getPlayer(item);
			if(playerOptional.isPresent())
			{
				Player currentPlayer = playerOptional.get();

				Label labelStatus = new Label(currentPlayer.getState().getName());
				labelStatus.setPadding(new Insets(5, 10, 5, 10));

				if(currentPlayer.isHighlighted())
				{
					labelStatus.setStyle("-fx-background-color: #339AF0; -fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: center;");
					setGraphic(labelStatus);
					return;
				}

				switch(currentPlayer.getState())
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
		}
		else
		{
			setGraphic(null);
		}
	}
}