package de.lemonpie.beddocontrol.ui.cells;

import de.lemonpie.beddocontrol.model.Player;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;

public class TableCellWinProbability extends TableCell<Player, Double>
{
	@Override
	public void updateItem(Double item, boolean empty)
	{
		if(!empty)
		{
			Label labelStatus = new Label(item.toString() + "%");
			setGraphic(labelStatus);
		}
		else
		{
			setGraphic(null);
		}
	}
}