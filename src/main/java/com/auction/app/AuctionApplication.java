package com.auction.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AuctionApplication extends Application {

	private ConfigurableApplicationContext context;

	@Override
	public void init() {
		// Start Spring before the UI shows up
		this.context = SpringApplication.run(AuctionApplication.class);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));

		// This is CRITICAL: It tells JavaFX to use Spring to create the Controller
		loader.setControllerFactory(context::getBean);

		Parent root = loader.load();
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("Auction App");
		primaryStage.show();
	}

	@Override
	public void stop() {
		context.close();
	}

	public static void main(String[] args) {
		Application.launch(AuctionApplication.class, args);
	}

}
