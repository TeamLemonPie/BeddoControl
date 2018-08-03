package de.lemonpie.beddocontrol.ui.cells;

import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.ui.Controller;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import tools.NumberTextFormatter;

public class TableCellReaderID extends TableCell<Player, Integer>
{
	private Controller controller;

	public TableCellReaderID(Controller controller)
	{
		this.controller = controller;
	}

	@Override
	public void updateItem(Integer item, boolean empty)
	{
		if(!empty && item != null)
		{
			TextField textFieldReader = new TextField();
			textFieldReader.textProperty().addListener((a, b, c) -> textFieldReader.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2"));
			textFieldReader.setTextFormatter(new NumberTextFormatter());

			Object currentItem = getTableRow().getItem();

			if(currentItem == null)
			{
				setGraphic(null);
				return;
			}

			Player currentPlayer = (Player) currentItem;
			textFieldReader.setText(String.valueOf(currentPlayer.getReaderId()));
			textFieldReader.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");

			textFieldReader.setOnKeyPressed(ke -> {
				if(ke.getCode().equals(KeyCode.ENTER))
				{
					if(textFieldReader.getText().trim().equals(""))
					{
						currentPlayer.setReaderId(-3);
						return;
					}

					if(controller.setReaderIDForPlayer(currentPlayer, Integer.parseInt(textFieldReader.getText().trim())))
					{
						textFieldReader.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");
					}
					else
					{
						textFieldReader.setText(String.valueOf(currentPlayer.getReaderId()));
						textFieldReader.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
					}
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