import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.transform.*;

import static javafx.scene.input.KeyCode.*;

/**
 * Main class.
 */
public class Main extends Application {
    Recorder recorder;
    Robot robot;
    Camera camera;
    Box box, floor;
    Rotate boxRotate;

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


        // robot
        robot = new Robot(1.0, 0.25, 2.0, 1.5, 1.25, 0.25, 0.5, 0.125, 2.0,
                0.675, 0.175, 120, 0.5, Color.DARKGRAY, Color.GREY);

        // recorder
        recorder = new Recorder();

        // floor
        floor = new Box(8.0, 0.1, 8.0);
        floor.setMaterial(new PhongMaterial(Color.WHITE));
        floor.setDrawMode(DrawMode.FILL);
        floor.setTranslateY(0.1);

        box = new Box(0.675, 0.675, 0.675);
        box.setMaterial(new PhongMaterial(Color.BLUE));
        box.setDrawMode(DrawMode.FILL);
        box.setTranslateX(2.75);
        box.setTranslateY(-0.25);
        boxRotate = new Rotate(0, Rotate.Y_AXIS);
        box.getTransforms().add(boxRotate);

        // create and position camera
        camera = new PerspectiveCamera(true);
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
        root.getChildren().addAll(camera, robot, floor, box, pLight, aLight);

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
        Button play = new Button("Odtwarzaj");
        Button stop = new Button("Zatrzymaj");

        // to-do: reorginize
        record.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                recorder.startRecording(robot, box, boxRotate);
            }
        });
        play.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                recorder.play(robot, box, boxRotate, floor);
            }
        });
        stop.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                recorder.stopAll();
            }
        });


        // adding controls to GridPane
        controls.add(robotSettingsText, 0, 0, 3, 1);
        controls.add(innerAngleLabel, 0, 1, 2, 1);
        controls.add(innerAngleField, 2, 1, 1, 1);
        controls.add(outerAngleLabel, 0, 2, 2, 1);
        controls.add(outerAngleField, 2, 2, 1, 1);
        controls.add(effectorAngleLabel, 0, 3, 2, 1);
        controls.add(effectorAngleField, 2, 3, 1, 1);
        controls.add(effectorPosLabel, 0, 4, 2, 1);
        controls.add(effectorPosField, 2, 4, 1, 1);
        controls.add(reset, 1, 5, 1, 1);
        controls.add(set, 2, 5, 1, 1);
        controls.add(recordLabel, 0, 6, 3, 1);
        controls.add(record, 0, 7, 1, 1);
        controls.add(play, 1, 7, 1, 1);
        controls.add(stop, 2, 7, 1, 1);

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
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (recorder.isRecording()) {
                    if (event.getCode() == Q || event.getCode() == A)
                        recorder.addPos(robot.outerAngleProperty());
                    else if (event.getCode() == W || event.getCode() == S)
                        recorder.addPos(robot.innerAngleProperty());
                    else if (event.getCode() == E || event.getCode() == D)
                        recorder.addPos(robot.effectorAngleProperty());
                    else if (event.getCode() == R || event.getCode() == F)
                        recorder.addPos(robot.effectorPosProperty());
                }
            }
        });

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == G) { // box grab/lay down
                    robot.attemptGrabLaydown(robot, box, boxRotate, floor, null);
                    if (recorder.isRecording()) recorder.addPos(null); // signifies grab/lay down attempt
                }
                else { // regular move
                    performMoveFromKeyboard(event, 1.0);
                    // undo move if new position not legal
                    if (!robot.isPositionLegal(box, floor))
                        performMoveFromKeyboard(event, -1.0);
                }
            }
        });
    }

    private void performMoveFromKeyboard(KeyEvent event, double mult) {
        switch (event.getCode()) {
            case Q:
                robot.rotateOuter(mult * 1);
                break;
            case A:
                robot.rotateOuter(mult * -1);
                break;
            case W:
                robot.rotateInner(mult * 1);
                break;
            case S:
                robot.rotateInner(mult * -1);
                break;
            case E:
                robot.rotateEffector(mult * 1);
                break;
            case D:
                robot.rotateEffector(mult * -1);
                break;
            case R:
                robot.moveEffector(mult * 0.01);
                break;
            case F:
                robot.moveEffector(mult * -0.01);
                break;
        }
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