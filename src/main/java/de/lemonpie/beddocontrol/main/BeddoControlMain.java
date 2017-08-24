package de.lemonpie.beddocontrol.main;

import de.lemonpie.beddocontrol.model.Player;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class BeddoControlMain extends Application {

    private static List<Player> players = new ArrayList<>();

	public static void main(String[] args) {
		launch(args);
	}

    public void start(Stage primaryStage) throws Exception {

    }

    public static void addPlayer(Player player) {
        players.add(player);
    }
}
