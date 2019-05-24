import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Main extends Application {

    final Group root = new Group();

    @Override
    public void start(Stage primaryStage) {

        Scene scene = new Scene(root, 1024, 768, true);
        scene.setFill(Color.GREY);

        primaryStage.setTitle("SCARA Robotic Arm");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /**
     * The main() method is ignored in correctly deployed JavaFX
     * application. main() serves only as fallback in case the
     * application can not be launched through deployment artifacts,
     * e.g., in IDEs with limited FX support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}