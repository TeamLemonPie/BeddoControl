package de.lemonpie.beddocontrol.ui;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import de.lemonpie.beddocontrol.listener.BoardListener;
import de.lemonpie.beddocontrol.model.Board;
import de.lemonpie.beddocontrol.model.DataAccessable;
import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.model.PlayerState;
import de.lemonpie.beddocontrol.model.card.Card;
import de.lemonpie.beddocontrol.model.listener.PlayerListener;
import de.lemonpie.beddocontrol.network.ControlSocket;
import de.lemonpie.beddocontrol.network.ControlSocketDelegate;
import de.lemonpie.beddocontrol.network.command.read.CardReadCommand;
import de.lemonpie.beddocontrol.network.command.read.PlayerOpReadCommand;
import de.lemonpie.beddocontrol.network.command.send.ClearSendCommand;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import logger.Logger;
import tools.AlertGenerator;

public class Controller implements DataAccessable, BoardListener, PlayerListener
{
	@FXML private TableView<Player> tableView;
	@FXML private Button buttonAdd;
	@FXML private TextField textFieldPause;
	@FXML private Label labelPause;
	@FXML private Button buttonPause;
	@FXML private Button buttonPauseReset;

	private Stage stage;
	private Image icon;
	private ResourceBundle bundle;
	private Board board;
	private ArrayList<Player> players;
	private ControlSocket socket;
	private Timeline timeline;
	private int remainingSeconds;

	public void init(Stage stage, Image icon, ResourceBundle bundle)
	{
		this.stage = stage;
		this.icon = icon;
		this.bundle = bundle;
		board = new Board();
		players = new ArrayList<>();
		// TODO externalize in config file
		socket = new ControlSocket("localhost", 9998, new ControlSocketDelegate()
		{
			@Override
			public void onConnectionEstablished()
			{
				// TODO Auto-generated method stub
				Logger.debug("Connection established.");
			}

			@Override
			public void onConnectionClosed()
			{
				// TODO Auto-generated method stub
				Logger.debug("Connection closed.");
			}

			@Override
			public void init(ControlSocket socket)
			{
				socket.addCommand(new CardReadCommand(Controller.this));
				socket.addCommand(new PlayerOpReadCommand(Controller.this));
			}
		});

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

		// DEBUG
		Player p = new Player(0);
		p.setChips(1573);
		p.setTwitchName("Papaplatte");
		p.setName("Kevin");
		p.setCardLeft(Card.fromString("Pi-7"));
		p.setPlayerState(PlayerState.OUT_OF_GAME);
		p.addListener(this);
		players.add(p);

		if(socket.connect())
		{
			initTableView();
			refreshTableView();
		}
		else
		{
			// ERRORHANDLING
			Logger.debug("Couldn't connect.");
			System.exit(1);
		}
	}

	private Image getImageForCard(Card card)
	{
		String base = "/de/lemonpie/beddocontrol/resources/cards/";

		return new Image(base + card.getSymbol() + "-" + card.getValue() + ".png");
	}

