package de.lemonpie.beddocontrol.network.command.read;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import de.lemonpie.beddocommon.model.seat.Seat;
import de.lemonpie.beddocommon.network.Command;
import de.lemonpie.beddocommon.network.CommandData;
import de.lemonpie.beddocommon.network.CommandName;
import de.lemonpie.beddocontrol.model.Board;
import de.lemonpie.beddocontrol.model.DataAccessible;
import de.lemonpie.beddocontrol.model.Player;
import de.lemonpie.beddocontrol.model.card.Card;
import de.tobias.midi.Mapping;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataReadCommand implements Command
{
	private DataAccessible dataAccessible;

	public DataReadCommand(DataAccessible dataAccessible)
	{
		this.dataAccessible = dataAccessible;
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
			JsonElement players = values.get("players");
			JsonArray board = values.getAsJsonArray("board");
			JsonArray boardReader = values.getAsJsonArray("board-reader");
			JsonPrimitive readerCount = values.getAsJsonPrimitive("reader-count");
			JsonElement seats = values.get("seats");

			if(seats != null)
			{
				dataAccessible.getSeats().clear();

				Type listType = new TypeToken<ArrayList<Seat>>()
				{
				}.getType();
				final List<Seat> list = new Gson().fromJson(seats, listType);
				dataAccessible.getSeats().addAll(list);
			}

			if(players != null)
			{
				// Clear old data
				dataAccessible.getPlayers().clear();
				Type listType = new TypeToken<ArrayList<Player>>()
				{
				}.getType();
				final List<Player> list = new Gson().fromJson(players, listType);

				for(Player player : list)
				{
					dataAccessible.addPlayer(player);
				}
			}

			if(board != null)
			{
				Board b = dataAccessible.getBoard();

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
						Board b = dataAccessible.getBoard();
						b.setReaderId(i[0], reader.getAsInt());
						i[0] += 1;
					}
				});
			}

			if(readerCount != null)
			{
				int count = readerCount.getAsInt();
				dataAccessible.setBeddoFabrikCount(count);
			}
		}

		Mapping.getCurrentMapping().showFeedback();
	}
}
