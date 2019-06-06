import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;
import javafx.util.Duration;

import static javafx.scene.shape.DrawMode.FILL;

/**
 * This class encapsulates all robotic arm components and implements relevant
 * methods for manipulations.
 */
public class Robot extends Group {
    Rotate rotateInnerTr, rotateOuterTr, rotateEffectorTr;
    Group rotateEffectorGroup;
    Box grabber, grabbedBox;
    double maxOuterAngle, maxEffectorMove;

    /**
     * Constructs a Robot object with a given set of dimensions.
     * @param baseSide base side length
     * @param baseHeight base height
     * @param baseExtensionHeight base extension (fixed cylinder) height
     * @param armInnerLength length of the inner arm
     * @param armOuterLength length of the outer arm
     * @param armHeight arm height
     * @param armDepth arm depth
     * @param effectorRadius effector radius
     * @param effectorHeight effector height
     * @param primaryCol primary color, used for the base extension and outer arm
     * @param secondaryCol secondary color, used for the base, inner arm an effector
     */
    public Robot(double baseSide, double baseHeight,
                 double baseExtensionHeight, double armInnerLength,
                 double armOuterLength, double armHeight, double armDepth,
                 double effectorRadius, double effectorHeight,
                 double grabberSide, double grabberHeight,
                 double _maxOuterAngle, double _maxEffectorMove,
                 Color primaryCol, Color secondaryCol) {
        super();
        Group rotateInnerGroup = new Group();
        Group rotateOuterGroup = new Group();
        rotateEffectorGroup = new Group();
        PhongMaterial primary = new PhongMaterial(primaryCol),
                secondary = new PhongMaterial(secondaryCol);

        Box base = new Box(baseSide, baseHeight, baseSide);
        base.setMaterial(secondary); base.setDrawMode(FILL);
        Cylinder baseExtension = new Cylinder(armDepth / 2.0, baseExtensionHeight);
        baseExtension.setTranslateY(-baseExtensionHeight / 2.0);
        baseExtension.setMaterial(primary); baseExtension.setDrawMode(FILL);
        SmoothBox armInner = new SmoothBox(armInnerLength, armHeight, armDepth);
        armInner.setMaterial(secondary); armInner.setDrawMode(FILL);
        armInner.setTranslateX(armInnerLength / 2.0);
        armInner.setTranslateY(-baseExtensionHeight - armHeight/2.0);
        SmoothBox armOuter = new SmoothBox(armOuterLength, armHeight, armDepth);
        armOuter.setMaterial(primary); armOuter.setDrawMode(FILL);
        armOuter.setTranslateX(armInnerLength + armOuterLength/2.0);
        armOuter.setTranslateY(-baseExtensionHeight + armHeight/2.0);
        Cylinder effector = new Cylinder(effectorRadius, effectorHeight);
        effector.setMaterial(secondary); effector.setDrawMode(FILL);
        effector.setTranslateX(armInnerLength + armOuterLength);
        effector.setTranslateY(-baseExtensionHeight);
        grabber = new Box(grabberSide, grabberHeight, grabberSide);
        grabber.setMaterial(primary); grabber.setDrawMode(FILL);
        grabber.setTranslateX(armInnerLength + armOuterLength);
        grabber.setTranslateY(-baseExtensionHeight + effectorHeight/2.0);

        rotateInnerTr = new Rotate(0.0, Rotate.Y_AXIS);
        rotateInnerGroup.getTransforms().add(rotateInnerTr);
        rotateOuterTr = new Rotate(0.0, armInner.getCentToCent(),
                0.0, 0.0, Rotate.Y_AXIS);
        rotateOuterGroup.getTransforms().add(rotateOuterTr);
        rotateEffectorTr = new Rotate(0.0, armInner.getCentToCent() +
                armOuter.getCentToCent(), 0.0, 0.0, Rotate.Y_AXIS);
        rotateEffectorGroup.getTransforms().add(rotateEffectorTr);

        rotateEffectorGroup.getChildren().addAll(effector, grabber);
        rotateOuterGroup.getChildren().addAll(armOuter, rotateEffectorGroup);
        rotateInnerGroup.getChildren().addAll(armInner, rotateOuterGroup);
        getChildren().addAll(base, baseExtension, rotateInnerGroup);

        maxOuterAngle = _maxOuterAngle;
        maxEffectorMove = _maxEffectorMove;
        grabbedBox = null;
    }

    /**
     * Rotates the inner arm.
     * @param angle rotation angle
     */
    public void rotateInner(double angle) {
        rotateInnerTr.setAngle(rotateInnerTr.getAngle() + angle);
    }

