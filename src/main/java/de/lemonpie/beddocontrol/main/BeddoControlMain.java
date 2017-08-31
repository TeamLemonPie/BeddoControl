package de.lemonpie.beddocontrol.main;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

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

public class BeddoControlMain extends Application
{
	private static final ResourceBundle bundle = ResourceBundle.getBundle("de/lemonpie/beddocontrol/resources/", Locale.GERMANY);	

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
			Parent root = (Parent)loader.load();

			Scene scene = new Scene(root, 800, 600);

			Image icon = new Image("/de/lemonpie/beddocontrol/resources/icon.png");
			((Controller)loader.getController()).init(primaryStage, icon, bundle);
			
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
	public void init() throws Exception
	{		
		Parameters params = getParameters();
		String logLevelParam = params.getNamed().get("loglevel");		
		Logger.setLevel(logLevelParam);	
		
		File logFolder = new File(PathUtils.getOSindependentPath() + "/LemonPie/" + bundle.getString("app.name"));			
		PathUtils.checkFolder(logFolder);
		Logger.enableFileOutput(logFolder, System.out, System.err, FileOutputMode.COMBINED);
		
		Logger.appInfo(bundle.getString("app.name"), bundle.getString("version.name"), bundle.getString("version.code"), bundle.getString("version.date"));		
	}
}