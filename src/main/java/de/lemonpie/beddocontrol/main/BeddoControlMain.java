package de.lemonpie.beddocontrol.main;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.lemonpie.beddocontrol.midi.Midi;
import de.lemonpie.beddocontrol.midi.MidiAction;
import de.lemonpie.beddocontrol.midi.PD12Handler;
import de.lemonpie.beddocontrol.ui.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import logger.FileOutputMode;
import logger.Logger;
import tools.PathUtils;

import java.io.BufferedReader;
import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class BeddoControlMain extends Application
{
	private static final ResourceBundle bundle = ResourceBundle.getBundle("de/lemonpie/beddocontrol/", Locale.GERMANY);

	private static List<MidiAction> midiActionList = new ArrayList<>();

	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		try
		{
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("de/lemonpie/beddocontrol/ui/GUI.fxml"));
			Parent root = loader.load();

			Scene scene = new Scene(root, 900, 800);

			Image icon = new Image("/de/lemonpie/beddocontrol/icon.png");
			Controller controller = loader.getController();
			if(controller.init(primaryStage, icon, bundle))
			{
				try
				{
					Midi.getInstance().lookupMidiDevice("PD 12");
					Midi.getInstance().setListener(new PD12Handler(controller, midiActionList));
				}
				catch(Exception e)
				{
					Logger.error(e);
				}
			}
			
			primaryStage.getIcons().add(icon);
			primaryStage.setTitle(bundle.getString("app.name") + " - " + bundle.getString("version.name")+ " (" + bundle.getString("version.code") + ")");
			primaryStage.setScene(scene);

			primaryStage.show();
		}
		catch(Exception e)
		{
			Logger.error(e);
		}
	}
	
	@Override
	public void init() throws Exception {
		BufferedReader inputStream = Files.newBufferedReader(Paths.get("midi.json"));
		Type type = new TypeToken<List<MidiAction>>() {
		}.getType();
		midiActionList = new Gson().fromJson(inputStream, type);

		Parameters params = getParameters();
		String logLevelParam = params.getNamed().get("loglevel");		
		Logger.setLevel(logLevelParam);	
		
		File logFolder = new File(PathUtils.getOSindependentPath() + "/LemonPie/" + bundle.getString("app.name"));			
		PathUtils.checkFolder(logFolder);
		Logger.enableFileOutput(logFolder, System.out, System.err, FileOutputMode.COMBINED);
		
		Logger.appInfo(bundle.getString("app.name"), bundle.getString("version.name"), bundle.getString("version.code"), bundle.getString("version.date"));		
	}

	@Override
	public void stop() throws Exception {
		Midi.getInstance().close();
	}
}