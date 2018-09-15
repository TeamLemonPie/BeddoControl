package de.lemonpie.beddocontrol.midi.action;

import de.lemonpie.beddocontrol.ui.Controller;
import de.tobias.midi.action.Action;
import de.tobias.midi.action.ActionHandler;
import de.tobias.midi.event.KeyEvent;
import de.tobias.midi.feedback.FeedbackType;
import javafx.application.Platform;

public class UnlockBoardActionHandler extends ActionHandler
{
	private Controller controller;

	public UnlockBoardActionHandler(Controller controller)
	{
		this.controller = controller;
	}

	@Override
	public String actionType()
	{
		return "unlock_board";
	}

	@Override
	public FeedbackType handle(KeyEvent keyEvent, Action action)
	{
		Platform.runLater(() -> controller.getBoardController().lockBoard(false));
		return FeedbackType.NONE;
	}

	@Override
	public FeedbackType getCurrentFeedbackType(Action action)
	{
		return FeedbackType.DEFAULT;
	}
}
