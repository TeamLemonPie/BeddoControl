package de.lemonpie.beddocontrol.main;

import de.lemonpie.beddocontrol.ui.Controller;
import de.tobias.logger.FileOutputOption;
import de.tobias.logger.LogLevelFilter;
import de.tobias.logger.Logger;
import de.tobias.midi.Mapping;
import de.tobias.midi.Midi;
import de.tobias.utils.application.App;
import de.tobias.utils.application.ApplicationUtils;
import de.tobias.utils.application.container.PathType;
import de.tobias.utils.nui.NVCStage;
import javafx.application.Application;
import javafx.stage.Stage;
import tools.Worker;

import java.util.Locale;
import java.util.ResourceBundle;

public class BeddoControlMain extends Application
{
	private static final ResourceBundle bundle = ResourceBundle.getBundle("de/lemonpie/beddocontrol/", Locale.GERMANY);

	public static void main(String[] args)
	{
		launch(args);
	}

	private static void prepareLogger()
	{
		App app = ApplicationUtils.getApplication();
		Logger.init(app.getPath(PathType.LOG));

		Logger.setLevelFilter(LogLevelFilter.DEBUG);
		Logger.setFileOutput(FileOutputOption.COMBINED);

		Logger.info("Launching App: {0}, version: {1}, build: {2}, date: {3}", bundle.getString("app.name"), bundle.getString("version.name"), bundle.getString("version.code"), bundle.getString("version.date"));
	}

	@Override
	public void start(Stage primaryStage)
	{
		try
		{
			NVCStage.setDisabledSizeLoading(true);
			Controller controller = new Controller(primaryStage, bundle);
			controller.showStage();
		}
		catch(Exception e)
		{
			Logger.error(e);
		}
	}

	@Override
	public void init()
	{
		prepareLogger();
	}

	@Override
	public void stop()
	{
		Mapping mapping = Mapping.getCurrentMapping();
		mapping.clearFeedback();
		Midi.getInstance().close();
		Worker.shutdown();
		System.exit(0);
	}
}