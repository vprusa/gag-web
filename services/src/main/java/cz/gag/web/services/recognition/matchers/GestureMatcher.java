package cz.gag.web.services.recognition.matchers;

import cz.gag.web.persistence.entity.Gesture;

import java.io.Serializable;

/**
 * @author Vojtech Prusa
 * <p>
 * {@link MultiSensorGestureMatcher}
 * {@link SingleSensorGestureMatcher}
 */
public interface GestureMatcher extends Serializable {
    Gesture getG();
}