    /**
     * Rotates the outer arm.
     * @param angle rotation angle
     */
    public void rotateOuter(double angle) {
        rotateOuterTr.setAngle(rotateOuterTr.getAngle() + angle);
    }

    /**
     * Rotates the outer arm.
     * @param angle rotation angle
     */
    public void rotateEffector(double angle) {
        rotateEffectorTr.setAngle(rotateEffectorTr.getAngle() + angle);
    }

    /**
     * Moves the effector up or down.
     * @param dist distance (+/-) to move the effector
     */
    public void moveEffector(double dist) {
        rotateEffectorGroup.setTranslateY(rotateEffectorGroup.getTranslateY() + dist);
    }

    public DoubleProperty innerAngleProperty() {
        return rotateInnerTr.angleProperty();
    }

    public DoubleProperty outerAngleProperty() {
        return rotateOuterTr.angleProperty();
    }

    public DoubleProperty effectorAngleProperty() {
        return rotateEffectorTr.angleProperty();
    }

    public DoubleProperty effectorPosProperty() {
        return rotateEffectorGroup.translateYProperty();
    }

    public boolean isPositionLegal(Box box, Box floor) {
        return  ((grabbedBox ==  null && !grabber.localToScene(grabber.getBoundsInLocal())
                        .intersects(box.localToScene(box.getBoundsInLocal()))) ||
                (grabbedBox != null && !grabbedBox.localToScene(grabbedBox.getBoundsInLocal())
                        .intersects(floor.localToScene(floor.getBoundsInLocal())))) &&
                Math.abs(rotateOuterTr.getAngle()) < maxOuterAngle &&
                Math.abs(rotateEffectorGroup.getTranslateY()) < maxEffectorMove;
    }

    public boolean isPositionLegal(double innerAngle, double outerAngle,
                                   double effectorAngle, double effectorPos) {
        return Math.abs(outerAngle) < maxOuterAngle && Math.abs(effectorPos) < maxEffectorMove;
    }

    public void attemptGrabLaydown(Robot robot, Box box, Rotate boxRotate,
                                   Box floor, Recorder recorder) {
        if (grabbedBox == null && canGrab(box)) {
            box.setVisible(false);
            grabbedBox = new Box(box.getWidth(), box.getHeight(), box.getDepth());
            grabbedBox.setMaterial(box.getMaterial());
            grabbedBox.setDrawMode(FILL);
            grabbedBox.setTranslateX(grabber.getTranslateX());
            grabbedBox.setTranslateY(grabber.getTranslateY() +
                    (grabbedBox.getHeight() + grabber.getHeight()) / 2.0);
            rotateEffectorGroup.getChildren().add(grabbedBox);
            if (recorder != null) recorder.play(robot, box, boxRotate, floor);
        }
        else if (grabbedBox != null) { // to-do: correct angle
            Point3D grabberPos = grabber.localToScene(0, 0, 0);
            box.setTranslateX(grabberPos.getX());
            box.setTranslateY(grabberPos.getY() + grabber.getHeight()/2.0 + box.getHeight()/2.0);
            box.setTranslateZ(grabberPos.getZ());
            boxRotate.setAngle(getGrabberAngle());
            animateFall(robot, box, boxRotate, floor, recorder);
            System.out.println(getGrabberAngle());
            box.setVisible(true);
            rotateEffectorGroup.getChildren().remove(grabbedBox);
            grabbedBox = null;
        }
    }

    private void animateFall(Robot robot, Box box, Rotate boxRotate,
                             Box floor, Recorder recorder) {
        final Timeline fallAnimation = new Timeline();
        fallAnimation.setCycleCount(1);
        fallAnimation.setAutoReverse(false);
        Interpolator gravity = new Interpolator() {
            @Override
            protected double curve(double t) {
                return t*t;
            }
        };
        fallAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(
                750.0 * Math.sqrt(rotateEffectorGroup.getTranslateX() + maxEffectorMove)),
                new KeyValue(box.translateYProperty(),
                        - floor.getHeight() - box.getHeight() / 4.0, gravity)));
        if (recorder != null) // signified automatic operation
            fallAnimation.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    recorder.play(robot, box, boxRotate, floor);
                }
            });

        fallAnimation.play();
    }

    private boolean canGrab(Box box) { // to-do: angles
        return box.localToScene(0.0, -box.getHeight() / 2.0, 0.0).distance(
                grabber.localToScene(0.0, grabber.getHeight() / 2.0, 0.0)) < 0.5;
    }

    public double getGrabberAngle() {
        return rotateEffectorTr.getAngle() + rotateInnerTr.getAngle() + rotateOuterTr.getAngle();
    }
}
