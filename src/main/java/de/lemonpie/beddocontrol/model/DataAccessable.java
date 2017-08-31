package de.lemonpie.beddocontrol.model;

import java.util.List;
import java.util.Optional;

public interface DataAccessable
{
	public List<Player> getPlayers();
	
	public void addPlayer(Player player);

	public Optional<Player> getPlayer(int id);

	public Board getBoard();
}