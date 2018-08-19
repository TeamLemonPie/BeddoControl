package de.lemonpie.beddocontrol.ui.cells;

import de.lemonpie.beddocommon.model.seat.Seat;
import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.ui.Controller;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import tools.NumberTextFormatter;

import java.util.Optional;

public class TableCellSeatID extends TableCell<Player, Integer>
{
	private Controller controller;

	public TableCellSeatID(Controller controller)
	{
		this.controller = controller;
	}

	@Override
	public void updateItem(Integer item, boolean empty)
	{
		if(!empty && item != null)
		{
			TextField textFieldSeat = new TextField();
			textFieldSeat.textProperty().addListener((a, b, c) -> textFieldSeat.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2"));
			textFieldSeat.setTextFormatter(new NumberTextFormatter());

			Object currentItem = getTableRow().getItem();

			if(currentItem == null)
			{
				setGraphic(null);
				return;
			}

			Optional<Seat> seat = controller.getSeats().getSeatByPlayerId(item);
			if(seat.isPresent())
			{
				textFieldSeat.setText(String.valueOf(seat.get().getId()));
				textFieldSeat.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");
			}
			else
			{
				textFieldSeat.setText("");
				textFieldSeat.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
			}

			textFieldSeat.setOnKeyPressed(ke -> {
				if(ke.getCode().equals(KeyCode.ENTER))
				{
					if(textFieldSeat.getText().trim().equals(""))
					{
						if(seat.isPresent())
						{
							seat.get().setPlayerId(-1);
						}

						return;
					}

					if(controller.setSeatIdForPlayer(item, Integer.parseInt(textFieldSeat.getText().trim())))
					{
						textFieldSeat.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");
					}
					else
					{
						Optional<Seat> oldSeat = controller.getSeats().getSeatByPlayerId(item);
						if(oldSeat.isPresent())
						{
							textFieldSeat.setText(String.valueOf(oldSeat.get().getId()));
						}
						textFieldSeat.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
					}
				}
			});
			setGraphic(textFieldSeat);
		}
		else
		{
			setGraphic(null);
		}
	}
}