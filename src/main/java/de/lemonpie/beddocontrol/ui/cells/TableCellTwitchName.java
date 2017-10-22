package de.lemonpie.beddocontrol.ui.cells;

import de.lemonpie.beddocontrol.model.Player;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class TableCellTwitchName extends TableCell<Player, String>
{
	@Override
	public void updateItem(String item, boolean empty)
	{
		if(!empty && item != null)
		{
			TextField textFieldTwitchName = new TextField();
			textFieldTwitchName.textProperty().addListener((a, b, c) -> {
				textFieldTwitchName.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
			});

			Object currentItem = getTableRow().getItem();

			if(currentItem == null)
			{
				setGraphic(null);
				return;
			}

			Player currentPlayer = (Player)currentItem;
			textFieldTwitchName.setText(currentPlayer.getTwitchName());
			textFieldTwitchName.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");

			textFieldTwitchName.setOnKeyPressed(ke -> {
				if(ke.getCode().equals(KeyCode.ENTER))
				{
					textFieldTwitchName.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");
					currentPlayer.setTwitchName(textFieldTwitchName.getText().trim());
				}
			});
			setGraphic(textFieldTwitchName);
		}
		else
		{
			setGraphic(null);
		}
	}
}