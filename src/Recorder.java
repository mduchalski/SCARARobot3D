import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This class implements robot movement recording and playback functionality.
 */
public class Recorder {
    Queue<DoublePropertyTarget> positions;
    boolean isRecording, isPlaying;
    Box startBox;

    public Recorder() {
        positions = new LinkedList<>();
        isRecording = false;
        isPlaying = false;
    }

    public void startRecording(Robot robot, Box _startBox) {
        if (!isPlaying) {
            isRecording = true;
            addPos(robot.outerAngleProperty());
            addPos(robot.innerAngleProperty());
            addPos(robot.effectorAngleProperty());
            addPos(robot.effectorPosProperty());
            startBox = _startBox;
        }
    }

    public void play(Robot robot, Box box, Rotate boxRotate, Box floor) {
        isPlaying = true;
        if (positions.isEmpty()) {
            isPlaying = false;
            return;
        }

        if (positions.peek() == null) {
            robot.attemptGrabLaydown(robot, box, boxRotate, floor, this);
            return;
        }

        double animationDur = Math.abs(positions.peek().getProperty().getValue() -
                positions.peek().getTarget());
        // different speeds for rotation and translation
        if (positions.peek().getProperty().getBean() instanceof Rotate)
            animationDur *= 25.0;
        else animationDur *= 2500.0;

        final Timeline animation = new Timeline();
        animation.setCycleCount(1);
        animation.setAutoReverse(false);
        animation.getKeyFrames().add(new KeyFrame(Duration.millis(
                animationDur), new KeyValue(positions.peek().getProperty(),
                        positions.poll().getTarget())));
        animation.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                play(robot, box, boxRotate, floor);
            }
        });
        animation.play();
    }

    public void stopAll() {
        isRecording = false;
        isPlaying = false;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void addPos(DoubleProperty property) {
        if (property != null)
            positions.add(new DoublePropertyTarget(property, property.getValue()));
        else positions.add(null);
    }

    //public void addGrablaydown() {
    //    positions.add(null); // null signifies box grab/lay down attempt
    //}
}
