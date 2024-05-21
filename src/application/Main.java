package application;


import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@SuppressWarnings("exports")
	@Override
	public void start(Stage primaryStage) throws IOException {
		primaryStage.setTitle("AL-Attabi Stores");

		// Load and set the welcome scene

		Parent welcomeRoot = FXMLLoader.load(getClass().getResource("Welcomepage.fxml"));
		Scene welcomeScene = new Scene(welcomeRoot, 1536.0, 820.0);
		welcomeScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
		primaryStage.setScene(welcomeScene);

		// Show the stage
		primaryStage.show();

	}

}
