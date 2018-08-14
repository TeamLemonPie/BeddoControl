package de.lemonpie.beddocontrol.network.command.send;

import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocommon.network.CommandData;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocommon.network.Scope;
import de.lemonpie.beddocontrol.model.card.Card;

public class BoardCardSetSendCommand extends CommandData
{

	public BoardCardSetSendCommand(int index, Card card)
	{
		super(Scope.ADMIN, CommandName.BOARD_CARD, index);
		setValue(new JsonPrimitive(card.getName()));
	}
}
