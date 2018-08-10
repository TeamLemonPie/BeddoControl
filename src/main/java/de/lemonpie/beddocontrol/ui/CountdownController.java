package de.lemonpie.beddocontrol.ui;

import de.lemonpie.beddocontrol.model.timeline.CountdownType;
import de.lemonpie.beddocontrol.model.timeline.TimelineHandler;
import de.lemonpie.beddocontrol.model.timeline.TimelineInstance;
import de.lemonpie.beddocontrol.network.ControlSocket;
import de.lemonpie.beddocontrol.network.command.send.CountdownSetSendCommand;
import de.tobias.utils.nui.NVC;
import fontAwesome.FontIcon;
import fontAwesome.FontIconType;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import logger.Logger;
import tools.AlertGenerator;
import tools.NumberTextFormatter;

import java.net.SocketException;

public class CountdownController extends NVC
{
	private final int COUNTDOWN_WARNING_TIME = 30;
	private final int COUNTDOWN_PRE_WARNING_TIME = 60;

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

	private ControlSocket socket;
	private TimelineHandler timelineHandler;

	public CountdownController(ControlSocket socket)
	{
		this.socket = socket;

		load("de/lemonpie/beddocontrol/ui", "CountdownGUI");
	}

	public void init()
	{
		timelineHandler = new TimelineHandler();
		timelineHandler.getTimelines().add(new TimelineInstance(new Timeline(), 0));
		timelineHandler.getTimelines().add(new TimelineInstance(new Timeline(), 0));

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

		buttonPause.setGraphic(new FontIcon(FontIconType.MAIL_FORWARD, 14, Color.BLACK));
		buttonPauseReset.setGraphic(new FontIcon(FontIconType.TRASH, 14, Color.BLACK));
		buttonNextPause.setGraphic(new FontIcon(FontIconType.MAIL_FORWARD, 14, Color.BLACK));
		buttonNextPauseReset.setGraphic(new FontIcon(FontIconType.TRASH, 14, Color.BLACK));
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
			AlertGenerator.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred", e.getMessage(), ImageHandler.getIcon(), getContainingWindow(), null, false);
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
			AlertGenerator.showAlert(Alert.AlertType.WARNING, "Warning", "", message, ImageHandler.getIcon(), getContainingWindow(), null, false);
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
			AlertGenerator.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred", e.getMessage(), ImageHandler.getIcon(), getContainingWindow(), null, false);
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

	private String getMinuteStringFromSeconds(int seconds)
	{
		int minutes = seconds / 60;
		int secondsRest = seconds % 60;

		return String.format("%02d", minutes) + ":" + String.format("%02d", secondsRest);
	}

}
