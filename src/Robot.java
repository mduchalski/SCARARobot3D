import javafx.animation.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;
import javafx.util.Duration;
import javafx.geometry.Point3D;
import javafx.beans.property.DoubleProperty;
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

    /**
     * Retrieves inner angle rotation property
     * @return property
     */
    public DoubleProperty innerAngleProperty() {
        return rotateInnerTr.angleProperty();
    }

    /**
     * Retrieves outer angle rotation property
     * @return property
     */
    public DoubleProperty outerAngleProperty() {
        return rotateOuterTr.angleProperty();
    }

    /**
     * Retrieves effector angle rotation property
     * @return property
     */
    public DoubleProperty effectorAngleProperty() {
        return rotateEffectorTr.angleProperty();
    }

    /**
     * Retrieves effector position translation property
     * @return property
     */
    public DoubleProperty effectorPosProperty() {
        return rotateEffectorGroup.translateYProperty();
    }

    /**
     * Checks whether or not the current robot position is legal with respect to
     * saved bounds and collision with the interactive box or the floor.
     * @param box interactive box
     * @param floor floor
     * @return true if it is, false otherwise
     */
    public boolean isPositionLegal(Box box, Box floor) {
        return  ((grabbedBox ==  null && !grabber.localToScene(grabber.getBoundsInLocal())
                        .intersects(box.localToScene(box.getBoundsInLocal()))) ||
                (grabbedBox != null && !grabbedBox.localToScene(grabbedBox.getBoundsInLocal())
                        .intersects(floor.localToScene(floor.getBoundsInLocal())))) &&
                Math.abs(rotateOuterTr.getAngle()) < maxOuterAngle &&
                Math.abs(rotateEffectorGroup.getTranslateY()) < maxEffectorMove;
    }

    /**
     * Checks whether or not a specified robot position is legal with respect to
     * saved bounds.
     * @param innerAngle inner arm angle
     * @param outerAngle outer arm angle
     * @param effectorAngle effector angle
     * @param effectorPos effector position
     * @return true if it is, false otherwise
     */
    public boolean isPositionLegal(double innerAngle, double outerAngle,
                                   double effectorAngle, double effectorPos) {
        return Math.abs(outerAngle) < maxOuterAngle && Math.abs(effectorPos) < maxEffectorMove;
    }

    /**
     * Attempts interactive box grab/lay down. This method can be called both
     * manually or when the recorder is playing.
     * @param box interactive box
     * @param boxRotate interactive box's rotation transform
     * @param floor floor
     * @param recorder recorder whose play function should be called when the
     *                 action is finished, null if not playing
     * @see Recorder#doPlay(Robot, Box, Rotate, Box)
     */
    public void attemptGrabLaydown(Box box, Rotate boxRotate,
                                   Box floor, Recorder recorder) {
        // attempt to grab the box
        if (grabbedBox == null && canGrab(box)) {
            // hide box on the ground
            box.setVisible(false);
            // setup and add new grabbed box
            grabbedBox = new Box(box.getWidth(), box.getHeight(), box.getDepth());
            grabbedBox.setMaterial(box.getMaterial());
            grabbedBox.setDrawMode(FILL);
            grabbedBox.setTranslateX(grabber.getTranslateX());
            grabbedBox.setTranslateY(grabber.getTranslateY() +
                    (grabbedBox.getHeight() + grabber.getHeight()) / 2.0);
            rotateEffectorGroup.getChildren().add(grabbedBox);
            // call doPlay - no animation here
            if (recorder != null)
                recorder.doPlay(this, box, boxRotate, floor);
        }
        // attempt to lay down the box
        else if (grabbedBox != null) {
            // retrieve and move box to current grabber coordinates
            Point3D grabberPos = grabber.localToScene(0, 0, 0);
            box.setTranslateX(grabberPos.getX());
            box.setTranslateY(grabberPos.getY() + grabber.getHeight()/2.0 + box.getHeight()/2.0);
            box.setTranslateZ(grabberPos.getZ());
            boxRotate.setAngle(getGrabberAngle());
            // animate box's fall to the ground
            animateFall(box, boxRotate, floor, recorder);
            // un-hide box on the ground and remove grabbed box
            box.setVisible(true);
            rotateEffectorGroup.getChildren().remove(grabbedBox);
            grabbedBox = null;
        }
        // call doPlay if attempt unsuccessful so that recorder playback doesn't hang up
        else if (recorder != null)
            recorder.doPlay(this, box, boxRotate, floor);
    }

    /**
     * Sets up and executed quasi-gravity fall of attached box to the ground.
     * @param box interactive box
     * @param boxRotate interactive box's rotation transform
     * @param floor floor
     * @param recorder recorder whose play function should be called when the
     *                 animation is finished, null if not playing
     */
    private void animateFall(Box box, Rotate boxRotate,
                             Box floor, Recorder recorder) {
        final Timeline fallAnimation = new Timeline();
        fallAnimation.setCycleCount(1);
        fallAnimation.setAutoReverse(false);

        // constant acceleration fall - quadratic relationship between time and
        // y-axis position...
        Interpolator gravity = new Interpolator() {
            @Override
            protected double curve(double t) {
                return t*t;
            }
        };
        //...and square root relationship between fall time and height
        fallAnimation.getKeyFrames().add(new KeyFrame(Duration.millis(
                750.0 * Math.sqrt(rotateEffectorGroup.getTranslateX() + maxEffectorMove)),
                new KeyValue(box.translateYProperty(),
                        - floor.getHeight() - box.getHeight() / 4.0, gravity)));

        // recorder playback - make it so that next action animated when fall
        // animation ends
        Robot thisRobot = this;
        if (recorder != null)
            fallAnimation.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    recorder.doPlay(thisRobot, box, boxRotate, floor);
                }
            });

        fallAnimation.play();
    }

    /**
     * Checks whether or not the robot can grab the box in its current position.
     * @param box interactive box
     * @return true if it can, false otherwise
     */
    private boolean canGrab(Box box) {
        // check distance between grabber's and box's nearest surfeces' center
        // points and relative rotation angle
        return box.localToScene(0.0, -box.getHeight() / 2.0, 0.0).distance(
                grabber.localToScene(0.0, grabber.getHeight() / 2.0, 0.0)) < 0.3;
    }

    /**
     * Get grabber rotation angle with respect to scene X-axis.
     * @return angle in degrees
     */
    private double getGrabberAngle() {
        return rotateEffectorTr.getAngle() + rotateInnerTr.getAngle() + rotateOuterTr.getAngle();
    }
}
