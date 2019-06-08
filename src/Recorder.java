import javafx.animation.*;
import javafx.event.*;
import java.util.*;
import javafx.beans.property.DoubleProperty;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * This class implements robot movement recording and playback functionality.
 * @see DoublePropertyTarget
 */
public class Recorder {
    Queue<DoublePropertyTarget> positions;
    EventHandler<ActionEvent> onPlayFinished;
    boolean isRecording, isPlaying;
    double initialBoxTranslateX, initialBoxTranslateZ, initialBoxRotateAngle;

    /**
     * Initializes recorder object.
     */
    public Recorder() {
        positions = new LinkedList<>();
        isRecording = false;
        isPlaying = false;
    }

    /**
     * Starts recording. This method clears the internal action queue and
     * adds initial positions of both the arm and interactive box.
     * @param robot robot to record
     * @param box interactive box
     * @param boxRotate interactive box's rotate transform
     */
    public void doRecord(Robot robot, Box box, Rotate boxRotate) {
        isRecording = true;
        positions.clear();

        // save robot position
        addPos(robot.outerAngleProperty());
        addPos(robot.innerAngleProperty());
        addPos(robot.effectorAngleProperty());
        addPos(robot.effectorPosProperty());

        // save box position
        initialBoxTranslateX = box.getTranslateX();
        initialBoxTranslateZ = box.getTranslateZ();
        initialBoxRotateAngle = boxRotate.getAngle();
    }

    /**
     * Checks whether or not recorder is recording.
     * @return true if it is, false otherwise
     */
    public boolean isRecording() {
        return isRecording;
    }

    /**
     * Plays next recorded step. This method is intended to be called manually
     * once. In each call an animation is going to be set up and run in so that
     * this method will be run again until some action is present in the queue.
     * @param robot recorder robot
     * @param box interactive box
     * @param boxRotate interactive box's rotate transform
     * @param floor floor
     */
    public void doPlay(Robot robot, Box box, Rotate boxRotate, Box floor) {
        // initial call - move box to initial position
        if (!isPlaying) {
            box.setTranslateX(initialBoxTranslateX);
            box.setTranslateZ(initialBoxTranslateZ);
            boxRotate.setAngle(initialBoxRotateAngle);
            isPlaying = true;
        }

        // last call - update status variables and call the assigned handler
        if (positions.isEmpty()) {
            isPlaying = false;
            if (onPlayFinished != null)
                onPlayFinished.handle(new ActionEvent());
            return;
        }

        // null assigned DoubleProperty signifies grab/lay down attempt,
        // animation handed off to the robot
        if (positions.peek().getProperty() == null) {
            positions.poll();
            robot.attemptGrabLaydown(box, boxRotate, floor, this);
            return;
        }

        // set constant speed, different for rotation and translation
        double animationDur = Math.abs(positions.peek().getProperty().getValue() -
                positions.peek().getTarget());
        if (positions.peek().getProperty().getBean() instanceof Rotate)
            animationDur *= 25.0;
        else animationDur *= 2500.0;

        // setup and run playback step
        final Timeline animation = new Timeline();
        animation.setCycleCount(1);
        animation.setAutoReverse(false);
        animation.getKeyFrames().add(new KeyFrame(Duration.millis(
                animationDur), new KeyValue(positions.peek().getProperty(),
                        positions.poll().getTarget())));
        // run this function again for the next step when this animation is finished
        animation.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                doPlay(robot, box, boxRotate, floor);
            }
        });
        animation.play();
    }

    /**
     * Stops both recorder actions. Note that this method doesn't clear positions
     * queue, so it should only be called in normal operation.
     * @see Recorder#abortAll()
     */
    public void stopAll() {
        isRecording = false;
        isPlaying = false;
    }

    /**
     * Stops both recorder actions and clears positions queue. This method is
     * intended to be used when, for example, an exception is thrown.
     * @see Recorder#stopAll()
     */
    public void abortAll() {
        stopAll();
        positions.clear();
    }

    /**
     * Adds the current position of the given DoubleProperty to the action queue.
     * @param property positional/angle property to record
     * @see Recorder#addPos(DoubleProperty, double)
     */
    public void addPos(DoubleProperty property) {
        positions.add(new DoublePropertyTarget(property,
                (property == null) ? 0 : property.getValue()));
    }

    /**
     * Adds a specified position of the given DoubleProperty to the action queue.
     * @param property positional/angle property to record
     * @param target property's target value to record
     */
    public void addPos(DoubleProperty property, double target) {
        positions.add(new DoublePropertyTarget(property, target));
    }

    /**
     * Add EventHandler to call when playback is finished.
     * @param _onPlayFinished EventHandler to call
     */
    public void setOnPlayFinished(EventHandler<ActionEvent> _onPlayFinished) {
        onPlayFinished = _onPlayFinished;
    }
}
