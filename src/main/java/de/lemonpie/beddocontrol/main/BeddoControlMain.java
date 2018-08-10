package de.lemonpie.beddocontrol.main;

import de.lemonpie.beddocontrol.midi.Midi;
import de.lemonpie.beddocontrol.ui.Controller;
import de.tobias.utils.nui.NVCStage;
import javafx.application.Application;
import javafx.stage.Stage;
import logger.FileOutputMode;
import logger.Logger;
import tools.PathUtils;
import tools.Worker;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

public class BeddoControlMain extends Application
{
	private static final ResourceBundle bundle = ResourceBundle.getBundle("de/lemonpie/beddocontrol/", Locale.GERMANY);

	public static void main(String[] args)
	{
		launch(args);
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
		Parameters params = getParameters();
		String logLevelParam = params.getNamed().get("loglevel");
		Logger.setLevel(logLevelParam);

		File logFolder = new File(PathUtils.getOSindependentPath() + "LemonPie/" + bundle.getString("app.name"));
		PathUtils.checkFolder(logFolder);
		Logger.enableFileOutput(logFolder, System.out, System.err, FileOutputMode.COMBINED);

		Logger.appInfo(bundle.getString("app.name"), bundle.getString("version.name"), bundle.getString("version.code"), bundle.getString("version.date"));
	}

	@Override
	public void stop() throws Exception
	{
		Midi.getInstance().close();
		Worker.shutdown();
		System.exit(0);
	}
}