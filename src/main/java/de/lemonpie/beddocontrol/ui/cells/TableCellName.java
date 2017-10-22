package de.lemonpie.beddocontrol.ui.cells;

import de.lemonpie.beddocontrol.model.Player;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class TableCellName extends TableCell<Player, String>
{
	@Override
	public void updateItem(String item, boolean empty)
	{
		if(!empty && item != null)
		{
			TextField textFieldName = new TextField();
			textFieldName.textProperty().addListener((a, b, c) -> {
				textFieldName.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
			});

			Object currentItem = getTableRow().getItem();

			if(currentItem == null)
			{
				setGraphic(null);
				return;
			}

			Player currentPlayer = (Player)currentItem;
			textFieldName.setText(currentPlayer.getName());
			textFieldName.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");

			textFieldName.setOnKeyPressed(ke -> {
				if(ke.getCode().equals(KeyCode.ENTER))
				{
					textFieldName.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");
					currentPlayer.setName(textFieldName.getText().trim());
				}
			});
			setGraphic(textFieldName);
		}
		else
		{
			setGraphic(null);
		}
	}
}