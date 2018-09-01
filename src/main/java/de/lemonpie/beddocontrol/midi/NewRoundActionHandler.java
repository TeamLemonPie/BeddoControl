package de.lemonpie.beddocontrol.midi;

import de.lemonpie.beddocontrol.ui.Controller;
import de.tobias.midi.action.Action;
import de.tobias.midi.action.ActionHandler;
import de.tobias.midi.event.KeyEvent;
import de.tobias.midi.feedback.FeedbackType;
import javafx.application.Platform;

public class NewRoundActionHandler extends ActionHandler
{
	private Controller controller;

	public NewRoundActionHandler(Controller controller)
	{
		this.controller = controller;
	}

	@Override
	public String actionType()
	{
		return "new_round";
	}

	@Override
	public FeedbackType handle(KeyEvent keyEvent, Action action)
	{
		Platform.runLater(controller::newRound);
		return FeedbackType.NONE;
	}
}
