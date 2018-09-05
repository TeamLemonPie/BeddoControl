package de.lemonpie.beddocontrol.model.timeline;

import java.util.ArrayList;

public class TimelineHandler
{
	private ArrayList<TimelineInstance> timelines;

	public TimelineHandler()
	{
		timelines = new ArrayList<>();
	}

	public ArrayList<TimelineInstance> getTimelines()
	{
		return timelines;
	}

	public void setTimelines(ArrayList<TimelineInstance> timelines)
	{
		this.timelines = timelines;
	}

	@Overrides
	public String toString()
	{
		return "TimelineHandler [timelines=" + timelines + "]";
	}
}