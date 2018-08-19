package de.lemonpie.beddocontrol.ui;

import de.lemonpie.beddocommon.model.seat.Seat;
import de.lemonpie.beddocontrol.model.DataAccessible;
import de.tobias.utils.nui.NVC;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import tools.AlertGenerator;
import tools.NumberTextFormatter;


public class ManageReadersController extends NVC
{
	@FXML
	private AnchorPane mainPane;

	@FXML
	private VBox hboxSeats;

	@FXML
	private VBox hboxBoard;

	private DataAccessible dataAccessible;

	public ManageReadersController(DataAccessible dataAccessible, Window owner)
	{
		this.dataAccessible = dataAccessible;
		load("de/lemonpie/beddocontrol/ui", "ManageReaders");
		applyViewControllerToStage().initModality(Modality.WINDOW_MODAL).initOwner(owner);
	}

	@Override
	public void init()
	{
		initSeats();
		initBoard();
	}

	@Override
	public void initStage(Stage stage)
	{
		stage.setWidth(300);
		stage.setHeight(350);
		stage.setTitle("Manage Readers");
		stage.getIcons().add(ImageHandler.getIcon());
	}

	private void initSeats()
	{
		for(int i = 1; i <= 7; i++)
		{
			hboxSeats.getChildren().add(getReaderHBox(i, i));
		}
	}

	private void initBoard()
	{
		for(int i = 1; i <= 5; i++)
		{
			hboxBoard.getChildren().add(getReaderHBox(i, i));
		}
	}

	private HBox getReaderHBox(int ID, int value)
	{
		Label labelSeatNumber = new Label(String.valueOf(ID));
		labelSeatNumber.setStyle("-fx-font-size: 16; -fx-font-weight: bold");
		labelSeatNumber.setPrefWidth(40);

		TextField textFieldReaderID = new TextField(String.valueOf(value));
		textFieldReaderID.setPrefWidth(40);

		HBox hbox = new HBox();
		hbox.getChildren().addAll(labelSeatNumber, textFieldReaderID);
		return hbox;
	}


	private void initTextFieldBoard(TextField textField, int position)
	{
		textField.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");

		textField.setTextFormatter(new NumberTextFormatter());

		textField.setOnKeyPressed(ke -> {
			if(ke.getCode().equals(KeyCode.ENTER))
			{
				if(textField.getText().trim().equals(""))
				{
					dataAccessible.getBoard().setReaderId(position, -3);
					return;
				}

				if(setReaderIDForBoard(position, Integer.parseInt(textField.getText().trim())))
				{
					textField.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");
				}
				else
				{
					textField.setText(String.valueOf(dataAccessible.getBoard().getReaderId(position)));
					textField.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
				}
			}
		});
	}

	private boolean checkNewReaderID(int ownReaderID, int newReaderID)
	{
		if(ownReaderID == newReaderID)
		{
			return true;
		}


		for(Seat currentPlayer : dataAccessible.getSeats())
		{
			if(currentPlayer.getReaderId() == newReaderID)
			{
				AlertGenerator.showAlert(Alert.AlertType.ERROR, "Warning", "", "The reader ID \"" + newReaderID + "\" is already in use for player " + currentPlayer.getId(), ImageHandler.getIcon(), getContainingWindow(), null, false);
				return false;
			}
		}

		for(int i = 0; i < 5; i++)
		{
			if(dataAccessible.getBoard().getReaderId(i) == newReaderID)
			{
				AlertGenerator.showAlert(Alert.AlertType.ERROR, "Warning", "", "The reader ID \"" + newReaderID + "\" is already in use for board card " + i, ImageHandler.getIcon(), getContainingWindow(), null, false);
				return false;
			}
		}

		return true;
	}

	public boolean setReaderIDForPlayer(Seat seat, int newReaderID)
	{
		if(checkNewReaderID(seat.getReaderId(), newReaderID))
		{
			seat.setReaderId(newReaderID);
			return true;
		}

		return false;
	}

	public boolean setReaderIDForBoard(int boardIndex, int newReaderID)
	{
		if(checkNewReaderID(dataAccessible.getBoard().getReaderId(boardIndex), newReaderID))
		{
			dataAccessible.getBoard().setReaderId(boardIndex, newReaderID);
			return true;
		}

		return false;
	}

}