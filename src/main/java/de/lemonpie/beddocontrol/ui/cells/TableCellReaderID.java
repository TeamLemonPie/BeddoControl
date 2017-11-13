package de.lemonpie.beddocontrol.ui.cells;

import de.lemonpie.beddocontrol.model.Player;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import tools.NumberTextFormatter;

public class TableCellReaderID extends TableCell<Player, Integer>
{
	@Override
	public void updateItem(Integer item, boolean empty)
	{
		if(!empty && item != null)
		{
			TextField textFieldReader = new TextField();
			textFieldReader.textProperty().addListener((a, b, c) -> {
				textFieldReader.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
			});
			textFieldReader.setTextFormatter(new NumberTextFormatter());

			Object currentItem = getTableRow().getItem();

			if(currentItem == null)
			{
				setGraphic(null);
				return;
			}

			Player currentPlayer = (Player)currentItem;
			textFieldReader.setText(String.valueOf(currentPlayer.getReaderId()));
			textFieldReader.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");

			textFieldReader.setOnKeyPressed(ke -> {
				if(ke.getCode().equals(KeyCode.ENTER))
				{
					textFieldReader.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");
					currentPlayer.setReaderId(Integer.parseInt(textFieldReader.getText().trim()));
				}
			});
			setGraphic(textFieldReader);
		}
		else
		{
			setGraphic(null);
		}
	}
}