package de.lemonpie.beddocontrol.ui.cells;

import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.ui.Controller;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;

public class TableCellManageCardID extends TableCell<Player, Integer>
{
	private Controller controller;

	public TableCellManageCardID(Controller controller)
	{
		this.controller = controller;
	}

	@Override
	public void updateItem(Integer item, boolean empty)
	{
		if(!empty && item != null)
		{
			TextField textFieldManageCardID = new TextField();
			textFieldManageCardID.textProperty().addListener((a, b, c) -> textFieldManageCardID.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2"));

			Object currentItem = getTableRow().getItem();

			if(currentItem == null)
			{
				setGraphic(null);
				return;
			}

			Player currentPlayer = (Player) currentItem;
			if(currentPlayer.getManageCardId() > -1)
			{
				textFieldManageCardID.setText(String.valueOf(currentPlayer.getManageCardId()));
				textFieldManageCardID.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");
			}
			else
			{
				textFieldManageCardID.setText("");
				textFieldManageCardID.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
			}

			textFieldManageCardID.setOnKeyPressed(ke -> {
				if(textFieldManageCardID.getText().trim().equals(""))
				{
					currentPlayer.setManageCardId(-1);
					return;
				}

				if(controller.setManageCardIdForPlayer(item, Integer.parseInt(textFieldManageCardID.getText().trim())))
				{
					textFieldManageCardID.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");
				}
				else
				{
					textFieldManageCardID.setText(String.valueOf(currentPlayer.getManageCardId()));
					textFieldManageCardID.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
				}
			});
			setGraphic(textFieldManageCardID);
		}
		else
		{
			setGraphic(null);
		}
	}
}