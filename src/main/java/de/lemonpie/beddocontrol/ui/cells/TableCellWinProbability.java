package de.lemonpie.beddocontrol.ui.cells;

import de.lemonpie.beddocontrol.model.Player;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;

public class TableCellWinProbability extends TableCell<Player, Integer>
{
	@Override
	public void updateItem(Integer item, boolean empty)
	{
		if(!empty)
		{
			Label labelProbability = new Label(item.toString() + "%");
			setGraphic(labelProbability);
		}
		else
		{
			setGraphic(null);
		}
	}
}