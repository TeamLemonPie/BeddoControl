package de.lemonpie.beddocontrol.model.timeline;

import javafx.animation.Timeline;

public class TimelineInstance
{
	private Timeline timeline;
	private int remainingSeconds;

	public TimelineInstance(Timeline timeline, int remainingSeconds)
	{
		this.timeline = timeline;
		this.remainingSeconds = remainingSeconds;
	}

	public Timeline getTimeline()
	{
		return timeline;
	}

	public void setTimeline(Timeline timeline)
	{
		this.timeline = timeline;
	}

	public int getRemainingSeconds()
	{
		return remainingSeconds;
	}

	public void setRemainingSeconds(int remainingSeconds)
	{
		this.remainingSeconds = remainingSeconds;
	}
	
	public void reduceRemainingSeconds()
	{
		this.remainingSeconds--;
	}

	@Override
	public String toString()
	{
		return "TimelineInstance [timeline=" + timeline + ", remainingSeconds=" + remainingSeconds + "]";
	}
}