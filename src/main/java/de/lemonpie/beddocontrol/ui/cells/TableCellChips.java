package de.lemonpie.beddocontrol.ui.cells;

import de.lemonpie.beddocontrol.model.Player;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;

public class TableCellChips extends TableCell<Player, Integer>
{
	@Override
	public void updateItem(Integer item, boolean empty)
	{
		if(!empty && item != null)
		{
			TextField textFieldChips = new TextField();
			textFieldChips.textProperty().addListener((a, b, c) -> {
				textFieldChips.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
			});
			textFieldChips.setTextFormatter(new TextFormatter<>(c -> {
				if(c.getControlNewText().isEmpty())
				{
					return c;
				}
				if(c.getControlNewText().matches("[0-9]*"))
				{
					return c;
				}
				else
				{
					return null;
				}
			}));

			Object currentItem = getTableRow().getItem();

			if(currentItem == null)
			{
				setGraphic(null);
				return;
			}

			Player currentPlayer = (Player)currentItem;
			textFieldChips.setText(String.valueOf(currentPlayer.getChips()));
			textFieldChips.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");

			textFieldChips.setOnKeyPressed(ke -> {
				if(ke.getCode().equals(KeyCode.ENTER))
				{
					textFieldChips.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");
					currentPlayer.setChips(Integer.parseInt(textFieldChips.getText().trim()));
				}
			});
			setGraphic(textFieldChips);
		}
		else
		{
			setGraphic(null);
		}
	}
}
