import javafx.beans.property.DoubleProperty;

public class DoublePropertyTarget {
    DoubleProperty property;
    Double targetVal;
    public double outerAngle, innerAngle, effectorAngle, effectorPos;

    public DoublePropertyTarget(DoubleProperty _property, Double _targetVal) {
        property = _property;
        targetVal = _targetVal;
    }

    public DoubleProperty getProperty() {
        return property;
    }

    public Double getTarget() {
        return targetVal;
    }
}
