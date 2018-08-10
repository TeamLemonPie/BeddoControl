package de.lemonpie.beddocontrol.ui;

import de.tobias.utils.nui.NVC;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;


public class ManageReadersController extends NVC
{
	@FXML
	private AnchorPane mainPane;

	@FXML
	private VBox hboxSeats;

	@FXML
	private VBox hboxBoard;

	public ManageReadersController(Window owner)
	{
		load("de/lemonpie/beddocontrol/ui", "ManageReadersGUI");
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
}