package de.lemonpie.beddocontrol.ui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.lemonpie.beddocontrol.midi.Midi;
import de.lemonpie.beddocontrol.midi.MidiAction;
import de.lemonpie.beddocontrol.midi.PD12Handler;
import de.tobias.logger.Logger;
import javafx.application.Platform;
import javafx.scene.control.Label;
import tools.PathUtils;

import javax.sound.midi.MidiUnavailableException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MidiHandler
{
	private List<MidiAction> midiActionList;
	private Controller controller;

	public MidiHandler(Controller controller)
	{
		this.midiActionList = new ArrayList<>();
		this.controller = controller;
	}

	public void init(Label labelStatusMIDI)
	{
		try
		{
			controller.updateStatusLabel(labelStatusMIDI, "MIDI available", StatusLabelType.SUCCESS);

			Path midiSettingsPath = Paths.get(PathUtils.getOSindependentPath() + controller.getBundle().getString("folder") + "midi.json");

			if(Files.notExists(midiSettingsPath))
			{
				if(Files.notExists(midiSettingsPath.getParent()))
				{
					Files.createDirectories(midiSettingsPath.getParent());
				}

				InputStream iStr = getClass().getClassLoader().getResourceAsStream("de/lemonpie/beddocontrol/midi.json");
				Files.copy(iStr, midiSettingsPath);
			}

			BufferedReader inputStream = Files.newBufferedReader(midiSettingsPath);
			Type type = new TypeToken<List<MidiAction>>()
			{
			}.getType();
			midiActionList = new Gson().fromJson(inputStream, type);
			Midi.getInstance().lookupMidiDevice("PD 12");
			Midi.getInstance().setListener(new PD12Handler(controller, midiActionList));
		}
		catch(MidiUnavailableException | IOException e)
		{
			Logger.error(e.getClass());
			Platform.runLater(() -> controller.updateStatusLabel(labelStatusMIDI, "MIDI unavailable", StatusLabelType.ERROR));
		}
	}
}
