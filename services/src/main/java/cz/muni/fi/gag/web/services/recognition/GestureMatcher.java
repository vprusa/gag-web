package cz.muni.fi.gag.web.services.recognition;

import cz.muni.fi.gag.web.persistence.entity.DataLine;
import cz.muni.fi.gag.web.persistence.entity.Gesture;

import java.io.Serializable;

/**
 * @author Vojtech Prusa
 */
public class GestureMatcher implements Serializable {
    private Integer index;
    private Gesture g;
    private DataLine atDataLine;
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

    public <T extends DataLine> void setAtDataLine(T atDataLine) {
        this.atDataLine = atDataLine;
    }

    public DataLine getAtDataLine() {
        return atDataLine;
    }
}
