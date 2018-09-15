package de.lemonpie.beddocontrol.midi.action;

import de.tobias.midi.action.Action;
import de.tobias.midi.action.ActionHandler;
import de.tobias.midi.event.KeyEvent;
import de.tobias.midi.feedback.FeedbackType;

import java.awt.*;

public class ConfirmActionHandler extends ActionHandler
{
	private Robot robot;

	public ConfirmActionHandler()
	{
		try
		{
			robot = new Robot();
		}
		catch(AWTException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public String actionType()
	{
		return "confirm";
	}

	@Override
	public FeedbackType handle(KeyEvent keyEvent, Action action)
	{
		if(robot != null)
		{
			robot.keyPress(java.awt.event.KeyEvent.VK_ENTER);
			robot.keyRelease(java.awt.event.KeyEvent.VK_ENTER);
		}
		return FeedbackType.NONE;
	}

	@Override
	public FeedbackType getCurrentFeedbackType(Action action)
	{
		return FeedbackType.DEFAULT;
	}
}
