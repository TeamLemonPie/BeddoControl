package de.lemonpie.beddocontrol.ui;

import de.lemonpie.beddocommon.ServerConnectionSettings;
import de.lemonpie.beddocommon.model.BlockOption;
import de.lemonpie.beddocommon.model.seat.Seat;
import de.lemonpie.beddocommon.model.seat.SeatList;
import de.lemonpie.beddocommon.network.client.ControlSocket;
import de.lemonpie.beddocommon.network.client.ControlSocketDelegate;
import de.lemonpie.beddocommon.ui.StatusTag;
import de.lemonpie.beddocommon.ui.StatusTagBar;
import de.lemonpie.beddocommon.ui.StatusTagType;
import de.lemonpie.beddocontrol.midi.MidiHandler;
import de.lemonpie.beddocontrol.midi.listener.MidiPlayerListener;
import de.lemonpie.beddocontrol.model.*;
import de.lemonpie.beddocontrol.network.command.read.*;
import de.lemonpie.beddocontrol.network.command.send.BlockSendCommand;
import de.lemonpie.beddocontrol.network.command.send.ClearSendCommand;
import de.lemonpie.beddocontrol.network.command.send.DataSendCommand;
import de.lemonpie.beddocontrol.network.command.send.player.PlayerOpSendCommand;
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
	private HBox hboxBoard;
	@FXML
	private HBox hboxGameSettings;

	@FXML
	private Button buttonNewRound;
	@FXML
	private Button buttonManageReaders;

	private BoardController boardController;
	private CountdownController countdownController;
	private BlindController blindController;

	private PlayerList players;
	private SeatList seats;

	ControlSocket socket;
	private Stage modalStage;
	public static StringProperty modalText;
	private ServerConnectionSettings settings;
	PlayerTableView tableViewPlayer;
	private StatusTagBar statusTagBar;
	private boolean isAllLocked;

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

			Platform.runLater(() -> AlertGenerator.showAlert(AlertType.ERROR, "Error", "", "Missing or invalid settings.json.\nA default settings.json has been created.", ImageHandler.getIcon(), getContainingWindow(), null, false));
		}

		MidiHandler midiHandler = new MidiHandler(this);
		midiHandler.init();

		settings = (ServerConnectionSettings) possibleSettings;
		statusTagBar.getTag("status").setAdditionalText(settings.getHostName() + ":" + settings.getPort());

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

		initTableView();

		Platform.runLater(() -> {
			initConnection();

			boardController = new BoardController(socket, this);

			countdownController = new CountdownController(socket);
			blindController = new BlindController(socket, boardController);

			hboxBoard.getChildren().add(boardController.getParent());
			hboxGameSettings.getChildren().add(countdownController.getParent());
			hboxGameSettings.getChildren().add(blindController.getParent());

			connect();

			seats.addListener(new SeatListListenerImpl(socket, this));

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

	public BoardController getBoardController()
	{
		return boardController;
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
					if(result.isPresent() && result.get() == buttonTypeOne)
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
	public void newRound()
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("New Round");
		alert.setHeaderText("");
		alert.initOwner(getContainingWindow());
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.setContentText("Do you really want to start a new round?");

		Optional<ButtonType> result = alert.showAndWait();
		if(!result.isPresent() || result.get() != ButtonType.OK)
		{
			return;
		}

		for(Player currentPlayer : players)
		{
			if(currentPlayer.getState().equals(PlayerState.OUT_OF_ROUND))
			{
				currentPlayer.setState(PlayerState.ACTIVE);
			}
		}

		boardController.getBoard().clearCards(); // TODO is this necessary
		try
		{
			socket.write(new ClearSendCommand(-1));
		}
		catch(SocketException | IndexOutOfBoundsException e)
		{
			Logger.error(e);
			AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e.getMessage(), ImageHandler.getIcon(), getContainingWindow(), null, false);
		}

		boardController.lockBoard(true);
	}

	public void lockAll(boolean lock)
	{
		try
		{
			if(lock)
			{
				vboxAll.setDisable(true);
				buttonManageReaders.setDisable(true);
				buttonMasterLock.setGraphic(new FontIcon(FontIconType.UNLOCK, 16, Color.WHITE));
				buttonMasterLock.setStyle("-fx-background-color: #CC0000; -fx-text-fill: white");
				buttonMasterLock.setText("Unlock");
				socket.write(new BlockSendCommand(BlockOption.ALL));

				isAllLocked = true;
			}
			else
			{
				vboxAll.setDisable(false);
				buttonManageReaders.setDisable(false);
				buttonMasterLock.setGraphic(new FontIcon(FontIconType.LOCK, 16, Color.BLACK));
				buttonMasterLock.setStyle("");
				buttonMasterLock.setText("Lock");

				if(boardController.isBoardLocked())
				{
					socket.write(new BlockSendCommand(BlockOption.BOARD));
				}
				else
				{
					socket.write(new BlockSendCommand(BlockOption.NONE));
				}

				isAllLocked = false;
			}
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
		player.addListener(new MidiPlayerListener(this));
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
		return boardController.getBoard();
	}

	@Override
	public SeatList getSeats()
	{
		return seats;
	}

	@Override
	public Player getPlayerBySeat(int seatId)
	{
		if(seats.size() > seatId && seatId >= 0)
		{
			Optional<Seat> seatOptional = seats.getObject(seatId);
			if(seatOptional.isPresent())
			{
				int playerId = seatOptional.get().getPlayerId();
				if(playerId != -1)
				{
					return getPlayers().get(playerId - 1);
				}
			}
		}

		return null;
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

		seats.getObject(newSeatId).ifPresent(seat->seat.setPlayerId(playerId));

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