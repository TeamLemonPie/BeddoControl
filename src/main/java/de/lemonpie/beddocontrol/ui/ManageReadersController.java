package de.lemonpie.beddocontrol.ui;

import de.lemonpie.beddocommon.model.seat.Seat;
import de.lemonpie.beddocontrol.model.Board;
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

import java.util.Optional;


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
		stage.setHeight(450);
		stage.setMinHeight(450);
		stage.setMinWidth(300);
		stage.setTitle("Manage Readers");
		stage.getIcons().add(ImageHandler.getIcon());
	}

	private void initSeats()
	{
		hboxSeats.getChildren().clear();
		for(Seat seat : dataAccessible.getSeats())
		{
			hboxSeats.getChildren().add(getReaderHBox(seat.getId(), seat.getReaderId(), false));
		}
	}

	private void initBoard()
	{
		for(int i = 0; i < Board.NUMBER_OF_CARDS; i++)
		{
			hboxBoard.getChildren().add(getReaderHBox(i, dataAccessible.getBoard().getReaderId(i), true));
		}
	}

	private HBox getReaderHBox(int ID, int value, boolean isBoard)
	{
		Label labelSeatNumber = new Label(String.valueOf(ID));
		labelSeatNumber.setStyle("-fx-font-size: 16; -fx-font-weight: bold");
		labelSeatNumber.setPrefWidth(40);

		TextField textField = isBoard ? createTextFieldBoard(ID, value) : createTextFieldSeat(ID, value);

		HBox hbox = new HBox();
		hbox.getChildren().addAll(labelSeatNumber, textField);
		return hbox;
	}

	private TextField createTextFieldBoard(int ID, int value)
	{
		TextField textField = createBasicTextField(value);
		textField.setOnKeyPressed(ke -> {
			if(ke.getCode().equals(KeyCode.ENTER))
			{
				if(textField.getText().trim().equals(""))
				{
					dataAccessible.getBoard().setReaderId(ID, -3);
					return;
				}

				int newReaderID = Integer.parseInt(textField.getText().trim());
				if(isReaderIdAlreadyUsed(ID, newReaderID))
				{
					textField.setText(String.valueOf(dataAccessible.getBoard().getReaderId(ID)));
					textField.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
				}
				else
				{
					dataAccessible.getBoard().setReaderId(ID, newReaderID);
					textField.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");
				}
			}
		});

		return textField;
	}

	private TextField createTextFieldSeat(int ID, int value)
	{
		TextField textField = createBasicTextField(value);
		textField.setOnKeyPressed(ke -> {
			if(ke.getCode().equals(KeyCode.ENTER))
			{
				Optional<Seat> seatOptional = dataAccessible.getSeats().getObject(ID);
				if(textField.getText().trim().equals(""))
				{
					seatOptional.ifPresent(seat -> seat.setReaderId(-3));
					return;
				}

				int newReaderId = Integer.parseInt(textField.getText().trim());
				if(isReaderIdAlreadyUsed(ID, newReaderId))
				{
					if(seatOptional.isPresent())
					{
						textField.setText(String.valueOf(seatOptional.get().getReaderId()));
					}
					else
					{
						textField.setText("");
					}
					textField.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
				}
				else
				{
					seatOptional.ifPresent(seat -> seat.setReaderId(newReaderId));
					textField.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");
				}
			}
		});

		return textField;
	}

	private TextField createBasicTextField(int value)
	{
		TextField textField = new TextField();
		if(value < 0)
		{
			textField.setText("");
			textField.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
		}
		else
		{
			textField.setText(String.valueOf(value));
			textField.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");
		}

		textField.setPrefWidth(40);
		textField.setTextFormatter(new NumberTextFormatter());
		return textField;
	}

	private boolean isReaderIdAlreadyUsed(int oldReaderID, int newReaderId)
	{
		if(oldReaderID == newReaderId)
		{
			return true;
		}


		for(Seat currentSeat : dataAccessible.getSeats())
		{
			if(currentSeat.getReaderId() == newReaderId)
			{
				AlertGenerator.showAlert(Alert.AlertType.ERROR, "Warning", "", "The reader ID \"" + newReaderId + "\" is already in use for player " + currentSeat.getId(), ImageHandler.getIcon(), getContainingWindow(), null, false);
				return true;
			}
		}

		for(int i = 0; i < Board.NUMBER_OF_CARDS; i++)
		{
			if(dataAccessible.getBoard().getReaderId(i) == newReaderId)
			{
				AlertGenerator.showAlert(Alert.AlertType.ERROR, "Warning", "", "The reader ID \"" + newReaderId + "\" is already in use for board card " + i, ImageHandler.getIcon(), getContainingWindow(), null, false);
				return true;
			}
		}

		return false;
	}

	@FXML
	public void close()
	{
		closeStage();
	}
}