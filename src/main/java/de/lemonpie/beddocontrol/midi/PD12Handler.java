package de.lemonpie.beddocontrol.midi;

import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.model.PlayerState;
import de.lemonpie.beddocontrol.ui.Controller;
import javafx.application.Platform;

import javax.sound.midi.MidiMessage;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class PD12Handler implements MidiListener
{

	private Controller controller;
	private List<MidiAction> midiActions;

	private Robot robot;

	public PD12Handler(Controller controller, List<MidiAction> midiActionList)
	{
		this.controller = controller;
		this.midiActions = midiActionList;

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
	public void onMidiAction(MidiMessage message)
	{
		if(message.getMessage()[2] == 0)
		{ // MIDI Velocity 0 --> Key Released
			return;
		}

		for(MidiAction midiAction : midiActions)
		{
			if(message.getMessage()[1] == midiAction.getKey())
			{
				switch(midiAction.getType())
				{
					case PLAYER_FOLD:
						List<Player> players = controller.getPlayerList().getPlayer();
						int additionalInfo = midiAction.getAdditionalInfo();
						if(players.size() > additionalInfo && additionalInfo >= 0)
						{
							Player player = players.get(additionalInfo);
							if(player.getPlayerState() == PlayerState.ACTIVE)
							{
								player.setPlayerState(PlayerState.OUT_OF_ROUND);
							}
							else if(player.getPlayerState() == PlayerState.OUT_OF_ROUND)
							{
								player.setPlayerState(PlayerState.ACTIVE);
							}
						}
						break;
					case NEW_ROUND:
						Platform.runLater(controller::newRound);
						break;
					case CONFIRM:
						if(robot != null)
						{
							robot.keyPress(KeyEvent.VK_ENTER);
							robot.keyRelease(KeyEvent.VK_ENTER);
						}
						break;
					case LOCK_ALL_TOGGLE:
						Platform.runLater(() -> controller.lockAll(!controller.isAllLocked()));
						break;
					case UNLOCK_BOARD:
						Platform.runLater(() -> controller.lockBoard(false));
						break;
					case BOARD_CLEAR:
						Platform.runLater(() -> controller.clearBoard());
						break;
				}
			}
		}
	}
}
