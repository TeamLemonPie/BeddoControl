package de.lemonpie.beddocontrol.network.command.send;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocontrol.model.card.Card;
import de.lemonpie.beddocontrol.network.CommandName;
import de.lemonpie.beddocontrol.network.ControlCommandData;
import de.lemonpie.beddocontrol.network.Scope;

public class BoardCardSetSendCommand extends ControlCommandData
{

	public BoardCardSetSendCommand(int index, Card card)
	{
		super(Scope.ADMIN, CommandName.BOARD_CARD, index);
		setValue(new JsonPrimitive(card.getName()));
	}
}
