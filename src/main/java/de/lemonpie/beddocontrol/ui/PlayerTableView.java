package de.lemonpie.beddocontrol.ui;

import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.model.PlayerState;
import de.lemonpie.beddocontrol.ui.cells.*;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class PlayerTableView extends TableView<Player>
{
	public PlayerTableView(Controller controller)
	{
		Label labelPlaceholder = new Label("No data available");
		labelPlaceholder.setStyle("-fx-font-size: 16");
		setPlaceholder(labelPlaceholder);

		setFixedCellSize(60);
		setEditable(true);

		TableColumn<Player, Integer> columnID = new TableColumn<>();
		columnID.setCellValueFactory(new PropertyValueFactory<>("id"));
		columnID.setStyle("-fx-alignment: CENTER;");
		columnID.setText("Nr.");
		columnID.prefWidthProperty().bind(widthProperty().multiply(0.03).subtract(2));
		getColumns().add(columnID);

		TableColumn<Player, Integer> columnReader = new TableColumn<>();
		columnReader.setCellValueFactory(new PropertyValueFactory<>("readerId"));
		columnReader.setCellFactory(param -> new TableCellReaderID(controller));
		columnReader.setStyle("-fx-alignment: CENTER;");
		columnReader.setText("Reader ID");
		columnReader.prefWidthProperty().bind(widthProperty().multiply(0.05).subtract(2));
		getColumns().add(columnReader);

		TableColumn<Player, String> columnName = new TableColumn<>();
		columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		columnName.setCellFactory(param -> new TableCellName());
		columnName.setStyle("-fx-alignment: CENTER;");
		columnName.setText("Name");
		columnName.prefWidthProperty().bind(widthProperty().multiply(0.15).subtract(2));
		getColumns().add(columnName);

		TableColumn<Player, String> columnTwitchName = new TableColumn<>();
		columnTwitchName.setCellValueFactory(new PropertyValueFactory<>("twitchName"));
		columnTwitchName.setCellFactory(param -> new TableCellTwitchName());
		columnTwitchName.setStyle("-fx-alignment: CENTER;");
		columnTwitchName.setText("Twitch Name");
		getColumns().add(columnTwitchName);
		columnTwitchName.prefWidthProperty().bind(widthProperty().multiply(0.15).subtract(2));

		TableColumn<Player, Integer> columnCards = new TableColumn<>();
		columnCards.setCellValueFactory(new PropertyValueFactory<>("id"));
		columnCards.setCellFactory(param -> new TableCellCards(columnCards, controller));
		columnCards.setStyle("-fx-alignment: CENTER;");
		columnCards.setText("Cards");
		columnCards.prefWidthProperty().bind(widthProperty().multiply(0.20).subtract(2));
		getColumns().add(columnCards);

		TableColumn<Player, Integer> columnChips = new TableColumn<>();
		columnChips.setCellValueFactory(new PropertyValueFactory<>("chips"));
		columnChips.setCellFactory(param -> new TableCellChips());
		columnChips.setStyle("-fx-alignment: CENTER;");
		columnChips.setText("Chips");
		columnChips.prefWidthProperty().bind(widthProperty().multiply(0.09).subtract(2));
		getColumns().add(columnChips);

		TableColumn<Player, Integer> columnWinProbability = new TableColumn<>();
		columnWinProbability.setCellValueFactory(new PropertyValueFactory<>("winprobability"));
		columnWinProbability.setCellFactory(param -> new TableCellWinProbability());
		columnWinProbability.setStyle("-fx-alignment: CENTER;");
		columnWinProbability.setText("Win %");
		columnWinProbability.prefWidthProperty().bind(widthProperty().multiply(0.05).subtract(2));
		getColumns().add(columnWinProbability);

		TableColumn<Player, Integer> columnStatus = new TableColumn<>();
		columnStatus.setCellValueFactory(new PropertyValueFactory<>("id"));
		columnStatus.setCellFactory(param -> new TableCellStatus(controller));
		columnStatus.setStyle("-fx-alignment: CENTER;");
		columnStatus.setText("Status");
		columnStatus.prefWidthProperty().bind(widthProperty().multiply(0.10).subtract(2));
		getColumns().add(columnStatus);

		TableColumn<Player, PlayerState> columnButtons = new TableColumn<>();
		columnButtons.setCellValueFactory(new PropertyValueFactory<>("playerState"));
		columnButtons.setCellFactory(param -> new TableCellActions(controller));
		columnButtons.setStyle("-fx-alignment: CENTER;");
		columnButtons.setText("Actions");
		columnButtons.prefWidthProperty().bind(widthProperty().multiply(0.18).subtract(2));
		getColumns().add(columnButtons);
	}
}
