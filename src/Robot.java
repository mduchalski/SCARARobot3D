import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * This class encapsulates all robotic arm components and implements relevant
 * methods for manipulations.
 */
public class Robot extends Group {
    Box base;
    Cylinder baseExtension, effector;
    SmoothBox armInner, armOuter;
    double armInnerAngle;

    /**
     * Constructs a robot object based on given set of dimensions.
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

        // to-do: effector

        getChildren().addAll(base, baseExtension, armInner, armOuter);
    }

    public void rotateInner(double angle) {
        // rotate inner arm
        Rotate rotate = new Rotate(angle, Rotate.Y_AXIS);
        rotate.pivotXProperty().bind(armInner.getPivotX());
        rotate.pivotYProperty().bind(armInner.getPivotY());
        rotate.pivotZProperty().bind(armInner.getPivotZ());
        armInner.getTransforms().add(rotate);

        // translate outer arm to match
        armOuter.getTransforms().add(new Translate(
                armInner.getWidth() * (Math.cos(Math.toRadians(armInnerAngle+angle))
                        - Math.cos(Math.toRadians(armInnerAngle))), 0,
                -armInner.getWidth() * (Math.sin(Math.toRadians(armInnerAngle+angle))
                        - Math.sin(Math.toRadians(armInnerAngle)))));

        // keep track of the cumulative rotation angle
        armInnerAngle += angle;
        System.out.println(armInnerAngle);
    }

    public void rotateOuter(double angle) {
        Rotate rotate = new Rotate(angle, Rotate.Y_AXIS);
        rotate.pivotXProperty().bind(armOuter.getPivotX());
        rotate.pivotYProperty().bind(armOuter.getPivotY());
        rotate.pivotZProperty().bind(armOuter.getPivotZ());
        armOuter.getTransforms().add(rotate);
    }
}
