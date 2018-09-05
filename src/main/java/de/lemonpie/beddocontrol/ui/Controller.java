package de.lemonpie.beddocontrol.ui;

import de.lemonpie.beddocommon.ServerConnectionSettings;
import de.lemonpie.beddocommon.model.seat.Seat;
import de.lemonpie.beddocommon.model.seat.SeatList;
import de.lemonpie.beddocommon.network.client.ControlSocket;
import de.lemonpie.beddocommon.network.client.ControlSocketDelegate;
import de.lemonpie.beddocommon.ui.StatusTag;
import de.lemonpie.beddocommon.ui.StatusTagBar;
import de.lemonpie.beddocommon.ui.StatusTagType;
import de.lemonpie.beddocontrol.midi.MidiHandler;
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
import de.lemonpie.beddocontrol.network.listener.SeatListListenerImpl;
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
import tools.ObjectJSONHandler;
import tools.Worker;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller extends NVC implements DataAccessible
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
	private Button buttonMasterLock;
	@FXML
	private VBox vboxAll;
	@FXML
	private Label labelStatusMIDI;
	@FXML
	private HBox hboxMaster;

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
	private HBox hboxBoard;

	@FXML
	private HBox hboxGameSettings;

	@FXML
	private Button buttonNewRound;
	@FXML
	private Button buttonLockBoard;
	@FXML
	private Button buttonManageReaders;

	private CountdownController countdownController;
	private BlindController blindController;

	private Board board;
	private PlayerList players;
	private SeatList seats;

	ControlSocket socket;
	private Stage modalStage;
	public static StringProperty modalText;
	private boolean isBoardLocked = false;
	private boolean isAllLocked = false;
	private ServerConnectionSettings settings;
	PlayerTableView tableViewPlayer;
	private StatusTagBar statusTagBar;

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

		seats = new SeatList();

		modalText = new SimpleStringProperty();

		initStatusTagBar();

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
		midiHandler.init();

		settings = (ServerConnectionSettings) possibleSettings;
		statusTagBar.getTag("status").setAdditionalText(settings.getHostName() + ":" + settings.getPort());

		buttonLockBoard.setGraphic(new FontIcon(FontIconType.LOCK, 16, Color.BLACK));
		buttonLockBoard.setOnAction((e) -> lockBoard(!isBoardLocked));

		buttonMasterLock.setGraphic(new FontIcon(FontIconType.LOCK, 16, Color.BLACK));
		buttonMasterLock.setFocusTraversable(false);
		buttonMasterLock.setOnAction((e) -> lockAll(!isAllLocked));

		buttonManageReaders.setFocusTraversable(false);

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

		Platform.runLater(() -> {
			initConnection();

			countdownController = new CountdownController(socket);
			blindController = new BlindController(socket, board);

			hboxGameSettings.getChildren().add(countdownController.getParent());
			hboxGameSettings.getChildren().add(blindController.getParent());

			connect();
			board.addListener(new BoardListenerImpl(socket));

			seats.addListener(new SeatListListenerImpl(socket));

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

	private void initStatusTagBar()
	{
		statusTagBar = new StatusTagBar();
		statusTagBar.addTag("status", new StatusTag("Connecting...", StatusTagType.WARNING, null));
		statusTagBar.addTag("beddofabriken", new StatusTag("0 BeddoFabriken", StatusTagType.ERROR, "connected"));
		statusTagBar.addTag("midi", new StatusTag("MIDI unavailable", StatusTagType.ERROR, null));

		hboxMaster.getChildren().add(statusTagBar);
	}

	public StatusTagBar getStatusTagBar()
	{
		return statusTagBar;
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
					statusTagBar.getTag("status").setText("Connected");
					statusTagBar.getTag("status").setType(StatusTagType.SUCCESS);
				});
			}

			@Override
			public void onConnectionClosed()
			{
				Logger.debug("Connection closed.");
				Platform.runLater(() -> {
					statusTagBar.getTag("status").setText("Disconnected");
					statusTagBar.getTag("status").setType(StatusTagType.ERROR);
					connect();
				});
			}

			@Override
			public void init(ControlSocket socket)
			{
				socket.addCommand(new CardReadCommand(Controller.this));
				socket.addCommand(new PlayerOpReadCommand(Controller.this));
				socket.addCommand(new PlayerHighlightReadCommand(Controller.this));
				socket.addCommand(new DataReadCommand(Controller.this));
				socket.addCommand(new PlayerWinProbabilityReadCommand(Controller.this));
				socket.addCommand(new ReaderCountReadCommand(Controller.this));
				socket.addCommand(new SeatPlayerReadCommand(Controller.this));
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

	public void refreshTableView()
	{
		tableViewPlayer.getItems().clear();

		ObservableList<Player> objectsForTable = FXCollections.observableArrayList(players.getPlayer());
		tableViewPlayer.setItems(objectsForTable);
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
		}

		board.clearCards(); // TODO is this necessary
		try
		{
			socket.write(new ClearSendCommand(-1));
		}
		catch(SocketException | IndexOutOfBoundsException e)
		{
			Logger.error(e);
			AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e.getMessage(), ImageHandler.getIcon(), getContainingWindow(), null, false);
		}

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
	public SeatList getSeats()
	{
		return seats;
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
				statusTagBar.getTag("beddofabriken").setText(String.valueOf(beddoFabrikCount) + " BeddoFabriken");
				statusTagBar.getTag("beddofabriken").setType(StatusTagType.ERROR);
			}
			else
			{
				statusTagBar.getTag("beddofabriken").setText(String.valueOf(beddoFabrikCount) + " BeddoFabriken");
				statusTagBar.getTag("beddofabriken").setType(StatusTagType.SUCCESS);
			}
		});
	}

	@Override
	public void seatAssignNewPlayerId(int seatId, int newPlayerId)
	{
		seats.getData().get(seatId).setPlayerId(newPlayerId);
		refreshTableView();
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

	public Stage showModal(String title)
	{
		ModalController modalController = new ModalController(getContainingWindow(), modalText, title);
		modalController.showStage();
		final Optional<NVCStage> stageContainer = modalController.getStageContainer();
		return stageContainer.map(NVCStage::getStage).orElse(null);
	}

	public boolean setSeatIdForPlayer(int playerId, int newSeatId)
	{
		Optional<Seat> existingSeat = seats.getSeatByPlayerId(playerId);
		if(existingSeat.isPresent())
		{
			if(existingSeat.get().getId() == newSeatId)
			{
				return true;
			}
		}

		for(Seat seat : seats)
		{
			if(seat.getId() == newSeatId)
			{
				if(seat.getPlayerId() != -1)
				{
					AlertGenerator.showAlert(Alert.AlertType.ERROR, "Warning", "", "The seat ID \"" + newSeatId + "\" is already in use for player " + seat.getPlayerId(), ImageHandler.getIcon(), getContainingWindow(), null, false);
					return false;
				}
			}
		}

		seats.getObject(newSeatId).get().setPlayerId(playerId);

		return true;
	}

	public boolean setManageCardIdForPlayer(int playerId, int newManageCardId)
	{
		Optional<Player> playerOptional = players.getPlayer(playerId);
		if(!playerOptional.isPresent())
		{
			return false;
		}

		Player player = playerOptional.get();
		if(player.getManageCardId() == newManageCardId)
		{
			return true;
		}

		for(Player currentPlayer : players)
		{
			if(currentPlayer.getManageCardId() == newManageCardId)
			{
				AlertGenerator.showAlert(Alert.AlertType.ERROR, "Warning", "", "The manageCard ID \"" + newManageCardId + "\" is already in use for player " + currentPlayer.getId(), ImageHandler.getIcon(), getContainingWindow(), null, false);
				return false;
			}
		}

		player.setManageCardId(newManageCardId);
		return true;
	}

	@FXML
	public void manageReaders()
	{
		ManageReadersController manageReadersController = new ManageReadersController(this, getContainingWindow());
		manageReadersController.showStage();
	}
}