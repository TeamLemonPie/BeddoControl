package de.lemonpie.beddocontrol.model;

import java.util.List;
import java.util.Optional;

public interface DataAccessable
{
	List<Player> getPlayers();

	void addPlayer(Player player);

	Optional<Player> getPlayer(int id);

	Board getBoard();

	void increaseBeddoFabrikCount();

	void decreaseBeddoFabrikCount();

	void setBeddoFabrikCount(int count);
}