import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;

/**
 * This class encapsulates all robotic arm components and implements relevant
 * methods for manipulations.
 */
public class Robot extends Group {
    Box base, grabber;
    Cylinder baseExtension, effector;
    SmoothBox armInner, armOuter;
    double armInnerAngle, armOuterAngle, effectorAngle;
    Group rotateInnerGroup, rotateOuterGroup, rotateEffectorGroup;

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
                 Color primaryCol, Color secondaryCol) {
        super();
        rotateInnerGroup = new Group();
        rotateOuterGroup = new Group();
        rotateEffectorGroup = new Group();
        PhongMaterial primary = new PhongMaterial(primaryCol),
                secondary = new PhongMaterial(secondaryCol);

        base = new Box(baseSide, baseHeight, baseSide);
        base.setMaterial(secondary); base.setDrawMode(DrawMode.FILL);
        baseExtension = new Cylinder(armDepth / 2.0, baseExtensionHeight);
        baseExtension.setTranslateY(-baseExtensionHeight / 2.0);
        baseExtension.setMaterial(primary); baseExtension.setDrawMode(DrawMode.FILL);
        armInner = new SmoothBox(armInnerLength, armHeight, armDepth);
        armInner.setMaterial(secondary); armInner.setDrawMode(DrawMode.FILL);
        armInner.setTranslateX(armInnerLength / 2.0);
        armInner.setTranslateY(-baseExtensionHeight - armHeight/2.0);
        armOuter = new SmoothBox(armOuterLength, armHeight, armDepth);
        armOuter.setMaterial(primary); armOuter.setDrawMode(DrawMode.FILL);
        armOuter.setTranslateX(armInnerLength + armOuterLength/2.0);
        armOuter.setTranslateY(-baseExtensionHeight + armHeight/2.0);
        armOuterAngle = armInnerAngle = 0.0;
        effector = new Cylinder(effectorRadius, effectorHeight);
        effector.setMaterial(secondary); effector.setDrawMode(DrawMode.FILL);
        effector.setTranslateX(armInnerLength + armOuterLength);
        effector.setTranslateY(-baseExtensionHeight);
        grabber = new Box(grabberSide, grabberHeight, grabberSide);
        grabber.setMaterial(primary); grabber.setDrawMode(DrawMode.FILL);
        grabber.setTranslateX(armInnerLength + armOuterLength);
        grabber.setTranslateY(-baseExtensionHeight + effectorHeight/2.0);

        rotateEffectorGroup.getChildren().addAll(effector, grabber);
        rotateOuterGroup.getChildren().addAll(armOuter, rotateEffectorGroup);
        rotateInnerGroup.getChildren().addAll(armInner, rotateOuterGroup);
        getChildren().addAll(base, baseExtension, rotateInnerGroup);
    }

    /**
     * Rotates the inner arm.
     * @param angle rotation angle
     */
    public void rotateInner(double angle) {
        armInnerAngle += angle;
        rotateInnerGroup.getTransforms().clear();
        rotateInnerGroup.getTransforms().add(new Rotate(Math.toDegrees(armInnerAngle),
                Rotate.Y_AXIS));
    }

    /**
     * Rotates the outer arm.
     * @param angle rotation angle
     */
    public void rotateOuter(double angle) {
        armOuterAngle += angle;
        rotateOuterGroup.getTransforms().clear();
        rotateOuterGroup.getTransforms().add(new Rotate(Math.toDegrees(armOuterAngle),
                armInner.getCentToCent(), 0.0, 0.0, Rotate.Y_AXIS));
    }

    /**
     * Rotates the outer arm.
     * @param angle rotation angle
     */
    public void rotateEffector(double angle) {
        effectorAngle += angle;
        rotateEffectorGroup.getTransforms().clear();
        rotateEffectorGroup.getTransforms().add(new Rotate(Math.toDegrees(effectorAngle),
                armInner.getCentToCent() + armOuter.getCentToCent(),
                0.0, 0.0, Rotate.Y_AXIS));
    }

    /**
     * Moves the effector up or down.
     * @param dist distance (+/-) to move the effector
     */
    public void moveEffector(double dist) {
        rotateEffectorGroup.setTranslateY(rotateEffectorGroup.getTranslateY() + dist);
    }
}