	private void initTableView()
	{
		Label labelPlaceholder = new Label("Keine Daten verfügbar");
		labelPlaceholder.setStyle("-fx-font-size: 16");
		tableView.setPlaceholder(labelPlaceholder);

		tableView.setFixedCellSize(50);
		tableView.setEditable(true);

		TableColumn<Player, Integer> columnID = new TableColumn<>();
		columnID.setCellValueFactory(new PropertyValueFactory<Player, Integer>("id"));
		columnID.setStyle("-fx-alignment: CENTER;");
		columnID.setText("Nr.");
		tableView.getColumns().add(columnID);

		// TODO column reader ID

		TableColumn<Player, String> columnName = new TableColumn<>();
		columnName.setCellValueFactory(new PropertyValueFactory<Player, String>("name"));
		columnName.setStyle("-fx-alignment: CENTER;");
		columnName.setText("Name");
		tableView.getColumns().add(columnName);

		TableColumn<Player, String> columnTwitchName = new TableColumn<>();
		columnTwitchName.setCellValueFactory(new PropertyValueFactory<Player, String>("twitchName"));
		columnTwitchName.setStyle("-fx-alignment: CENTER;");
		columnTwitchName.setText("Twitch Name");
		tableView.getColumns().add(columnTwitchName);

		TableColumn<Player, Integer> columnCards = new TableColumn<>();
		columnCards.setCellValueFactory(new PropertyValueFactory<Player, Integer>("id"));
		columnCards.setCellFactory(new Callback<TableColumn<Player, Integer>, TableCell<Player, Integer>>()
		{
			@Override
			public TableCell<Player, Integer> call(TableColumn<Player, Integer> param)
			{
				TableCell<Player, Integer> cell = new TableCell<Player, Integer>()
				{
					@Override
					public void updateItem(Integer item, boolean empty)
					{
						if(!empty && item != null)
						{
							Optional<Player> playerOptional = getPlayer(item);
							if(playerOptional.isPresent())
							{
								Player currentPlayer = playerOptional.get();

								HBox hboxCards = new HBox();
								hboxCards.setAlignment(Pos.CENTER);
								hboxCards.setSpacing(10);

								Image imageCardLeft = getImageForCard(currentPlayer.getCardLeft());
								ImageView imageViewCardLeft = new ImageView(imageCardLeft);
								imageViewCardLeft.setFitHeight(38);
								imageViewCardLeft.fitWidthProperty().bind(columnCards.widthProperty().divide(4));
								hboxCards.getChildren().add(imageViewCardLeft);

								Image imageCardRight = getImageForCard(currentPlayer.getCardLeft());
								ImageView imageViewCardRight = new ImageView(imageCardRight);
								imageViewCardRight.setFitHeight(38);
								imageViewCardRight.fitWidthProperty().bind(columnCards.widthProperty().divide(4));
								hboxCards.getChildren().add(imageViewCardRight);

								Button buttonClear = new Button("Clear");
								buttonClear.setOnAction((e) -> {
									try
									{
										socket.write(new ClearSendCommand(currentPlayer.getReaderId()));
									}
									catch(SocketException e1)
									{
										// ERRORHANDLING
										Logger.error(e1);
									}

									tableView.refresh();
								});
								hboxCards.getChildren().add(buttonClear);

								setGraphic(hboxCards);
							}
							else
							{
								setGraphic(null);
							}
						}
						else
						{
							setGraphic(null);
						}
					}
				};
				return cell;
			}
		});
		columnCards.setStyle("-fx-alignment: CENTER;");
		columnCards.setText("Karten");
		tableView.getColumns().add(columnCards);

		TableColumn<Player, Integer> columnChips = new TableColumn<>();
		columnChips.setCellValueFactory(new PropertyValueFactory<Player, Integer>("chips"));
		columnChips.setStyle("-fx-alignment: CENTER;");
		columnChips.setText("Chips");
		tableView.getColumns().add(columnChips);

		// TODO column win probability

		TableColumn<Player, PlayerState> columnStatus = new TableColumn<>();
		columnStatus.setCellValueFactory(new PropertyValueFactory<Player, PlayerState>("playerState"));
		columnStatus.setCellFactory(new Callback<TableColumn<Player, PlayerState>, TableCell<Player, PlayerState>>()
		{
			@Override
			public TableCell<Player, PlayerState> call(TableColumn<Player, PlayerState> param)
			{
				TableCell<Player, PlayerState> cell = new TableCell<Player, PlayerState>()
				{
					@Override
					public void updateItem(PlayerState item, boolean empty)
					{
						if(!empty)
						{
							switch(item)
							{
								case ACTIVE:
									this.setStyle("-fx-background-color: #48DB5E; -fx-text-fill: black; -fx-font-weight: bold; -fx-alignment: center");
									break;
								case OUT_OF_ROUND:
									this.setStyle("-fx-background-color: orange; -fx-text-fill: black; -fx-font-weight: bold; -fx-alignment: center");
									break;
								case OUT_OF_GAME:
									this.setStyle("-fx-background-color: #CC0000; -fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: center");
									break;
								default:
									break;
							}

							setText(item.getName());
						}
						else
						{
							setText(null);
						}
					}
				};
				return cell;
			}
		});
		columnStatus.setStyle("-fx-alignment: CENTER;");
		columnStatus.setText("Status");
		tableView.getColumns().add(columnStatus);

		TableColumn<Player, PlayerState> columnButtons = new TableColumn<>();
		columnButtons.setCellValueFactory(new PropertyValueFactory<Player, PlayerState>("playerState"));
		columnButtons.setCellFactory(new Callback<TableColumn<Player, PlayerState>, TableCell<Player, PlayerState>>()
		{
			@Override
			public TableCell<Player, PlayerState> call(TableColumn<Player, PlayerState> param)
			{
				TableCell<Player, PlayerState> cell = new TableCell<Player, PlayerState>()
				{
					@Override
					public void updateItem(PlayerState item, boolean empty)
					{
						if(!empty)
						{
							HBox hboxButtons = new HBox();
							hboxButtons.setAlignment(Pos.CENTER);
							hboxButtons.setSpacing(10);

							Button buttonActivate = new Button("Aktivieren");
							buttonActivate.setOnAction((e) -> {
								((Player)getTableRow().getItem()).setPlayerState(PlayerState.ACTIVE);
								tableView.refresh();
							});
							hboxButtons.getChildren().add(buttonActivate);

							Button buttonOutOfRound = new Button("Fold");
							buttonOutOfRound.setOnAction((e) -> {
								((Player)getTableRow().getItem()).setPlayerState(PlayerState.OUT_OF_ROUND);
								tableView.refresh();
							});
							hboxButtons.getChildren().add(buttonOutOfRound);

							Button buttonOutOfGame = new Button("Deactivate");
							buttonOutOfGame.setOnAction((e) -> {
								((Player)getTableRow().getItem()).setPlayerState(PlayerState.OUT_OF_GAME);
								tableView.refresh();
							});
							hboxButtons.getChildren().add(buttonOutOfGame);

							setGraphic(hboxButtons);
						}
						else
						{
							setGraphic(null);
						}
					}
				};
				return cell;
			}
		});
		columnButtons.setStyle("-fx-alignment: CENTER;");
		columnButtons.setText("Aktionen");
		tableView.getColumns().add(columnButtons);
	}

