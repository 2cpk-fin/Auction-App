package com.auction.app;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AuctionApplication extends Application {

	private ConfigurableApplicationContext context;

	@Override
	public void init() {
		// Load .env variables into system properties before Spring starts
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()
				.load();

		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);

		// Boot Spring without a web server
		this.context = new SpringApplicationBuilder()
				.sources(AuctionApplication.class)
				.web(WebApplicationType.NONE)
				.run();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Update the target FXML to the consolidated AuthView
		var url = getClass().getResource("/fxml/AuthView.fxml");
		if (url == null) {
			throw new IllegalStateException(
					"Cannot find /fxml/AuthView.fxml on the classpath. " +
							"Make sure the file is in src/main/resources/fxml/AuthView.fxml"
			);
		}
		FXMLLoader loader = new FXMLLoader(url);

		// Let Spring create and inject all controllers (@Autowired, @RequiredArgsConstructor, etc.)
		loader.setControllerFactory(context::getBean);

		Parent root = loader.load();

		// Update the window title to reflect the new branding
		primaryStage.setTitle("BidVault — Authentication");
		primaryStage.setScene(new Scene(root));
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