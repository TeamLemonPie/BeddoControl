package de.lemonpie.beddocontrol.ui;

import de.tobias.utils.nui.NVC;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ModalController extends NVC
{
	@FXML
	private Label labelMessage;

	public ModalController(Window owner, StringProperty message, String title)
	{
		load("de/lemonpie/beddocontrol/ui", "Modal");
		applyViewControllerToStage().initOwner(owner).initModality(Modality.WINDOW_MODAL);

		labelMessage.textProperty().bind(message);

		getStageContainer().ifPresent(container -> container.getStage().setTitle(title));
	}

	@Override
	public void initStage(Stage stage)
	{
		stage.setOnCloseRequest(Event::consume);
		stage.setResizable(false);
		stage.getIcons().add(ImageHandler.getIcon());
	}
}