	private void refreshTableView()
	{
		tableView.getItems().clear();

		ObservableList<Player> objectsForTable = FXCollections.observableArrayList(players);
		tableView.setItems(objectsForTable);
	}

	@FXML
	public void newPlayer()
	{
		//TODO
		tableView.refresh();
	}

	@FXML
	public void setPause()
	{
		String pauseTime = textFieldPause.getText().trim();
		if(pauseTime == null || pauseTime.equals(""))
		{
			AlertGenerator.showAlert(AlertType.WARNING, "Warnung", "", "Das Feld für die Pausenzeit darf nicht leer sein.", icon, stage, null, false);
			return;
		}

		// TODO send to server

		resetPause();		
		remainingSeconds = Integer.parseInt(pauseTime) * 60;
		labelPause.setText(getMinuteStringFromSeconds(remainingSeconds));
		
		timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), (event)->{			
			remainingSeconds--;			
			labelPause.setText(getMinuteStringFromSeconds(remainingSeconds));
			if(remainingSeconds <= 0)
			{
				timeline.stop();
			}
		}));
			
		timeline.playFromStart();
	}
	
	private String getMinuteStringFromSeconds(int seconds)
	{
		int minutes = seconds / 60;
		int secondsRest = seconds % 60;
		
		return String.format("%02d", minutes) + ":"  + String.format("%02d", secondsRest);
	}

	@FXML
	void resetPause()
	{
		if(timeline != null)
		{
			timeline.stop();
		}
		labelPause.setText("--:--");
	}

	@Override
	public List<Player> getPlayers()
	{
		return players;
	}

	@Override
	public void addPlayer(Player player)
	{
		players.add(player);
	}

	@Override
	public Optional<Player> getPlayer(int id)
	{
		return players.stream().filter(r -> r.getId() == id).findFirst();
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

	// Board
	@Override
	public void cardDidChangeAtIndex(int index, Card card)
	{
		// TODO Auto-generated method stub

	}
}