package de.lemonpie.beddocontrol.ui;

import de.lemonpie.beddocommon.ServerConnectionSettings;
import de.lemonpie.beddocommon.network.client.ControlSocket;
import de.lemonpie.beddocommon.network.client.ControlSocketDelegate;
import de.lemonpie.beddocontrol.model.*;
import de.lemonpie.beddocontrol.model.card.Card;
import de.lemonpie.beddocontrol.network.command.read.*;
import de.lemonpie.beddocontrol.network.command.send.BlockSendCommand;
import de.lemonpie.beddocontrol.network.command.send.BlockSendCommand.Option;
import de.lemonpie.beddocontrol.network.command.send.BoardCardSetSendCommand;
import de.lemonpie.beddocontrol.network.command.send.ClearSendCommand;
import de.lemonpie.beddocontrol.network.command.send.DataSendCommand;
import de.lemonpie.beddocontrol.network.command.send.player.PlayerOpSendCommand;
import de.lemonpie.beddocontrol.network.listener.BoardListenerImpl;
import de.tobias.logger.Logger;
import de.tobias.utils.nui.NVC;
import de.tobias.utils.nui.NVCStage;
import fontAwesome.FontIcon;
import fontAwesome.FontIconType;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tools.AlertGenerator;
import tools.NumberTextFormatter;
import tools.ObjectJSONHandler;
import tools.Worker;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller extends NVC implements DataAccessable
{
	@FXML
	private AnchorPane mainPane;
	@FXML
	private VBox vboxPlayerTable;
	@FXML
	private Button buttonAdd;
	@FXML
	private Label labelConnectedBeddoFabriks;
	@FXML
	private Label labelStatus;
	@FXML
	private Label labelServer;
	@FXML
	private Button buttonMasterLock;
	@FXML
	private VBox vboxAll;
	@FXML
	private Label labelStatusMIDI;

	@FXML
	ImageView imageViewBoard1;
	@FXML
	ImageView imageViewBoard2;
	@FXML
	ImageView imageViewBoard3;
	@FXML
	ImageView imageViewBoard4;
	@FXML
	ImageView imageViewBoard5;
	@FXML
	TextField textFieldBoard1;
	@FXML
	TextField textFieldBoard2;
	@FXML
	TextField textFieldBoard3;
	@FXML
	TextField textFieldBoard4;
	@FXML
	TextField textFieldBoard5;
	@FXML
	private HBox hboxBoard;

	@FXML
	private HBox hboxGameSettings;

	@FXML
	private Button buttonNewRound;
	@FXML
	private Button buttonLockBoard;

	private CountdownController countdownController;
	private BlindController blindController;

	private Board board;
	private PlayerList players;
	ControlSocket socket;
	private Stage modalStage;
	public static StringProperty modalText;
	private boolean isBoardLocked = false;
	private boolean isAllLocked = false;
	private ServerConnectionSettings settings;
	PlayerTableView tableViewPlayer;

	private int beddoFabrikCount = 0;

	private ControllerListenerImpl listenerImpl;

	public Controller(Stage stage, ResourceBundle bundle)
	{
		load("de/lemonpie/beddocontrol/ui", "GUI", bundle);
		applyViewControllerToStage(stage);
	}

	@Override
	public void init()
	{
		this.listenerImpl = new ControllerListenerImpl(this);

		board = new Board();
		board.addListener(listenerImpl);

		players = new PlayerList();
		players.addListener(listenerImpl);

		modalText = new SimpleStringProperty();

		updateStatusLabel(labelStatus, "Connecting...", StatusLabelType.WARNING);

		Object possibleSettings = ObjectJSONHandler.loadObjectFromJSON(getBundle().getString("folder"), "settings", new ServerConnectionSettings());
		if(possibleSettings == null)
		{
			Logger.error("Missing or invalid settings.json - Created default JSON");
			try
			{
				ServerConnectionSettings s = new ServerConnectionSettings();
				s.setHostName("localhost");
				s.setPort(9998);
				ObjectJSONHandler.saveObjectToJSON(getBundle().getString("folder"), "settings", s);
				possibleSettings = ObjectJSONHandler.loadObjectFromJSON(getBundle().getString("folder"), "settings", new ServerConnectionSettings());

			}
			catch(IOException e1)
			{
				Logger.error(e1);
			}

			Platform.runLater(() -> {
				AlertGenerator.showAlert(AlertType.ERROR, "Error", "", "Missing or invalid settings.json.\nA default settings.json has been created.", ImageHandler.getIcon(), getContainingWindow(), null, false);
			});
		}

		MidiHandler midiHandler = new MidiHandler(this);
		midiHandler.init(labelStatusMIDI);

		settings = (ServerConnectionSettings) possibleSettings;
		labelServer.setText(settings.getHostName() + ":" + settings.getPort());

		buttonLockBoard.setGraphic(new FontIcon(FontIconType.LOCK, 16, Color.BLACK));
		buttonLockBoard.setOnAction((e) -> lockBoard(!isBoardLocked));

		buttonMasterLock.setGraphic(new FontIcon(FontIconType.LOCK, 16, Color.BLACK));
		buttonMasterLock.setFocusTraversable(false);
		buttonMasterLock.setOnAction((e) -> lockAll(!isAllLocked));

		mainPane.setOnKeyPressed((e) ->
		{
			if(e.getCode() == KeyCode.F1)
			{
				newRound();
			}
		});

		imageViewBoard1.setOnMouseClicked((e) -> showBoardCardGUI(0));
		imageViewBoard2.setOnMouseClicked((e) -> showBoardCardGUI(1));
		imageViewBoard3.setOnMouseClicked((e) -> showBoardCardGUI(2));
		imageViewBoard4.setOnMouseClicked((e) -> showBoardCardGUI(3));
		imageViewBoard5.setOnMouseClicked((e) -> showBoardCardGUI(4));

		initTableView();
		initBoard();

		Platform.runLater(() -> {
			initConnection();

			countdownController = new CountdownController(socket);
			blindController = new BlindController(socket, board);

			hboxGameSettings.getChildren().add(countdownController.getParent());
			hboxGameSettings.getChildren().add(blindController.getParent());

			connect();
			board.addListener(new BoardListenerImpl(socket));

			buttonNewRound.requestFocus();
		});
	}

	@Override
	public void initStage(Stage stage)
	{
		stage.setWidth(1000);
		stage.setHeight(800);

		stage.getIcons().add(ImageHandler.getIcon());
		stage.setTitle(getBundle().getString("app.name") + " - " + getBundle().getString("version.name") + " (" + getBundle().getString("version.code") + ")");
	}

	public void updateStatusLabel(Label label, String text, StatusLabelType statusLabeType)
	{
		label.setText(text);
		switch(statusLabeType)
		{
			case ERROR:
				label.setStyle("-fx-background-color: rgba(204, 0, 0, 0.5); -fx-padding: 5px 7px 5px 7px; -fx-background-radius: 3px");
				break;
			case WARNING:
				label.setStyle("-fx-background-color: rgba(255, 165, 0, 0.5); -fx-padding: 5px 7px 5px 7px; -fx-background-radius: 3px");
				break;
			case SUCCESS:
				label.setStyle("-fx-background-color: rgba(72, 219, 94, 0.5); -fx-padding: 5px 7px 5px 7px; -fx-background-radius: 3px");
				break;
		}
	}

	public ControlSocket getSocket()
	{
		return socket;
	}

	public TableView<Player> getTableView()
	{
		return tableViewPlayer;
	}

	public PlayerList getPlayerList()
	{
		return players;
	}

	private void connect()
	{
		modalText.set("Trying to connect to " + settings.getHostName() + ":" + settings.getPort());
		modalStage = showModal("Connect to server...");

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
					Logger.error(e);
				}
			}
			else
			{
				Logger.debug("Couldn't connect.");
				Platform.runLater(() -> {
					if(modalStage != null)
						modalStage.close();

					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("");
					alert.setContentText("Connection could not be established.");
					Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
					dialogStage.getIcons().add(ImageHandler.getIcon());
					dialogStage.initOwner(getContainingWindow());

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
		socket = new ControlSocket(settings, new ControlSocketDelegate()
		{
			@Override
			public void onConnectionEstablished()
			{
				Logger.debug("Connection established.");
				Platform.runLater(() -> {
					updateStatusLabel(labelStatus, "Connected", StatusLabelType.SUCCESS);
				});
			}

			@Override
			public void onConnectionClosed()
			{
				Logger.debug("Connection closed.");
				Platform.runLater(() -> {
					updateStatusLabel(labelStatus, "Disconnected", StatusLabelType.ERROR);
					connect();
				});
			}

			@Override
			public void init(ControlSocket socket)
			{
				socket.addCommand(new CardReadCommand(Controller.this));
				socket.addCommand(new PlayerOpReadCommand(Controller.this));
				socket.addCommand(new DataReadCommand(Controller.this));
				socket.addCommand(new PlayerWinProbabilityReadCommand(Controller.this));
				socket.addCommand(new ReaderCountReadCommand(Controller.this));
			}

			@Override
			public void startConnecting(String host, int port)
			{
				String message = "Trying to connect to " + host + ":" + port + "...";

				Logger.debug(message);
				Platform.runLater(() -> Controller.modalText.set(message));
			}

			@Override
			public void onConnectionFailed(Exception e, int retry)
			{
				String message = e.getMessage() + " Retry " + (retry + 1) + "/" + ControlSocket.MAX +
						".\nNext Retry in " + (ControlSocket.SLEEP_TIME / 1000) + " seconds...";
				Logger.error(message);
				Platform.runLater(() -> Controller.modalText.set(message));
			}
		});
	}

	private void initTableView()
	{
		tableViewPlayer = new PlayerTableView(this);
		vboxPlayerTable.getChildren().add(tableViewPlayer);
		VBox.setVgrow(tableViewPlayer, Priority.ALWAYS);
	}

	private void initBoard()
	{
		initTextFieldBoard(textFieldBoard1, 0);
		initTextFieldBoard(textFieldBoard2, 1);
		initTextFieldBoard(textFieldBoard3, 2);
		initTextFieldBoard(textFieldBoard4, 3);
		initTextFieldBoard(textFieldBoard5, 4);
	}

	public void refreshTableView()
	{
		tableViewPlayer.getItems().clear();

		ObservableList<Player> objectsForTable = FXCollections.observableArrayList(players.getPlayer());
		tableViewPlayer.setItems(objectsForTable);
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
					board.setReaderId(position, -3);
					return;
				}

				if(setReaderIDForBoard(position, Integer.parseInt(textField.getText().trim())))
				{
					textField.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");
				}
				else
				{
					textField.setText(String.valueOf(board.getReaderId(position)));
					textField.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
				}
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
			AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e1.getMessage(), ImageHandler.getIcon(), getContainingWindow(), null, false);
		}
	}

	@FXML
	public void clearBoard()
	{
		board.clearCards();
		try
		{
			socket.write(new ClearSendCommand(-2));
		}
		catch(SocketException | IndexOutOfBoundsException e)
		{
			Logger.error(e);
			AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e.getMessage(), ImageHandler.getIcon(), getContainingWindow(), null, false);
		}
	}

	@FXML
	public void newRound()
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("New Round");
		alert.setHeaderText("");
		alert.initOwner(getContainingWindow());
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.setContentText("Do you really want to start a new round?");

		Optional<ButtonType> result = alert.showAndWait();
		if(result.get() != ButtonType.OK)
		{
			return;
		}

		for(Player currentPlayer : players)
		{
			if(currentPlayer.getPlayerState().equals(PlayerState.OUT_OF_ROUND))
			{
				currentPlayer.setPlayerState(PlayerState.ACTIVE);
			}

			try
			{
				socket.write(new ClearSendCommand(currentPlayer.getReaderId()));
			}
			catch(SocketException e1)
			{
				Logger.error(e1);
				AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e1.getMessage(), ImageHandler.getIcon(), getContainingWindow(), null, false);
			}
		}

		clearBoard();
		lockBoard(true);
	}

	public void lockBoard(boolean lock)
	{
		try
		{
			if(lock)
			{
				hboxBoard.setDisable(true);
				buttonLockBoard.setGraphic(new FontIcon(FontIconType.UNLOCK, 16, Color.BLACK));
				buttonLockBoard.setText("Unlock");
				socket.write(new BlockSendCommand(Option.BOARD));
			}
			else
			{
				hboxBoard.setDisable(false);
				buttonLockBoard.setGraphic(new FontIcon(FontIconType.LOCK, 16, Color.BLACK));
				buttonLockBoard.setText("Lock");
				socket.write(new BlockSendCommand(Option.NONE));
			}

			isBoardLocked = lock;
		}
		catch(SocketException e)
		{
			Logger.error(e);
			AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e.getMessage(), ImageHandler.getIcon(), getContainingWindow(), null, false);
		}
	}

	public void lockAll(boolean lock)
	{
		try
		{
			if(lock)
			{
				vboxAll.setDisable(true);
				buttonMasterLock.setGraphic(new FontIcon(FontIconType.UNLOCK, 16, Color.WHITE));
				buttonMasterLock.setStyle("-fx-background-color: #CC0000; -fx-text-fill: white");
				buttonMasterLock.setText("Unlock");
				socket.write(new BlockSendCommand(Option.ALL));
			}
			else
			{
				vboxAll.setDisable(false);
				buttonMasterLock.setGraphic(new FontIcon(FontIconType.LOCK, 16, Color.BLACK));
				buttonMasterLock.setStyle("");
				buttonMasterLock.setText("Lock");
				socket.write(new BlockSendCommand(Option.NONE));
			}

			isAllLocked = lock;
		}
		catch(SocketException e)
		{
			Logger.error(e);
			AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e.getMessage(), ImageHandler.getIcon(), getContainingWindow(), null, false);
		}
	}

	public boolean isAllLocked()
	{
		return isAllLocked;
	}

	@Override
	public List<Player> getPlayers()
	{
		return players.getPlayer();
	}

	@Override
	public void addPlayer(Player player)
	{
		players.add(player);
		refreshTableView();
	}

	@Override
	public Optional<Player> getPlayer(int id)
	{
		return players.getPlayer(id);
	}

	@Override
	public Board getBoard()
	{
		return board;
	}

	@Override
	public void increaseBeddoFabrikCount()
	{
		beddoFabrikCount = beddoFabrikCount + 1;
		updateBeddoFabrikCountLabel();
	}

	@Override
	public void decreaseBeddoFabrikCount()
	{
		beddoFabrikCount = beddoFabrikCount - 1;
		updateBeddoFabrikCountLabel();
	}

	@Override
	public void setBeddoFabrikCount(int count)
	{
		beddoFabrikCount = count;
		updateBeddoFabrikCountLabel();
	}

	private void updateBeddoFabrikCountLabel()
	{
		Platform.runLater(() -> {
			if(beddoFabrikCount <= 0)
			{
				updateStatusLabel(labelConnectedBeddoFabriks, String.valueOf(beddoFabrikCount) + " BeddoFabriken", StatusLabelType.ERROR);
			}
			else
			{
				updateStatusLabel(labelConnectedBeddoFabriks, String.valueOf(beddoFabrikCount) + " BeddoFabriken", StatusLabelType.SUCCESS);

			}
		});
	}

	private void showBoardCardGUI(int index)
	{
		BoardCardController boardCardController = new BoardCardController(getContainingWindow(), this, index);
		boardCardController.showStage();
	}

	public void overrideBoardCard(int index, Card card)
	{
		try
		{
			socket.write(new BoardCardSetSendCommand(index, card));
		}
		catch(SocketException e)
		{
			Logger.error(e);
			AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e.getMessage(), ImageHandler.getIcon(), getContainingWindow(), null, false);
		}
	}

	private boolean checkNewReaderID(int ownReaderID, int newReaderID)
	{
		if(ownReaderID == newReaderID)
		{
			return true;
		}


		for(Player currentPlayer : players)
		{
			if(currentPlayer.getReaderId() == newReaderID)
			{
				AlertGenerator.showAlert(AlertType.ERROR, "Warning", "", "The reader ID \"" + newReaderID + "\" is already in use for player " + currentPlayer.getId(), ImageHandler.getIcon(), getContainingWindow(), null, false);
				return false;
			}
		}

		for(int i = 0; i < 5; i++)
		{
			if(board.getReaderId(i) == newReaderID)
			{
				AlertGenerator.showAlert(AlertType.ERROR, "Warning", "", "The reader ID \"" + newReaderID + "\" is already in use for board card " + i, ImageHandler.getIcon(), getContainingWindow(), null, false);
				return false;
			}
		}

		return true;
	}

	public boolean setReaderIDForPlayer(Player player, int newReaderID)
	{
		if(checkNewReaderID(player.getReaderId(), newReaderID))
		{
			player.setReaderId(newReaderID);
			return true;
		}

		return false;
	}

	public boolean setReaderIDForBoard(int boardIndex, int newReaderID)
	{
		if(checkNewReaderID(board.getReaderId(boardIndex), newReaderID))
		{
			board.setReaderId(boardIndex, newReaderID);
			return true;
		}

		return false;
	}

	public Stage showModal(String title)
	{
		ModalController modalController = new ModalController(getContainingWindow(), modalText, title);
		modalController.showStage();
		final Optional<NVCStage> stageContainer = modalController.getStageContainer();
		return stageContainer.map(NVCStage::getStage).orElse(null);
	}

	@FXML
	public void manageReaders()
	{
		ManageReadersController manageReadersController = new ManageReadersController(getContainingWindow());
		manageReadersController.showStage();
	}
}