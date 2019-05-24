import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;
import javafx.stage.*;

public class Main extends Application {
    Box testBox;

    public Group createContent() throws Exception {
        // Box
        testBox = new Box(5,5,5);
        testBox.setMaterial(new PhongMaterial(Color.RED));
        testBox.setDrawMode(DrawMode.FILL);

        // Create and position camera
        Camera camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll (
                new Rotate(-20, Rotate.Y_AXIS),
                new Rotate(-20, Rotate.X_AXIS),
                new Translate(0, 0, -15));

        // Lights
        PointLight pLight = new PointLight(Color.WHITE);
        pLight.setTranslateX(50);
        pLight.setTranslateY(-300);
        pLight.setTranslateZ(-400);
        AmbientLight aLight = new AmbientLight(Color.color(0.3, 0.3, 0.3));

        // Build the Scene Graph
        Group root = new Group();
        root.getChildren().addAll(camera, testBox, pLight, aLight);

        // Use a SubScene
        SubScene subScene = new SubScene(root, 500,500);
        subScene.setFill(Color.ALICEBLUE);
        subScene.setCamera(camera);
        Group group = new Group();
        group.getChildren().add(subScene);
        return group;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setResizable(false);
        Scene scene = new Scene(createContent());
        handleKeyboard(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void handleKeyboard(Scene scene) {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case LEFT:
                        testBox.getTransforms().add(new Rotate(-1, Rotate.Y_AXIS));
                        break;
                    case RIGHT:
                        testBox.getTransforms().add(new Rotate(1, Rotate.Y_AXIS));
                        break;
                    case UP:
                        testBox.getTransforms().add(new Rotate(1, Rotate.X_AXIS));
                        break;
                    case DOWN:
                        testBox.getTransforms().add(new Rotate(-1, Rotate.X_AXIS));
                        break;
                }
            }
        });

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