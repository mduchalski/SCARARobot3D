import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;
import javafx.stage.*;

public class Main extends Application {
    Robot robot;

    public Group createContent() throws Exception {
        // Box
        robot = new Robot(1.0, 0.25, 2.0, 1.5, 1.25, 0.25, 0.5, 0.125, 1.5,
                Color.DARKGRAY, Color.GREY);

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
        root.getChildren().addAll(camera, robot, pLight, aLight);

        // Use a SubScene
        SubScene subScene = new SubScene(root, 500,500, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.WHITE);
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
                    case Q:
                        robot.rotateOuter(0.01);
                        break;
                    case A:
                        robot.rotateOuter(-0.01);
                        break;
                    case W:
                        robot.rotateInner(0.01);
                        break;
                    case S:
                        robot.rotateInner(-0.01);
                        break;
                    case E:
                        robot.moveEffector(0.01);
                        break;
                    case D:
                        robot.moveEffector(-0.01);
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