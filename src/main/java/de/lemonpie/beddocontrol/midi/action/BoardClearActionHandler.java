package de.lemonpie.beddocontrol.midi.action;

import de.lemonpie.beddocontrol.ui.Controller;
import de.tobias.midi.action.Action;
import de.tobias.midi.action.ActionHandler;
import de.tobias.midi.event.KeyEvent;
import de.tobias.midi.feedback.FeedbackType;
import javafx.application.Platform;

public class BoardClearActionHandler extends ActionHandler
{
	private Controller controller;

	public BoardClearActionHandler(Controller controller)
	{
		this.controller = controller;
	}

	@Override
	public String actionType()
	{
		return "board_clear";
	}

	@Override
	public FeedbackType handle(KeyEvent keyEvent, Action action)
	{
		Platform.runLater(() -> controller.clearBoard());
		return FeedbackType.NONE;
	}
}
