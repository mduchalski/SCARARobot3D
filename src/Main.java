import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.transform.*;

/**
 * Main class.
 */
public class Main extends Application {
    Robot robot;

    /**
     * Initializes JavaFX application
     * @param primaryStage primary stage
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SCARA Robot Simulation");
        primaryStage.setResizable(false);
        HBox layout = new HBox(createControls(), createContent());
        Scene scene = new Scene(layout);
        handleKeyboard(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Initializes 3D content and returns a relevant Group layout.
     * @return group with 3D content, a Group object
     */
    private Group createContent() {
        // Box
        robot = new Robot(1.0, 0.25, 2.0, 1.5, 1.25, 0.25, 0.5, 0.125, 1.5,
                Color.DARKGRAY, Color.GREY);

        // create and position camera
        Camera camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll (
                new Rotate(-20, Rotate.Y_AXIS),
                new Rotate(-20, Rotate.X_AXIS),
                new Translate(0, 0, -15));

        // lights
        PointLight pLight = new PointLight(Color.WHITE);
        pLight.setTranslateX(50);
        pLight.setTranslateY(-300);
        pLight.setTranslateZ(-400);
        AmbientLight aLight = new AmbientLight(Color.color(0.3, 0.3, 0.3));

        // build the Scene Graph
        Group root = new Group();
        root.getChildren().addAll(camera, robot, pLight, aLight);

        // Use a SubScene
        SubScene subScene = new SubScene(root, 500,500, true, SceneAntialiasing.BALANCED);
        subScene.setCamera(camera);

        Group group = new Group();
        group.getChildren().add(subScene);
        return group;
    }

    /**
     * Initializes controls and returns a relevant GridPane layout.
     * @return layout with controls, a GridPane object
     */
    private GridPane createControls() {
        // GridPane object initialization
        GridPane controls = new GridPane();
        controls.setHgap(6.0);
        controls.setVgap(6.0);
        controls.setPadding(new Insets(8.0, 8.0, 8.0, 8.0));

        // controls initialization
        Label robotSettingsText = new Label("Sterowanie robotem");
        Label innerAngleLabel = new Label("Kąt ramienia wewn. [st.]:");
        Slider slider = new Slider();
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        TextField innerAngleField = new TextField();
        innerAngleField.setPrefWidth(40.0);
        Label outerAngleLabel = new Label("Kąt ramienia zewn. [st.]:");
        TextField outerAngleField = new TextField();
        outerAngleField.setPrefWidth(40.0);
        Label effectorAngleLabel = new Label("Kąt obrotu efektora [st.]:");
        TextField effectorAngleField = new TextField();
        effectorAngleField.setPrefWidth(40.0);
        Label effectorPosLabel = new Label("Przemieszczenie efektora:");
        TextField effectorPosField = new TextField();
        effectorPosField.setPrefWidth(40.0);
        Button set = new Button("Zatwierdź");
        Button reset = new Button("Resetuj");
        Label recordLabel = new Label("Nagrywanie ruchów");
        Button record = new Button("Nagrywaj");
        Button playPause = new Button("Odtwarzaj");
        Button stop = new Button("Zatrzymaj");

        // adding controls to GridPane
        controls.add(robotSettingsText, 0, 0, 3, 1);
        controls.add(slider, 0, 1, 3, 1);
        controls.add(innerAngleLabel, 0, 2, 2, 1);
        controls.add(innerAngleField, 2, 2, 1, 1);
        controls.add(outerAngleLabel, 0, 3, 2, 1);
        controls.add(outerAngleField, 2, 3, 1, 1);
        controls.add(effectorAngleLabel, 0, 4, 2, 1);
        controls.add(effectorAngleField, 2, 4, 1, 1);
        controls.add(effectorPosLabel, 0, 5, 2, 1);
        controls.add(effectorPosField, 2, 5, 1, 1);
        controls.add(reset, 1, 6, 1, 1);
        controls.add(set, 2, 6, 1, 1);
        controls.add(recordLabel, 0, 7, 3, 1);
        controls.add(record, 0, 8, 1, 1);
        controls.add(playPause, 1, 8, 1, 1);
        controls.add(stop, 2, 8, 1, 1);

        // alignment corrections
        GridPane.setHalignment(robotSettingsText, HPos.CENTER);
        GridPane.setHalignment(reset, HPos.RIGHT);
        GridPane.setHalignment(set, HPos.CENTER);
        GridPane.setHalignment(recordLabel, HPos.CENTER);

        return controls;
    }

    /**
     * Handles keyboard input.
     * @param scene active scene
     */
    private void handleKeyboard(Scene scene) {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case Q:
                        robot.rotateOuter(0.015);
                        break;
                    case A:
                        robot.rotateOuter(-0.015);
                        break;
                    case W:
                        robot.rotateInner(0.015);
                        break;
                    case S:
                        robot.rotateInner(-0.015);
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