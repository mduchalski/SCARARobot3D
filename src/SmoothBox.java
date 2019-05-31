import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

/**
 * This class implements a simple 3D object - a box ending with two cylinders,
 * as seen below viewed from the top.
 *     = = = = = =
 *   =             =
 *  =               =
 *  =               =
 *   =             =
 *     = = = = = =
 */
public class SmoothBox extends Group {
    Box mid;
    Cylinder left, right;

    /**
     * Constructs a SmoothBox object. Its width will be (cenToCent + depth).
     * @param centToCent distance between extreme cylinders' centers
     * @param height object height
     * @param depth object depth
     */
    public SmoothBox(double centToCent, double height, double depth) {
        super();
        mid = new Box(centToCent, height, depth);
        left = new Cylinder(depth / 2.0, height);
        right = new Cylinder(depth / 2.0, height);
        left.setTranslateX(-centToCent / 2.0);
        right.setTranslateX(centToCent / 2.0);
        getChildren().addAll(mid, left, right);
    }

    /**
     * Sets object's material, analogous to how this works for simple 3D shapes.
     * @param material material to set
     */
    public void setMaterial(Material material) {
        mid.setMaterial(material);
        left.setMaterial(material);
        right.setMaterial(material);
    }

    /**
     * Sets object's draw mode, analogous to how this works for simple 3D shapes.
     * @param drawMode draw mode to set
     */
    public void setDrawMode(DrawMode drawMode) {
        mid.setDrawMode(drawMode);
        left.setDrawMode(drawMode);
        right.setDrawMode(drawMode);
    }

    public double getCentToCent() {
        return mid.getWidth();
    }
}
