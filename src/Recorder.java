import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import javax.swing.*;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class implements robot movement recording and playback functionality.
 */
public class Recorder {
    Queue<DoublePropertyTarget> positions;
    EventHandler<ActionEvent> onPlayFinished;
    boolean isRecording, isPlaying;
    double initialBoxTranslateX, initialBoxTranslateZ, initialBoxRotateAngle;

    public Recorder() {
        positions = new LinkedList<>();
        isRecording = false;
        isPlaying = false;
    }

    public void record(Robot robot, Box box, Rotate boxRotate) {
        isRecording = true;
        positions.clear();
        addPos(robot.outerAngleProperty());
        addPos(robot.innerAngleProperty());
        addPos(robot.effectorAngleProperty());
        addPos(robot.effectorPosProperty());
        initialBoxTranslateX = box.getTranslateX();
        initialBoxTranslateZ = box.getTranslateZ();
        initialBoxRotateAngle = boxRotate.getAngle();
    }

    public void play(Robot robot, Box box, Rotate boxRotate, Box floor) {
        if (!isPlaying) {
            box.setTranslateX(initialBoxTranslateX);
            box.setTranslateZ(initialBoxTranslateZ);
            boxRotate.setAngle(initialBoxRotateAngle);
            isPlaying = true;
        }

        if (positions.isEmpty()) {
            isPlaying = false;
            if (onPlayFinished != null)
                onPlayFinished.handle(new ActionEvent());
            return;
        }

        if (positions.peek() == null) {
            positions.poll();
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

    public void abortAll() {
        stopAll();
        positions.clear();
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void addPos(DoubleProperty property) {
        if (property != null)
            positions.add(new DoublePropertyTarget(property, property.getValue()));
        else positions.add(null);
    }

    public void addPos(DoubleProperty property, double target) {
        if (property != null)
            positions.add(new DoublePropertyTarget(property, target));
        else positions.add(null);
    }

    public void setOnPlayFinished(EventHandler<ActionEvent> _onPlayFinished) {
        onPlayFinished = _onPlayFinished;
    }
}
