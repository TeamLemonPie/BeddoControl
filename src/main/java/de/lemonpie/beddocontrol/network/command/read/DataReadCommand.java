package de.lemonpie.beddocontrol.network.command.read;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.lemonpie.beddocommon.network.Command;
import de.lemonpie.beddocommon.network.CommandData;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocontrol.model.Board;
import de.lemonpie.beddocontrol.model.DataAccessible;
import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.model.PlayerState;
import de.lemonpie.beddocontrol.model.card.Card;

public class DataReadCommand implements Command
{
	private DataAccessible dataAccessable;

	public DataReadCommand(DataAccessible dataAccessable)
	{
		this.dataAccessable = dataAccessable;
	}

	@Override
	public CommandName name()
	{
		return CommandName.DATA;
	}

	@Override
	public void execute(CommandData data)
	{
		if(data.getValue() instanceof JsonObject)
		{
			JsonObject values = (JsonObject) data.getValue();
			JsonArray players = values.getAsJsonArray("players");
			JsonArray board = values.getAsJsonArray("board");
			JsonArray boardReader = values.getAsJsonArray("board-reader");
			JsonPrimitive readerCount = values.getAsJsonPrimitive("reader-count");

			if(players != null)
			{
				// Clear old data
				dataAccessable.getPlayers().clear();

				players.forEach(elem -> {
					if(elem instanceof JsonObject)
					{
						JsonObject obj = (JsonObject) elem;
						int id = obj.getAsJsonPrimitive("id").getAsInt();
						String name = obj.getAsJsonPrimitive("name").getAsString();
						String twitchName = obj.getAsJsonPrimitive("twitchName").getAsString();
						PlayerState state = PlayerState.valueOf(obj.getAsJsonPrimitive("state").getAsString());
						int chips = obj.getAsJsonPrimitive("chips").getAsInt();
						Card cardLeft = Card.fromString(obj.getAsJsonPrimitive("cardLeft").getAsString());
						Card cardRight = Card.fromString(obj.getAsJsonPrimitive("cardRight").getAsString());
						boolean isHighlighted = obj.getAsJsonPrimitive("isHighlighted").getAsBoolean();

						Player player = new Player(id);
						player.setName(name);
						player.setTwitchName(twitchName);
						player.setPlayerState(state);
						player.setChips(chips);
						player.setCardLeft(cardLeft);
						player.setCardRight(cardRight);
						player.setHighlighted(isHighlighted);

						dataAccessable.addPlayer(player);
					}
				});
			}

			if(board != null)
			{
				Board b = dataAccessable.getBoard();

				board.forEach(elem -> {
					if(elem instanceof JsonObject)
					{
						JsonObject obj = (JsonObject) elem;
						int id = obj.getAsJsonPrimitive("id").getAsInt();
						Card card = Card.fromString(obj.getAsJsonPrimitive("card").getAsString());

						b.setCard(id, card);

					}
				});
			}

			int[] i = {0};
			if(boardReader != null)
			{
				boardReader.forEach(reader -> {
					if(reader instanceof JsonPrimitive)
					{
						Board b = dataAccessable.getBoard();
						b.setReaderId(i[0], reader.getAsInt());
						i[0] += 1;
					}
				});
			}

			if(readerCount != null)
			{
				int count = readerCount.getAsInt();
				dataAccessable.setBeddoFabrikCount(count);
			}
		}
	}
}
