package de.lemonpie.beddocontrol.ui;

import de.lemonpie.beddocommon.model.BlockOption;
import de.lemonpie.beddocommon.network.client.ControlSocket;
import de.lemonpie.beddocontrol.midi.listener.MidiBoardListener;
import de.lemonpie.beddocontrol.model.Board;
import de.lemonpie.beddocontrol.network.command.send.BlockSendCommand;
import de.lemonpie.beddocontrol.network.command.send.ClearSendCommand;
import de.tobias.logger.Logger;
import de.tobias.utils.nui.NVC;
import fontAwesome.FontIcon;
import fontAwesome.FontIconType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import tools.AlertGenerator;

import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class BoardController extends NVC
{
	@FXML
	private VBox vboxRoot;

	@FXML
	private HBox hboxBoard;

	@FXML
	private ImageView imageViewBoard0;

	@FXML
	private ImageView imageViewBoard1;

	@FXML
	private ImageView imageViewBoard2;

	@FXML
	private ImageView imageViewBoard3;

	@FXML
	private ImageView imageViewBoard4;

	@FXML
	private Button buttonClearBoard;

	@FXML
	private Button buttonLockBoard;

	private ControlSocket socket;
	private Controller controller;
	private Board board;
	private BoardListenerImpl listenerImpl;
	private Map<Integer, ImageView> imageViewMap;

	public BoardController(ControlSocket socket, Controller controller)
	{
		this.socket = socket;
		this.controller = controller;

		load("de/lemonpie/beddocontrol/ui", "BoardGUI");
	}

	public Board getBoard()
	{
		return board;
	}

	@Override
	public void init()
	{
		HBox.setHgrow(vboxRoot, Priority.ALWAYS);

		listenerImpl = new BoardListenerImpl(this);

		board = new Board();
		board.addListener(listenerImpl);
		board.addListener(new MidiBoardListener());
		board.addListener(new de.lemonpie.beddocontrol.network.listener.BoardListenerImpl(socket));

		buttonLockBoard.setGraphic(new FontIcon(FontIconType.LOCK, 16, Color.BLACK));
		buttonLockBoard.setOnAction((e) -> lockBoard(!board.isLocked()));

		imageViewMap = new HashMap<>();
		imageViewMap.put(0, imageViewBoard0);
		imageViewMap.put(1, imageViewBoard1);
		imageViewMap.put(2, imageViewBoard2);
		imageViewMap.put(3, imageViewBoard3);
		imageViewMap.put(4, imageViewBoard4);

		imageViewBoard0.setOnMouseClicked((e) -> showBoardCardGUI(0));
		imageViewBoard1.setOnMouseClicked((e) -> showBoardCardGUI(1));
		imageViewBoard2.setOnMouseClicked((e) -> showBoardCardGUI(2));
		imageViewBoard3.setOnMouseClicked((e) -> showBoardCardGUI(3));
		imageViewBoard4.setOnMouseClicked((e) -> showBoardCardGUI(4));
	}

	public void setImageForImageView(Image image, int ID)
	{
		Platform.runLater(() -> imageViewMap.get(ID).setImage(image));
	}

	private void showBoardCardGUI(int index)
	{
		BoardCardController boardCardController = new BoardCardController(getContainingWindow(), controller, index);
		boardCardController.showStage();
	}

	public void lockBoard(boolean lock)
	{
		try
		{
			if(lock)
			{
				hboxBoard.setDisable(true);
				buttonLockBoard.setGraphic(new FontIcon(FontIconType.UNLOCK, 16, Color.WHITE));
				buttonLockBoard.setStyle("-fx-background-color: #CC0000; -fx-text-fill: white");
				buttonLockBoard.setText("Unlock");
				socket.write(new BlockSendCommand(BlockOption.BOARD));
			}
			else
			{
				hboxBoard.setDisable(false);
				buttonLockBoard.setGraphic(new FontIcon(FontIconType.LOCK, 16, Color.BLACK));
				buttonLockBoard.setStyle("");
				buttonLockBoard.setText("Lock");
				socket.write(new BlockSendCommand(BlockOption.NONE));
			}

			board.setLocked(lock);
		}
		catch(SocketException e)
		{
			Logger.error(e);
			AlertGenerator.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred", e.getMessage(), ImageHandler.getIcon(), getContainingWindow(), null, false);
		}
	}

	public boolean isBoardLocked()
	{
		return board.isLocked();
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
			AlertGenerator.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred", e.getMessage(), ImageHandler.getIcon(), getContainingWindow(), null, false);
		}
	}
}
