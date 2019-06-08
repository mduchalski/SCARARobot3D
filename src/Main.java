import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.shape.*;
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
    // 3D objects
    Recorder recorder;
    Robot robot;
    Box box, floor;
    // 3D transformations
    Rotate boxRotate, cameraXRotate, cameraYRotate;
    Translate cameraTranslate;
    // camera and controls
    Camera camera;
    double mousePosX, mousePosY, mouseOldX, mouseOldY;
    // UI controls
    TextField innerAngleField, outerAngleField, effectorAngleField, effectorPosField;
    Button record, play, stop, set, reset;

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
        handleMouse(scene);
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

        // interactive box
        box = new Box(0.675, 0.675, 0.675);
        box.setMaterial(new PhongMaterial(Color.BLUE));
        box.setDrawMode(DrawMode.FILL);
        box.setTranslateX(2.75);
        box.setTranslateY(-0.25);
        boxRotate = new Rotate(0, Rotate.Y_AXIS);
        box.getTransforms().add(boxRotate);

        // camera setup
        camera = new PerspectiveCamera(true);
        cameraYRotate = new Rotate(-45.0, Rotate.Y_AXIS);
        cameraXRotate = new Rotate(-30.0, Rotate.X_AXIS);
        cameraTranslate = new Translate(0.0, 0.0, -15.0);
        camera.getTransforms().addAll(cameraYRotate, cameraXRotate, cameraTranslate);

        // lights
        PointLight pLight = new PointLight(Color.WHITE);
        pLight.setTranslateX(50);
        pLight.setTranslateY(-300);
        pLight.setTranslateZ(-400);
        AmbientLight aLight = new AmbientLight(Color.color(0.3, 0.3, 0.3));

        // misc other setup and return group
        Group root = new Group();
        root.getChildren().addAll(camera, robot, floor, box, pLight, aLight);
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
        innerAngleField = new TextField("0");
        innerAngleField.setPrefWidth(40.0);
        Label outerAngleLabel = new Label("Kąt ramienia zewn. [st.]:");
        outerAngleField = new TextField("0");
        outerAngleField.setPrefWidth(40.0);
        Label effectorAngleLabel = new Label("Kąt obrotu efektora [st.]:");
        effectorAngleField = new TextField("0");
        effectorAngleField.setPrefWidth(40.0);
        Label effectorPosLabel = new Label("Przemieszczenie efektora:");
        effectorPosField = new TextField("0");
        effectorPosField.setPrefWidth(40.0);
        set = new Button("Zatwierdź");
        reset = new Button("Resetuj");
        Label recordLabel = new Label("Nagrywanie ruchów");
        record = new Button("Nagrywaj");
        play = new Button("Odtwarzaj");
        stop = new Button("Zatrzymaj");

        // handle control events
        handleControls();

        // adding controls to GridPane
        controls.add(recordLabel, 0, 0, 3, 1);
        controls.add(record, 0, 1, 1, 1);
        controls.add(play, 1, 1, 1, 1);
        controls.add(stop, 2, 1, 1, 1);
        controls.add(robotSettingsText, 0, 2, 3, 1);
        controls.add(innerAngleLabel, 0, 3, 2, 1);
        controls.add(innerAngleField, 2, 3, 1, 1);
        controls.add(outerAngleLabel, 0, 4, 2, 1);
        controls.add(outerAngleField, 2, 4, 1, 1);
        controls.add(effectorAngleLabel, 0, 5, 2, 1);
        controls.add(effectorAngleField, 2, 5, 1, 1);
        controls.add(effectorPosLabel, 0, 6, 2, 1);
        controls.add(effectorPosField, 2, 6, 1, 1);
        controls.add(reset, 1, 7, 1, 1);
        controls.add(set, 2, 7, 1, 1);

        // alignment corrections
        GridPane.setHalignment(robotSettingsText, HPos.CENTER);
        GridPane.setHalignment(reset, HPos.RIGHT);
        GridPane.setHalignment(set, HPos.CENTER);
        GridPane.setHalignment(recordLabel, HPos.CENTER);

        return controls;
    }

    /**
     * Handles UI controls-related events.
     * @see Main#createControls()
     */
    private void handleControls() {
        record.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                recorder.doRecord(robot, box, boxRotate);
                play.setDisable(true);
                record.setDisable(true);
                set.setDisable(true);
            }
        });
        play.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                recorder.doPlay(robot, box, boxRotate, floor);
                play.setDisable(true);
                record.setDisable(true);
                stop.setDisable(true);
                set.setDisable(true);
                recorder.setOnPlayFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        play.setDisable(false);
                        record.setDisable(false);
                        stop.setDisable(false);
                        set.setDisable(false);
                        recorder.setOnPlayFinished(null);
                    }
                });
            }
        });
        stop.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                recorder.stopAll();
                play.setDisable(false);
                record.setDisable(false);
                set.setDisable(false);
            }
        });
        reset.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                innerAngleField.setText("0");
                outerAngleField.setText("0");
                effectorAngleField.setText("0");
                effectorPosField.setText("0");
            }
        });
        set.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                recorder.doRecord(robot, box, boxRotate);
                try {
                    double innerAngleTarget = Double.parseDouble(innerAngleField.getText()),
                            outerAngleTarget = Double.parseDouble(outerAngleField.getText()),
                            effectorAngleTarget = Double.parseDouble(effectorAngleField.getText()),
                            effectorPosTarget = Double.parseDouble(effectorPosField.getText());
                    if (!robot.isPositionLegal(innerAngleTarget, outerAngleTarget,
                            effectorAngleTarget, effectorPosTarget))
                        throw new Exception();
                    recorder.addPos(robot.innerAngleProperty(), innerAngleTarget);
                    recorder.addPos(robot.outerAngleProperty(), outerAngleTarget);
                    recorder.addPos(robot.effectorAngleProperty(), effectorAngleTarget);
                    recorder.addPos(robot.effectorPosProperty(), effectorPosTarget);
                } catch (Exception e) {
                    recorder.abortAll();
                    innerAngleField.setText("Błąd!"); outerAngleField.clear();
                    effectorAngleField.clear(); effectorPosField.clear();
                }
                recorder.doPlay(robot, box, boxRotate, floor);
                play.setDisable(true);
                record.setDisable(true);
                stop.setDisable(true);
                set.setDisable(true);
                recorder.setOnPlayFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        play.setDisable(false);
                        record.setDisable(false);
                        stop.setDisable(false);
                        set.setDisable(false);
                        recorder.setOnPlayFinished(null);
                    }
                });
            }
        });
    }

    /**
     * Handles mouse input for camera control.
     * @param scene active scene
     */
    private void handleMouse(Scene scene) {
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                mousePosX = mouseOldX = e.getSceneX();
                mousePosY = mouseOldY = e.getSceneY();
                if (e.isSecondaryButtonDown()) {
                    cameraYRotate.setAngle(-45.0);
                    cameraXRotate.setAngle(-30.0);
                    cameraTranslate.setZ(-15.0);
                }
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = e.getSceneX();
                mousePosY = e.getSceneY();

                if (e.isPrimaryButtonDown()) {
                    cameraYRotate.setAngle(cameraYRotate.getAngle() + 0.1*(mousePosX - mouseOldX));
                    if (cameraXRotate.getAngle() - 0.1*(mousePosY - mouseOldY) < 0.0)
                        cameraXRotate.setAngle(cameraXRotate.getAngle() - 0.1*(mousePosY - mouseOldY));
                }
                else if (e.isMiddleButtonDown()) {
                    cameraTranslate.setZ(cameraTranslate.getZ() - 0.01*(mousePosY - mouseOldY));
                }
            }
        });
    }

    /**
     * Handles keyboard input for robotic arm control.
     * @param scene active scene
     */
    private void handleKeyboard(Scene scene) {
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == G)
                    robot.attemptGrabLaydown(box, boxRotate, floor, null);

                if (recorder.isRecording()) {
                    if (event.getCode() == Q || event.getCode() == A)
                        recorder.addPos(robot.outerAngleProperty());
                    else if (event.getCode() == W || event.getCode() == S)
                        recorder.addPos(robot.innerAngleProperty());
                    else if (event.getCode() == E || event.getCode() == D)
                        recorder.addPos(robot.effectorAngleProperty());
                    else if (event.getCode() == R || event.getCode() == F)
                        recorder.addPos(robot.effectorPosProperty());
                    else if (event.getCode() == G)
                        recorder.addPos(null);
                }
            }
        });

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                performMoveFromKeyboard(event, 1.0);
                // undo move if new position not legal
                if (!robot.isPositionLegal(box, floor))
                    performMoveFromKeyboard(event, -1.0);
            }
        });
    }

    /**
     * Handles keyboard-initiated robot moves. Multiplier can be specified for
     * speed changes and action reversal.
     * @param event keyboard event
     * @param mult specified rotation/move multiplier
     * @see Main#handleKeyboard(Scene)
     */
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