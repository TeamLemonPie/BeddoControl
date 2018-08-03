package de.lemonpie.beddocontrol.model;

import de.lemonpie.beddocontrol.listener.PlayerListListener;

import java.util.*;
import java.util.function.Consumer;

public class PlayerList implements Iterable<Player>
{

	private List<Player> data = new ArrayList<>();
	private List<PlayerListListener> listeners;

	public PlayerList()
	{
		this.listeners = new LinkedList<>();
	}

	public Player add(Player player)
	{
		fireListener(l -> l.addPlayerToList(player));
		return data.add(player) ? player : null;
	}

	public Optional<Player> getPlayer(int id)
	{
		return data.stream().filter(r -> r.getId() == id).findFirst();
	}

	public void clear()
	{
		for(Player player : getPlayer())
		{
			fireListener(l -> l.removePlayerFromList(player));
		}
		getPlayer().clear();
	}

	public boolean remove(Object o)
	{
		boolean success = data.remove(o);
		if(o instanceof Player)
		{
			fireListener(l -> l.removePlayerFromList((Player) o));
		}
		return success;
	}

	public List<Player> getPlayer()
	{
		return data;
	}

	public void addListener(PlayerListListener playerListener)
	{
		this.listeners.add(playerListener);
	}

	public void removeListener(PlayerListListener playerListener)
	{
		this.listeners.remove(playerListener);
	}

	private void fireListener(Consumer<PlayerListListener> consumer)
	{
		for(PlayerListListener playerListener : listeners)
		{
			consumer.accept(playerListener);
		}
	}

	@Override
	public Iterator<Player> iterator()
	{
		return data.iterator();
	}

	@Override
	public void forEach(Consumer<? super Player> action)
	{
		data.forEach(action);
	}

	@Override
	public Spliterator<Player> spliterator()
	{
		return data.spliterator();
	}
}
