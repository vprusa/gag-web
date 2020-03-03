package cz.muni.fi.gag.web.validation;

import cz.muni.fi.gag.web.entity.Sensor;
import cz.muni.fi.gag.web.entity.FingerSensorOffset;
import cz.muni.fi.gag.web.entity.SensorOffset;
import cz.muni.fi.gag.web.entity.WristSensorOffset;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

/**
 * @author Patrik Novak.
 */
public class ValidHandOffsetsValidator implements ConstraintValidator<ValidHandOffsets, List<? extends SensorOffset>> {

    private static final int MAX_OFFSET_COUNT = 6;

    @Override
    public void initialize(ValidHandOffsets constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<? extends SensorOffset> annotatedList, ConstraintValidatorContext context) {
        if (annotatedList == null || annotatedList.isEmpty() || annotatedList.size() > MAX_OFFSET_COUNT) {
            return false;
        }

        boolean hasWristOffset = false;
        int otherOffsetCount = 0;
        int[] fingersCount = new int[Sensor.values().length];
        Arrays.fill(fingersCount, 0);

        for (SensorOffset offset : annotatedList) {
            Class<? extends SensorOffset> offsetClass = offset.getClass();

            if (offsetClass.equals(WristSensorOffset.class)) {
                if (hasWristOffset){
                    return false;
                }
                hasWristOffset = true;
            } else if (offsetClass.equals(FingerSensorOffset.class)) {
                FingerSensorOffset fingerOffset = (FingerSensorOffset) offset;
                fingersCount[fingerOffset.getPosition().ordinal()]++;
            } else {
                otherOffsetCount++;
            }
        }

        return hasWristOffset
                && Arrays.stream(fingersCount).allMatch(fingerCount -> fingerCount <= 1)
                && otherOffsetCount == 0;
    }
}
