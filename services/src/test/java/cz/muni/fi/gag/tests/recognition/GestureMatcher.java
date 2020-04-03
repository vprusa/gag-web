package cz.muni.fi.gag.tests.recognition;

import cz.muni.fi.gag.web.persistence.entity.Gesture;

/**
 * @author Vojtech Prusa
 */
public class GestureMatcher {
    private Integer index;
    private Gesture g;
    // TODO
//    private double matchedAccuracy;

    public GestureMatcher(Integer index, Gesture g) {
        this.index = index;
        this.g = g;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Gesture getG() {
        return g;
    }

    public void setG(Gesture g) {
        this.g = g;
    }

    public void incIndex() {
        this.index++;
    }
}
