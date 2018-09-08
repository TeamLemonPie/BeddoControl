package de.lemonpie.beddocontrol.midi;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import de.lemonpie.beddocommon.midi.MidiSettings;
import de.lemonpie.beddocommon.ui.StatusTagType;
import de.lemonpie.beddocontrol.midi.action.*;
import de.lemonpie.beddocontrol.ui.Controller;
import de.tobias.logger.Logger;
import de.tobias.midi.Mapping;
import de.tobias.midi.Midi;
import de.tobias.midi.action.ActionRegistry;
import javafx.application.Platform;
import tools.PathUtils;

import javax.sound.midi.MidiUnavailableException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MidiHandler
{
	private Controller controller;
	private MidiSettings midiSettings;

	public MidiHandler(Controller controller)
	{
		this.controller = controller;
	}

	public void init()
	{
		try
		{
			controller.getStatusTagBar().getTag("midi").setText("MIDI available");
			controller.getStatusTagBar().getTag("midi").setType(StatusTagType.SUCCESS);

			Path midiSettingsPath = Paths.get(PathUtils.getOSindependentPath() + controller.getBundle().getString("folder") + "midi.json");

			if(Files.notExists(midiSettingsPath))
			{
				if(Files.notExists(midiSettingsPath.getParent()))
				{
					Files.createDirectories(midiSettingsPath.getParent());
				}

				InputStream iStr = getClass().getClassLoader().getResourceAsStream("de/lemonpie/beddocontrol/midi.json");
				midiSettings = new Gson().fromJson(new InputStreamReader(iStr), MidiSettings.class);

				Files.write(midiSettingsPath, new Gson().toJson(midiSettings).getBytes());
			}
			else
			{
				BufferedReader inputStream = Files.newBufferedReader(midiSettingsPath);
				midiSettings = new Gson().fromJson(inputStream, MidiSettings.class);
			}

			Mapping.setCurrentMapping(midiSettings.getMapping());

			ActionRegistry.registerActionHandler(new BoardClearActionHandler(controller));
			ActionRegistry.registerActionHandler(new ConfirmActionHandler());
			ActionRegistry.registerActionHandler(new LockAllToggleActionHandler(controller));
			ActionRegistry.registerActionHandler(new NewRoundActionHandler(controller));
			ActionRegistry.registerActionHandler(new PlayerActivateActionHandler(controller));
			ActionRegistry.registerActionHandler(new PlayerDeactivateActionHandler(controller));
			ActionRegistry.registerActionHandler(new PlayerFoldActionHandler(controller));
			ActionRegistry.registerActionHandler(new PlayerHighlightActionHandler(controller));
			ActionRegistry.registerActionHandler(new UnlockBoardActionHandler(controller));

			Midi.getInstance().lookupMidiDevice(midiSettings.getDevice(), Midi.Mode.INPUT, Midi.Mode.OUTPUT);
		}
		catch(MidiUnavailableException | IOException | JsonSyntaxException e)
		{
			Logger.error(e.getClass() + ": " + e.getMessage());
			Platform.runLater(() -> {
				controller.getStatusTagBar().getTag("midi").setText("MIDI unavailable");
				controller.getStatusTagBar().getTag("midi").setType(StatusTagType.ERROR);
			});
		}
	}
}
