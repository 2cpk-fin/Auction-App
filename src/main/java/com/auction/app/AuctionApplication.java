package com.auction.app;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AuctionApplication extends Application {

	private ConfigurableApplicationContext context;

	@Override
	public void init() {
		// Start Spring before the UI shows up
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()
				.load();

		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);

		this.context = new SpringApplicationBuilder()
				.sources(AuctionApplication.class)
				.web(WebApplicationType.NONE)
				.run();
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
