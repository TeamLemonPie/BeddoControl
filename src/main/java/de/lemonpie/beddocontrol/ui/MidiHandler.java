package de.lemonpie.beddocontrol.ui;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import de.lemonpie.beddocommon.ui.StatusTagType;
import de.lemonpie.beddocontrol.midi.action.*;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MidiHandler
{
	private Controller controller;

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
				Files.copy(iStr, midiSettingsPath);
			}

			ActionRegistry.registerActionHandler(new BoardClearActionHandler(controller));
			ActionRegistry.registerActionHandler(new ConfirmActionHandler());
			ActionRegistry.registerActionHandler(new LockAllToggleActionHandler(controller));
			ActionRegistry.registerActionHandler(new NewRoundActionHandler(controller));
			ActionRegistry.registerActionHandler(new PlayerActivateActionHandler(controller));
			ActionRegistry.registerActionHandler(new PlayerDeactivateActionHandler(controller));
			ActionRegistry.registerActionHandler(new PlayerFoldActionHandler(controller));
			ActionRegistry.registerActionHandler(new PlayerHighlightActionHandler(controller));
			ActionRegistry.registerActionHandler(new UnlockBoardActionHandler(controller));

			BufferedReader inputStream = Files.newBufferedReader(midiSettingsPath);
			Mapping.setCurrentMapping(new Gson().fromJson(inputStream, Mapping.class));

			Midi.getInstance().lookupMidiDevice("PD 12", Midi.Mode.INPUT, Midi.Mode.OUTPUT);
		}
		catch(MidiUnavailableException | IOException | JsonSyntaxException e)
		{
			Logger.error(e.getClass());
			Platform.runLater(() -> {
				controller.getStatusTagBar().getTag("midi").setText("MIDI unavailable");
				controller.getStatusTagBar().getTag("midi").setType(StatusTagType.ERROR);
			});
		}
	}
}
