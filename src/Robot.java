import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;

/**
 * This class encapsulates all robotic arm components and implements relevant
 * methods for manipulations.
 */
public class Robot extends Group {
    Box base;
    Cylinder baseExtension, effector;
    SmoothBox armInner, armOuter;
    double armInnerAngle, armOuterAngle;

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
                 Color primaryCol, Color secondaryCol) {
        super();
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

        getChildren().addAll(base, baseExtension, armInner, armOuter, effector);
    }

    /**
     * Rotates the inner arm.
     * @param angle rotation angle
     */
    public void rotateInner(double angle) {
        // rotate inner arm
        armInnerAngle += angle;
        Rotate rotate = new Rotate(Math.toDegrees(armInnerAngle), Rotate.Y_AXIS);
        rotate.pivotXProperty().bind(armInner.getPivotX());
        rotate.pivotYProperty().bind(armInner.getPivotY());
        rotate.pivotZProperty().bind(armInner.getPivotZ());
        armInner.getTransforms().clear();
        armInner.getTransforms().add(rotate);

        // translate outer arm to match
        matchRotation(armOuter, armInner.getCentToCent(), armInnerAngle, angle);
        matchRotation(effector, armInner.getCentToCent(), armInnerAngle, angle);
    }

    /**
     * Rotates the outer arm.
     * @param angle rotation angle
     */
    public void rotateOuter(double angle) {
        armOuterAngle += angle;
        Rotate rotate = new Rotate(Math.toDegrees(armOuterAngle), Rotate.Y_AXIS);
        rotate.pivotXProperty().bind(armOuter.getPivotX());
        rotate.pivotYProperty().bind(armOuter.getPivotY());
        rotate.pivotZProperty().bind(armOuter.getPivotZ());
        armOuter.getTransforms().clear();
        armOuter.getTransforms().add(rotate);

        matchRotation(effector, armOuter.getCentToCent(), armOuterAngle, angle);
    }

    /**
     * Moves the effector up or down.
     * @param dist distance (+/-) to move the effector
     */
    public void moveEffector(double dist) {
        effector.setTranslateY(effector.getTranslateY() + dist);
    }

    /**
     * Translates a given node to match another node's rotation.
     * @param node node to translate
     * @param radius rotation radius
     * @param angle angle before rotation
     * @param angleChange angle change in rotation
     */
    private void matchRotation(Node node, double radius,
                               double angle, double angleChange) {
        node.setTranslateX(node.getTranslateX() + radius * (Math.cos(angle) -
                Math.cos(angle-angleChange)));
        node.setTranslateZ(node.getTranslateZ() - radius * (Math.sin(angle) -
                Math.sin(angle-angleChange)));
    }
}
