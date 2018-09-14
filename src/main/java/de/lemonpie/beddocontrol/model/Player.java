package de.lemonpie.beddocontrol.model;

import de.lemonpie.beddocontrol.listener.PlayerListener;
import de.lemonpie.beddocontrol.model.card.Card;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class Player
{
	private transient List<PlayerListener> listeners;

	private int id;
	private String name;
	private String twitchName;

	private int manageCardId;

	private Card cardLeft;
	private Card cardRight;

	private int chips;
	private PlayerState state;

	private int winprobability;

	private boolean isHighlighted;

	public Player()
	{
		id = -1;
		listeners = new LinkedList<>();
	}

	public Player(int id)
	{
		listeners = new LinkedList<>();

		this.id = id;
		this.name = "[Player]";
		this.twitchName = "[TwitchName]";
		this.state = PlayerState.ACTIVE;
		this.isHighlighted = false;
	}

	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
		fireListener(listener -> listener.nameDidChange(this, name));
	}

	public String getTwitchName()
	{
		return twitchName;
	}

	public void setTwitchName(String twitchName)
	{
		this.twitchName = twitchName;
		fireListener(listener -> listener.twitchNameDidChange(this, twitchName));
	}

	public int getManageCardId()
	{
		return manageCardId;
	}

	public void setManageCardId(int manageCardId)
	{
		this.manageCardId = manageCardId;
		fireListener(listener -> listener.manageCardIdDidChange(this, manageCardId));
	}

	public Card getCardLeft()
	{
		return cardLeft;
	}

	public void setCardLeft(Card cardLeft)
	{
		this.cardLeft = cardLeft;
		fireListener(listener -> listener.cardDidChangeAtIndex(this, 0, cardLeft));
	}

	public Card getCardRight()
	{
		return cardRight;
	}

	public void setCardRight(Card cardRight)
	{
		this.cardRight = cardRight;
		fireListener(listener -> listener.cardDidChangeAtIndex(this, 1, cardRight));
	}

	public int getChips()
	{
		return chips;
	}

	public void setChips(int chips)
	{
		this.chips = chips;
		fireListener(listener -> listener.chipsDidChange(this, chips));
	}

	public PlayerState getState()
	{
		return state;
	}

	public void setState(PlayerState state)
	{
		this.state = state;
		fireListener(listener -> listener.stateDidChange(this, state));

		if(this.isHighlighted)
		{
			this.isHighlighted = false;
			fireListener(listener -> listener.isHighlightedDidChange(this, false));
		}
	}

	public int getWinprobability()
	{
		return winprobability;
	}

	public void setWinprobability(int winprobability)
	{
		this.winprobability = winprobability;
		fireListener(listener -> listener.winProbabilityDidChange(this, winprobability));
	}

	public boolean isHighlighted()
	{
		return isHighlighted;
	}

	public void setHighlighted(boolean highlighted)
	{
		if(!state.equals(PlayerState.ACTIVE))
		{
			return;
		}

		boolean didChange = this.isHighlighted != highlighted;
		this.isHighlighted = highlighted;
		if(didChange)
		{
			fireListener(listener -> listener.isHighlightedDidChange(this, highlighted));
		}
	}

	public void addListener(PlayerListener playerListener)
	{
		this.listeners.add(playerListener);
	}

	public void removeListener(PlayerListener playerListener)
	{
		this.listeners.remove(playerListener);
	}

	private void fireListener(Consumer<PlayerListener> consumer)
	{
		for(PlayerListener playerListener : listeners)
		{
			consumer.accept(playerListener);
		}
	}

	public void setCard(int index, Card card)
	{
		if(index == 0)
		{
			setCardLeft(card);
		}
		else if(index == 1)
		{
			setCardRight(card);
		}
		else
		{
			throw new IllegalArgumentException("Index is " + index + " should be 0 or 1");
		}
	}

	@Override
	public String toString()
	{
		return "Player{" +
				"id=" + id +
				", name='" + name + '\'' +
				", twitchName='" + twitchName + '\'' +
				", cardLeft=" + cardLeft +
				", cardRight=" + cardRight +
				", chips=" + chips +
				", state=" + state +
				", winprobability=" + winprobability +
				", isHighlighted=" + isHighlighted +
				'}';
	}
}