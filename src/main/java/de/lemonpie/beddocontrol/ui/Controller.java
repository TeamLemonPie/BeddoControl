package de.lemonpie.beddocontrol.ui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.lemonpie.beddocontrol.midi.Midi;
import de.lemonpie.beddocontrol.midi.MidiAction;
import de.lemonpie.beddocontrol.midi.PD12Handler;
import de.lemonpie.beddocontrol.model.*;
import de.lemonpie.beddocontrol.model.card.Card;
import de.lemonpie.beddocontrol.model.timeline.CountdownType;
import de.lemonpie.beddocontrol.model.timeline.TimelineHandler;
import de.lemonpie.beddocontrol.model.timeline.TimelineInstance;
import de.lemonpie.beddocontrol.network.ControlSocket;
import de.lemonpie.beddocontrol.network.ControlSocketDelegate;
import de.lemonpie.beddocontrol.network.command.read.*;
import de.lemonpie.beddocontrol.network.command.send.*;
import de.lemonpie.beddocontrol.network.command.send.BlockSendCommand.Option;
import de.lemonpie.beddocontrol.network.command.send.player.PlayerOpSendCommand;
import de.lemonpie.beddocontrol.network.listener.BoardListenerImpl;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import logger.Logger;
import tools.*;

import javax.sound.midi.MidiUnavailableException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements DataAccessable
{
	@FXML
	private AnchorPane mainPane;
	@FXML
	private VBox vboxPlayerTable;
	@FXML
	private Button buttonAdd;
	@FXML
	private TextField textFieldPause;
	@FXML
	private Label labelPause;
	@FXML
	private Button buttonPause;
	@FXML
	private Button buttonPauseReset;
	@FXML
	private VBox vboxPause;
	@FXML
	private TextField textFieldNextPause;
	@FXML
	private Label labelNextPause;
	@FXML
	private Button buttonNextPause;
	@FXML
	private Button buttonNextPauseReset;
	@FXML
	private VBox vboxNextPause;
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
	private TextField textFieldSmallBlind;
	@FXML
	private TextField textFieldBigBlind;
	@FXML
	private TextField textFieldAnte;
	@FXML
	private Button buttonSmallBlind;
	@FXML
	private Button buttonBigBlind;
	@FXML
	private Button buttonAnte;
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
	private Button buttonClearBoard;
	@FXML
	private Button buttonNewRound;
	@FXML
	private Button buttonLockBoard;


	Stage stage;
	Image icon;
	ResourceBundle bundle;
	private Board board;
	private PlayerList players;
	ControlSocket socket;
	private TimelineHandler timelineHandler;
	private Stage modalStage;
	public static StringProperty modalText;
	private boolean isBoardLocked = false;
	private boolean isAllLocked = false;
	private Settings settings;
	PlayerTableView tableViewPlayer;

	private int beddoFabrikCount = 0;

	private final int COUNTDOWN_WARNING_TIME = 30;
	private final int COUNTDOWN_PRE_WARNING_TIME = 60;

	private ControllerListenerImpl listenerImpl;

	public void init(Stage stage, Image icon, ResourceBundle bundle)
	{
		this.stage = stage;
		this.icon = icon;
		this.bundle = bundle;
		this.listenerImpl = new ControllerListenerImpl(this);

		board = new Board();
		board.addListener(listenerImpl);

		players = new PlayerList();
		players.addListener(listenerImpl);

		modalText = new SimpleStringProperty();

		timelineHandler = new TimelineHandler();
		timelineHandler.getTimelines().add(new TimelineInstance(new Timeline(), 0));
		timelineHandler.getTimelines().add(new TimelineInstance(new Timeline(), 0));

		updateStatusLabel(labelStatus, "Connecting...", StatusLabelType.WARNING);

		Object possibleSettings = ObjectJSONHandler.loadObjectFromJSON(bundle.getString("folder"), "settings", new Settings());
		if(possibleSettings == null)
		{
			Logger.error("Missing or invalid settings.json - Created default JSON");
			try
			{
				Settings s = new Settings();
				s.setHostName("localhost");
				s.setPort(9998);
				ObjectJSONHandler.saveObjectToJSON(bundle.getString("folder"), "settings", s);
			}
			catch(IOException e1)
			{
				Logger.error(e1);
			}

			Platform.runLater(() -> {
				AlertGenerator.showAlert(AlertType.ERROR, "Error", "", "Missing or invalid settings.json.\nA default settings.json has been created.", icon, stage, null, false);
				System.exit(0);
			});
		}

		MidiHandler midiHandler = new MidiHandler(this);
		midiHandler.init(labelStatusMIDI);

		settings = (Settings) possibleSettings;
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

		textFieldPause.setTextFormatter(new NumberTextFormatter());
		textFieldPause.setOnKeyPressed(ke -> {
			if(ke.getCode().equals(KeyCode.ENTER))
			{
				setPause();
			}
		});

		textFieldNextPause.setTextFormatter(new NumberTextFormatter());
		textFieldNextPause.setOnKeyPressed(ke -> {
			if(ke.getCode().equals(KeyCode.ENTER))
			{
				setNextPause();
			}
		});

		textFieldSmallBlind.setTextFormatter(new NumberTextFormatter());
		textFieldSmallBlind.setOnKeyPressed(ke -> {
			if(ke.getCode().equals(KeyCode.ENTER))
			{
				setSmallBlind();
			}
		});
		textFieldBigBlind.setTextFormatter(new NumberTextFormatter());
		textFieldBigBlind.setOnKeyPressed(ke -> {
			if(ke.getCode().equals(KeyCode.ENTER))
			{
				setBigBlind();
			}
		});
		textFieldAnte.setTextFormatter(new NumberTextFormatter());
		textFieldAnte.setOnKeyPressed(ke -> {
			if(ke.getCode().equals(KeyCode.ENTER))
			{
				setAnte();
			}
		});

		buttonPause.setGraphic(new FontIcon(FontIconType.MAIL_FORWARD, 14, Color.BLACK));
		buttonPauseReset.setGraphic(new FontIcon(FontIconType.TRASH, 14, Color.BLACK));
		buttonNextPause.setGraphic(new FontIcon(FontIconType.MAIL_FORWARD, 14, Color.BLACK));
		buttonNextPauseReset.setGraphic(new FontIcon(FontIconType.TRASH, 14, Color.BLACK));
		buttonSmallBlind.setGraphic(new FontIcon(FontIconType.MAIL_FORWARD, 14, Color.BLACK));
		buttonBigBlind.setGraphic(new FontIcon(FontIconType.MAIL_FORWARD, 14, Color.BLACK));
		buttonAnte.setGraphic(new FontIcon(FontIconType.MAIL_FORWARD, 14, Color.BLACK));

		imageViewBoard1.setOnMouseClicked((e) -> showBoardCardGUI(0));
		imageViewBoard2.setOnMouseClicked((e) -> showBoardCardGUI(1));
		imageViewBoard3.setOnMouseClicked((e) -> showBoardCardGUI(2));
		imageViewBoard4.setOnMouseClicked((e) -> showBoardCardGUI(3));
		imageViewBoard5.setOnMouseClicked((e) -> showBoardCardGUI(4));

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

		buttonNewRound.requestFocus();
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
		return tableViewPlayer;
	}

	public PlayerList getPlayerList()
	{
		return players;
	}

	private void connect()
	{
		modalStage = showModal("Trying to connect to " + settings.getHostName() + ":" + settings.getPort(), "Connect to server...", stage, icon);

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
		});
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
			AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e1.getMessage(), icon, stage, null, false);
		}
	}

	private void resetCountdown(CountdownType countdownType)
	{
		int timelineIndex;
		Label currentLabel;
		VBox currentVbox;
		if(countdownType.equals(CountdownType.PAUSE))
		{
			timelineIndex = 0;
			currentLabel = labelPause;
			currentVbox = vboxPause;
		}
		else
		{
			timelineIndex = 1;
			currentLabel = labelNextPause;
			currentVbox = vboxNextPause;
		}

		try
		{
			socket.write(new CountdownSetSendCommand(0, countdownType));
		}
		catch(SocketException e)
		{
			Logger.error(e);
			AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e.getMessage(), icon, stage, null, false);
		}

		if(timelineHandler.getTimelines().get(timelineIndex).getTimeline() != null)
		{
			timelineHandler.getTimelines().get(timelineIndex).getTimeline().stop();
		}
		currentLabel.setText("--:--");
		currentLabel.setStyle("");
		currentVbox.setStyle("");
	}

	private void setCountdown(CountdownType countdownType)
	{
		int timelineIndex;
		Label currentLabel;
		VBox currentVbox;
		String pauseTime;
		String message;
		if(countdownType.equals(CountdownType.PAUSE))
		{
			timelineIndex = 0;
			currentLabel = labelPause;
			currentVbox = vboxPause;
			pauseTime = textFieldPause.getText().trim();
			message = "Please enter a pause time";
			resetCountdown(CountdownType.NEXT_PAUSE);
		}
		else
		{
			timelineIndex = 1;
			currentLabel = labelNextPause;
			currentVbox = vboxNextPause;
			pauseTime = textFieldNextPause.getText().trim();
			message = "Please enter a next pause time";
		}

		if(pauseTime == null || pauseTime.equals(""))
		{
			AlertGenerator.showAlert(AlertType.WARNING, "Warning", "", message, icon, stage, null, false);
			return;
		}

		resetCountdown(countdownType);

		final int minutes = Integer.parseInt(pauseTime);
		try
		{
			socket.write(new CountdownSetSendCommand(minutes, countdownType));
		}
		catch(SocketException e)
		{
			Logger.error(e);
			AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e.getMessage(), icon, stage, null, false);
		}

		int remainingSeconds = minutes * 60;
		currentLabel.setText(getMinuteStringFromSeconds(remainingSeconds));

		timelineHandler.getTimelines().set(timelineIndex, new TimelineInstance(new Timeline(), remainingSeconds));
		timelineHandler.getTimelines().get(timelineIndex).getTimeline().setCycleCount(Timeline.INDEFINITE);
		timelineHandler.getTimelines().get(timelineIndex).getTimeline().getKeyFrames().add(new KeyFrame(Duration.seconds(1), (event) -> {
			timelineHandler.getTimelines().get(timelineIndex).reduceRemainingSeconds();
			currentLabel.setText(getMinuteStringFromSeconds(timelineHandler.getTimelines().get(timelineIndex).getRemainingSeconds()));
			if(timelineHandler.getTimelines().get(timelineIndex).getRemainingSeconds() <= COUNTDOWN_WARNING_TIME)
			{
				currentVbox.setStyle("-fx-background-color: rgba(204, 0, 0, 0.3)");
			}
			else if(timelineHandler.getTimelines().get(timelineIndex).getRemainingSeconds() <= COUNTDOWN_PRE_WARNING_TIME)
			{
				currentVbox.setStyle("-fx-background-color: rgba(255, 165, 0, 0.3)");
			}
			else
			{
				currentVbox.setStyle("");
			}

			if(timelineHandler.getTimelines().get(timelineIndex).getRemainingSeconds() <= 0)
			{
				timelineHandler.getTimelines().get(timelineIndex).getTimeline().stop();
				currentVbox.setStyle("-fx-background-color: rgba(204, 0, 0, 0.5)");
			}
		}));

		timelineHandler.getTimelines().get(timelineIndex).getTimeline().playFromStart();
	}

	@FXML
	void resetPause()
	{
		resetCountdown(CountdownType.PAUSE);
	}

	@FXML
	void resetNextPause()
	{
		resetCountdown(CountdownType.NEXT_PAUSE);
	}

	@FXML
	public void setNextPause()
	{
		setCountdown(CountdownType.NEXT_PAUSE);
	}

	@FXML
	public void setPause()
	{
		setCountdown(CountdownType.PAUSE);
	}


	@FXML
	public void setSmallBlind()
	{
		String smallBlindText = textFieldSmallBlind.getText().trim();
		if(smallBlindText == null || smallBlindText.equals(""))
		{
			AlertGenerator.showAlert(AlertType.WARNING, "Warning", "", "Please enter a small blind value", icon, stage, null, false);
			textFieldSmallBlind.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
			return;
		}

		final int smallBlind = Integer.parseInt(smallBlindText);
		try
		{
			board.setSmallBlind(smallBlind);
			socket.write(new SmallBlindSendCommand(smallBlind));
		}
		catch(SocketException e)
		{
			Logger.error(e);
			textFieldSmallBlind.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
			AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e.getMessage(), icon, stage, null, false);
		}

		textFieldSmallBlind.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");
	}

	@FXML
	public void setBigBlind()
	{
		String bigBlindText = textFieldBigBlind.getText().trim();
		if(bigBlindText == null || bigBlindText.equals(""))
		{
			AlertGenerator.showAlert(AlertType.WARNING, "Warning", "", "Please enter a big blind value", icon, stage, null, false);
			textFieldBigBlind.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
			return;
		}

		final int bigBlind = Integer.parseInt(bigBlindText);
		try
		{
			board.setBigBlind(bigBlind);
			socket.write(new BigBlindSendCommand(bigBlind));
		}
		catch(SocketException e)
		{
			Logger.error(e);
			textFieldBigBlind.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
			AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e.getMessage(), icon, stage, null, false);
		}

		textFieldBigBlind.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");
	}

	@FXML
	public void setAnte()
	{
		String anteText = textFieldAnte.getText().trim();
		if(anteText == null || anteText.equals(""))
		{
			AlertGenerator.showAlert(AlertType.WARNING, "Warning", "", "Please enter an ante value", icon, stage, null, false);
			textFieldAnte.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
			return;
		}

		final int ante = Integer.parseInt(anteText);
		try
		{
			board.setAnte(ante);
			socket.write(new AnteSendCommand(ante));
		}
		catch(SocketException e)
		{
			Logger.error(e);
			textFieldAnte.setStyle("-fx-border-color: #CC0000; -fx-border-width: 2");
			AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e.getMessage(), icon, stage, null, false);
		}

		textFieldAnte.setStyle("-fx-border-color: #48DB5E; -fx-border-width: 2");
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
			AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e.getMessage(), icon, stage, null, false);
		}
	}

	private String getMinuteStringFromSeconds(int seconds)
	{
		int minutes = seconds / 60;
		int secondsRest = seconds % 60;

		return String.format("%02d", minutes) + ":" + String.format("%02d", secondsRest);
	}

	@FXML
	public void newRound()
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("New Round");
		alert.setHeaderText("");
		alert.initOwner(stage);
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
				AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e1.getMessage(), icon, stage, null, false);
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
			AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e.getMessage(), icon, stage, null, false);
		}
	}

	public boolean isBoardLocked()
	{
		return isBoardLocked;
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
			AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e.getMessage(), icon, stage, null, false);
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
		Platform.runLater(()->{
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
		try
		{
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("de/lemonpie/beddocontrol/ui/BoardCardGUI.fxml"));

			Parent root = loader.load();
			Stage newStage = new Stage();
			Scene scene = new Scene(root, 650, 270);
			newStage.setScene(scene);
			newStage.setTitle("Override Board Card");
			newStage.initOwner(stage);

			newStage.getIcons().add(icon);
			BoardCardController newController = loader.getController();
			newController.init(newStage, icon, bundle, this, index);

			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.setResizable(true);
			newStage.show();
		}
		catch(IOException e1)
		{
			Logger.error(e1);
			AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e1.getMessage(), icon, stage, null, false);
		}
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
			AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e.getMessage(), icon, stage, null, false);
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
				AlertGenerator.showAlert(AlertType.ERROR, "Warning", "", "The reader ID \"" + newReaderID + "\" is already in use for player " + currentPlayer.getId(), icon, stage, null, false);
				return false;
			}
		}

		for(int i = 0; i < 5; i++)
		{
			if(board.getReaderId(i) == newReaderID)
			{
				AlertGenerator.showAlert(AlertType.ERROR, "Warning", "", "The reader ID \"" + newReaderID + "\" is already in use for board card " + i, icon, stage, null, false);
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

	public Stage showModal(String title, String message, Stage owner, Image icon)
	{
		try
		{
			FXMLLoader fxmlLoader = new FXMLLoader(Controller.class.getResource("/de/lemonpie/beddocontrol/ui/Modal.fxml"));
			Parent root = (Parent) fxmlLoader.load();
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

	@FXML
	public void manageReaders()
	{
		try
		{
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("de/lemonpie/beddocontrol/ui/ManageReaders.fxml"));

			Parent root = loader.load();
			Stage newStage = new Stage();
			Scene scene = new Scene(root, 300, 350);
			newStage.setScene(scene);
			newStage.setTitle("Manage Readers");
			newStage.initOwner(stage);

			newStage.getIcons().add(icon);
			ManageReadersController newController = loader.getController();
			newController.init(newStage, icon, bundle, this);

			newStage.initModality(Modality.APPLICATION_MODAL);
			newStage.setResizable(true);
			newStage.show();
		}
		catch(IOException e1)
		{
			Logger.error(e1);
			AlertGenerator.showAlert(AlertType.ERROR, "Error", "An error occurred", e1.getMessage(), icon, stage, null, false);
		}
	}
}