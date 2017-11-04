package de.lemonpie.beddocontrol.ui;

import de.lemonpie.beddocontrol.listener.BoardListener;
import de.lemonpie.beddocontrol.listener.PlayerListListener;
import de.lemonpie.beddocontrol.model.*;
import de.lemonpie.beddocontrol.model.card.Card;
import de.lemonpie.beddocontrol.model.listener.PlayerListener;
import de.lemonpie.beddocontrol.network.ControlSocket;
import de.lemonpie.beddocontrol.network.ControlSocketDelegate;
import de.lemonpie.beddocontrol.network.command.read.CardReadCommand;
import de.lemonpie.beddocontrol.network.command.read.DataReadCommand;
import de.lemonpie.beddocontrol.network.command.read.PlayerOpReadCommand;
import de.lemonpie.beddocontrol.network.command.send.ClearSendCommand;
import de.lemonpie.beddocontrol.network.command.send.CountdownSetSendCommand;
import de.lemonpie.beddocontrol.network.command.send.DataSendCommand;
import de.lemonpie.beddocontrol.network.command.send.PlayerOpSendCommand;
import de.lemonpie.beddocontrol.network.listener.BoardListenerImpl;
import de.lemonpie.beddocontrol.network.listener.PlayerListenerImpl;
import de.lemonpie.beddocontrol.ui.cells.*;
import fontAwesome.FontIcon;
import fontAwesome.FontIconType;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import logger.Logger;
import tools.AlertGenerator;
import tools.Worker;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements DataAccessable, BoardListener, PlayerListener, PlayerListListener
{
	@FXML private TableView<Player> tableView;
	@FXML private Button buttonAdd;
	@FXML private TextField textFieldPause;
	@FXML private Label labelPause;
	@FXML private Button buttonPause;
	@FXML private Button buttonPauseReset;
	@FXML private Label labelStatus;

	@FXML private ImageView imageViewBoard1;
	@FXML private ImageView imageViewBoard2;
	@FXML private ImageView imageViewBoard3;
	@FXML private ImageView imageViewBoard4;
	@FXML private ImageView imageViewBoard5;
	@FXML private TextField textFieldBoard1;
	@FXML private TextField textFieldBoard2;
	@FXML private TextField textFieldBoard3;
	@FXML private TextField textFieldBoard4;
	@FXML private TextField textFieldBoard5;

	@FXML private Button buttonClearBoard;

	private Stage stage;
	private Image icon;
	private ResourceBundle bundle;
	private Board board;
	private PlayerList players;
	private ControlSocket socket;
	private Timeline timeline;
	private int remainingSeconds;
	private Stage modalStage;
	public static StringProperty modalText;

	// TODO externalize in config file
	private final String HOST = "localhost";
	private final int PORT = 9998;

	public void init(Stage stage, Image icon, ResourceBundle bundle)
	{
		this.stage = stage;
		this.icon = icon;
		this.bundle = bundle;
		board = new Board();
		board.addListener(this);

		players = new PlayerList();
		players.addListener(this);

		modalText = new SimpleStringProperty();

		labelStatus.setText("Connecting...");
		labelStatus.setStyle("-fx-text-fill: orange");

		textFieldPause.setTextFormatter(new TextFormatter<>(c -> {
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

		initTableView();
		initBoard();

		stage.setOnCloseRequest((event) -> {
			Worker.shutdown();
			System.exit(0);
		});

		Platform.runLater(() -> {
			initConnection();
			connect();
			board.addListener(new BoardListenerImpl(socket));
		});
	}

	public ControlSocket getSocket()
	{
		return socket;
	}

	public Image getIcon()
	{
		return icon;
	}

	public Stage getStage()
	{
		return stage;
	}

	public TableView<Player> getTableView()
	{
		return tableView;
	}

	public PlayerList getPlayerList()
	{
		return players;
	}

	private void connect()
	{
		modalStage = showModal("Trying to connect to " + HOST + ":" + PORT, "Connect to server...", stage, icon);

		Worker.runLater(() -> {
			if(socket.connect())
			{
				try
				{
					socket.write(new DataSendCommand());
					Platform.runLater(() -> {
						if(modalStage != null)
							modalStage.close();
						refreshTableView();
					});
				}
				catch(SocketException e)
				{
					// ERRORHANDLING
					e.printStackTrace();
				}
			}
			else
			{
				Logger.debug("Couldn't connect.");
				Platform.runLater(() -> {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("");
					alert.setContentText("Connection could not be established.");
					Stage dialogStage = (Stage)alert.getDialogPane().getScene().getWindow();
					dialogStage.getIcons().add(icon);
					dialogStage.initOwner(stage);

					ButtonType buttonTypeOne = new ButtonType("Retry");
					alert.getButtonTypes().setAll(buttonTypeOne);
					Optional<ButtonType> result = alert.showAndWait();
					if(result.get() == buttonTypeOne)
					{
						connect();
					}
				});
			}
		});
	}

	private void initConnection()
	{
		socket = new ControlSocket(HOST, PORT, new ControlSocketDelegate()
		{
			@Override
			public void onConnectionEstablished()
			{
				Logger.debug("Connection established.");
				Platform.runLater(() -> {
					labelStatus.setText("Connected");
					labelStatus.setStyle("-fx-text-fill: #48DB5E");
				});
			}

			@Override
			public void onConnectionClosed()
			{
				Logger.debug("Connection closed.");
				Platform.runLater(() -> {
					labelStatus.setText("Disconnected");
					labelStatus.setStyle("-fx-text-fill: #CC0000");
					connect();
				});
			}

			@Override
			public void init(ControlSocket socket)
			{
				socket.addCommand(new CardReadCommand(Controller.this));
				socket.addCommand(new PlayerOpReadCommand(Controller.this));
				socket.addCommand(new DataReadCommand(Controller.this));
			}
		});
	}

	public Image getImageForCard(Card card)
	{
		Image image = null;
		try
		{
			String base = "/de/lemonpie/beddocontrol/cards/";
			if(card == null || card == Card.EMPTY)
			{
				return new Image(base + "back.png");
			}
			
			image = new Image(base + card.getSymbol() + "-" + card.getValue() + ".png");
		}
		catch(Exception e)
		{
			Logger.error(e);
		}
		return image;
	}

	public FontIcon getFontIcon(FontIconType type, int size, Color color)
	{
		FontIcon icon = new FontIcon(type);
		icon.setSize(size);
		icon.setColor(color);

		return icon;
	}

	private void initTableView()
	{
		Label labelPlaceholder = new Label("No data available");
		labelPlaceholder.setStyle("-fx-font-size: 16");
		tableView.setPlaceholder(labelPlaceholder);

		tableView.setFixedCellSize(60);
		tableView.setEditable(true);

		TableColumn<Player, Integer> columnID = new TableColumn<>();
		columnID.setCellValueFactory(new PropertyValueFactory<Player, Integer>("id"));
		columnID.setStyle("-fx-alignment: CENTER;");
		columnID.setText("Nr.");
		columnID.prefWidthProperty().bind(tableView.widthProperty().multiply(0.05));
		tableView.getColumns().add(columnID);		

		TableColumn<Player, Integer> columnReader = new TableColumn<>();
		columnReader.setCellValueFactory(new PropertyValueFactory<Player, Integer>("readerId"));
		columnReader.setCellFactory(param -> {
			return new TableCellReaderID();
		});
		columnReader.setStyle("-fx-alignment: CENTER;");
		columnReader.setText("Reader ID");
		columnReader.prefWidthProperty().bind(tableView.widthProperty().multiply(0.05));
		tableView.getColumns().add(columnReader);

		TableColumn<Player, String> columnName = new TableColumn<>();
		columnName.setCellValueFactory(new PropertyValueFactory<Player, String>("name"));
		columnName.setCellFactory(param -> {
			return new TableCellName();
		});
		columnName.setStyle("-fx-alignment: CENTER;");
		columnName.setText("Name");
		columnName.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
		tableView.getColumns().add(columnName);

		TableColumn<Player, String> columnTwitchName = new TableColumn<>();
		columnTwitchName.setCellValueFactory(new PropertyValueFactory<Player, String>("twitchName"));
		columnTwitchName.setCellFactory(param -> {
			return new TableCellTwitchName();
		});
		columnTwitchName.setStyle("-fx-alignment: CENTER;");
		columnTwitchName.setText("Twitch Name");
		tableView.getColumns().add(columnTwitchName);
		columnTwitchName.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));

		TableColumn<Player, Integer> columnCards = new TableColumn<>();
		columnCards.setCellValueFactory(new PropertyValueFactory<Player, Integer>("id"));
		columnCards.setCellFactory(param -> {
			return new TableCellCards(columnCards, this);
		});
		columnCards.setStyle("-fx-alignment: CENTER;");
		columnCards.setText("Cards");
		columnCards.prefWidthProperty().bind(tableView.widthProperty().multiply(0.20));
		tableView.getColumns().add(columnCards);

		TableColumn<Player, Integer> columnChips = new TableColumn<>();
		columnChips.setCellValueFactory(new PropertyValueFactory<Player, Integer>("chips"));
		columnChips.setCellFactory(param -> {
			return new TableCellChips();
		});
		columnChips.setStyle("-fx-alignment: CENTER;");
		columnChips.setText("Chips");
		columnChips.prefWidthProperty().bind(tableView.widthProperty().multiply(0.10));
		tableView.getColumns().add(columnChips);
		
		TableColumn<Player, Integer> columnWinProbability = new TableColumn<>();
		columnWinProbability.setCellValueFactory(new PropertyValueFactory<Player, Integer>("id"));
		columnWinProbability.setCellFactory(param -> {
			return new TableCellWinProbability();
		});
		columnWinProbability.setStyle("-fx-alignment: CENTER;");
		columnWinProbability.setText("Win %");
		columnWinProbability.prefWidthProperty().bind(tableView.widthProperty().multiply(0.05));
		tableView.getColumns().add(columnWinProbability);	

		TableColumn<Player, PlayerState> columnStatus = new TableColumn<>();
		columnStatus.setCellValueFactory(new PropertyValueFactory<Player, PlayerState>("playerState"));
		columnStatus.setCellFactory(param -> {
			return new TableCellStatus();
		});
		columnStatus.setStyle("-fx-alignment: CENTER;");
		columnStatus.setText("Status");
		columnStatus.prefWidthProperty().bind(tableView.widthProperty().multiply(0.10));
		tableView.getColumns().add(columnStatus);

		TableColumn<Player, PlayerState> columnButtons = new TableColumn<>();
		columnButtons.setCellValueFactory(new PropertyValueFactory<Player, PlayerState>("playerState"));
		columnButtons.setCellFactory(param -> {
			return new TableCellActions(this);
		});
		columnButtons.setStyle("-fx-alignment: CENTER;");
		columnButtons.setText("Actions");
		columnButtons.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
		tableView.getColumns().add(columnButtons);
	}

	private void refreshTableView()
	{
		tableView.getItems().clear();

		ObservableList<Player> objectsForTable = FXCollections.observableArrayList(players.getPlayers());
		tableView.setItems(objectsForTable);
	}

	private void initBoard()
	{
		initTextFieldBoard(textFieldBoard1, 0);
		initTextFieldBoard(textFieldBoard2, 1);
		initTextFieldBoard(textFieldBoard3, 2);
		initTextFieldBoard(textFieldBoard4, 3);
		initTextFieldBoard(textFieldBoard5, 4);
	}

	private void initTextFieldBoard(TextField textField, int position)
	{
		textField.textProperty().addListener((a, b, c) -> {
			textField.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
		});

		textField.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");

		textField.setTextFormatter(new TextFormatter<>(c -> {
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

		textField.setOnKeyPressed(ke -> {
			if(ke.getCode().equals(KeyCode.ENTER))
			{
				textField.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");
				board.setReaderId(position, Integer.parseInt(textField.getText()));
			}
		});
	}

	@FXML
	public void newPlayer()
	{
		try
		{
			socket.write(new PlayerOpSendCommand());
		}
		catch(SocketException e1)
		{
			Logger.error(e1);
			AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e1.getMessage(), icon, stage, null, false);
		}
	}

	@FXML
	public void setPause()
	{
		String pauseTime = textFieldPause.getText().trim();
		if(pauseTime == null || pauseTime.equals(""))
		{
			AlertGenerator.showAlert(AlertType.WARNING, "Warning", "", "Please enter a pause time", icon, stage, null, false);
			return;
		}
		
		final int minutes = Integer.parseInt(pauseTime);
		try
		{
			socket.write(new CountdownSetSendCommand(minutes));
		}
		catch(SocketException e)
		{
			Logger.error(e);
			AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e.getMessage(), icon, stage, null, false);
		}

		resetPause();
		remainingSeconds = minutes * 60;
		labelPause.setText(getMinuteStringFromSeconds(remainingSeconds));

		timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), (event) -> {
			remainingSeconds--;
			labelPause.setText(getMinuteStringFromSeconds(remainingSeconds));
			if(remainingSeconds <= 0)
			{
				timeline.stop();
			}
		}));

		timeline.playFromStart();
	}

	@FXML
	public void clearBoard()
	{
		board.clearCards();
		for(int i = 0; i < 5; i++)
		{
			try
			{
				socket.write(new ClearSendCommand(board.getReaderId(i)));
			}
			catch(SocketException | IndexOutOfBoundsException e)
			{
				Logger.error(e);
				AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e.getMessage(), icon, stage, null, false);
			}
		}
	}

	private String getMinuteStringFromSeconds(int seconds)
	{
		int minutes = seconds / 60;
		int secondsRest = seconds % 60;

		return String.format("%02d", minutes) + ":" + String.format("%02d", secondsRest);
	}

	@FXML
	void resetPause()
	{
		try
		{
			socket.write(new CountdownSetSendCommand(0));
		}
		catch(SocketException e)
		{
			Logger.error(e);
			AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e.getMessage(), icon, stage, null, false);
		}
		if(timeline != null)
		{
			timeline.stop();
		}
		labelPause.setText("--:--");
	}

	@Override
	public List<Player> getPlayers()
	{
		return players.getPlayers();
	}

	@Override
	public void addPlayer(Player player)
	{
		players.add(player);

		refreshTableView();
	}

	// PlayerList Listener
	@Override
	public void addPlayerToList(Player player)
	{
		player.addListener(new PlayerListenerImpl(socket));
		player.addListener(this);
	}

	@Override
	public void removePlayerFromList(Player player)
	{
		try
		{
			socket.write(new PlayerOpSendCommand(player.getId()));
			refreshTableView();
		}
		catch(SocketException e1)
		{
			Logger.error(e1);
			AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e1.getMessage(), icon, stage, null, false);
		}
	}

	@Override
	public Optional<Player> getPlayer(int id)
	{
		return players.getPlayers(id);
	}

	@Override
	public Board getBoard()
	{
		return board;
	}

	public void about()
	{
		AlertGenerator.showAboutAlert(bundle.getString("app.name"), bundle.getString("version.name"), bundle.getString("version.code"), bundle.getString("version.date"), bundle.getString("author"), icon, stage, null, false);
	}

	@Override
	public void nameDidChange(Player player, String name)
	{
		tableView.refresh();
	}

	@Override
	public void twitchNameDidChange(Player player, String twitchName)
	{
		tableView.refresh();
	}

	@Override
	public void cardDidChangeAtIndex(Player player, int index, Card card)
	{
		tableView.refresh();
	}

	@Override
	public void chipsDidChange(Player player, int chips)
	{
		tableView.refresh();
	}

	@Override
	public void stateDidChange(Player player, PlayerState state)
	{
		tableView.refresh();
	}

	@Override
	public void readerIdDidChange(Player player, int readerId)
	{
		tableView.refresh();
	}

	@Override
	public void cardDidChangeAtIndex(int index, Card card)
	{
		switch(index)
		{
			case 0:
				imageViewBoard1.setImage(getImageForCard(card));
				break;
			case 1:
				imageViewBoard2.setImage(getImageForCard(card));
				break;
			case 2:
				imageViewBoard3.setImage(getImageForCard(card));
				break;
			case 3:
				imageViewBoard4.setImage(getImageForCard(card));
				break;
			case 4:
				imageViewBoard5.setImage(getImageForCard(card));
				break;
			default:
				break;
		}
	}

	@Override
	public void boardReaderIdDidChange(int index, int readerId)
	{
		String style = readerId == -2 ? "-fx-border-color: #CC0000; -fx-border-width: 2" : "-fx-border-color: #48DB5E; -fx-border-width: 2";
		switch(index)
		{
			case 0:
				textFieldBoard1.setText(String.valueOf(readerId));
				textFieldBoard1.setStyle(style);
				break;
			case 1:
				textFieldBoard2.setText(String.valueOf(readerId));
				textFieldBoard2.setStyle(style);
				break;
			case 2:
				textFieldBoard3.setText(String.valueOf(readerId));
				textFieldBoard3.setStyle(style);
				break;
			case 3:
				textFieldBoard4.setText(String.valueOf(readerId));
				textFieldBoard4.setStyle(style);
				break;
			case 4:
				textFieldBoard5.setText(String.valueOf(readerId));
				textFieldBoard5.setStyle(style);
				break;
			default:
				break;
		}
	}

	// TODO board allow card set

	public Stage showModal(String title, String message, Stage owner, Image icon)
	{
		try
		{
			FXMLLoader fxmlLoader = new FXMLLoader(Controller.class.getResource("/de/lemonpie/beddocontrol/ui/Modal.fxml"));
			Parent root = (Parent)fxmlLoader.load();
			Stage newStage = new Stage();
			newStage.initOwner(owner);
			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.setTitle(title);
			newStage.setScene(new Scene(root));
			newStage.getIcons().add(icon);
			newStage.setResizable(false);
			ModalController newController = fxmlLoader.getController();
			newController.init(newStage, modalText);
			newStage.show();

			return newStage;
		}
		catch(IOException e)
		{
			Logger.error(e);
			return null;
		}
	}
}