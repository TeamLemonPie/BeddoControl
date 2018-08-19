package de.lemonpie.beddocontrol.model;

import de.lemonpie.beddocommon.model.seat.SeatList;

import java.util.List;
import java.util.Optional;

public interface DataAccessible
{
	List<Player> getPlayers();

	void addPlayer(Player player);

	Optional<Player> getPlayer(int id);

	Board getBoard();

	SeatList getSeats();

	void increaseBeddoFabrikCount();

	void decreaseBeddoFabrikCount();

	void setBeddoFabrikCount(int count);
}