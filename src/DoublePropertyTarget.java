import javafx.beans.property.DoubleProperty;

/**
 * This class is a simple wrapper around DoubleProperty with a single Double
 * value. It's used when recording.
 * @see Recorder
 */
public class DoublePropertyTarget {
    DoubleProperty property;
    Double targetVal;

    /**
     * Initializes a DoublePropertyTarget object.
     * @param _property property to wrap
     * @param _targetVal target value to associate with it
     */
    public DoublePropertyTarget(DoubleProperty _property, Double _targetVal) {
        property = _property;
        targetVal = _targetVal;
    }

    /**
     * Retrieves wrapped property.
     * @return property
     */
    public DoubleProperty getProperty() {
        return property;
    }

    /**
     * Retrieves associated target value.
     * @return target value
     */
    public Double getTarget() {
        return targetVal;
    }
}
