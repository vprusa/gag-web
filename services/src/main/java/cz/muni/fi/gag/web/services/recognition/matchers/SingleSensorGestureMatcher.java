package cz.muni.fi.gag.web.services.recognition.matchers;

import cz.muni.fi.gag.web.persistence.entity.DataLine;
import cz.muni.fi.gag.web.persistence.entity.Gesture;

/**
 * @author Vojtech Prusa
 */
final public class SingleSensorGestureMatcher implements GestureMatcher {

    private Integer index;
    private Gesture g;
    private DataLine atDataLine;

    public SingleSensorGestureMatcher(Integer index, Gesture g) {
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

    /**
     * To remove gesture from recognized data, used last
     * TODO IDK, brainstorm?
     */
    public void clearG() {
        this.g = null;
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

    @Override
    public String toString() {
        return "GestureMatcher{" +
                "index=" + index +
                (g != null ?
                        ", g.id=" + g.getId() +
                                ", g.userAlias=" + g.getUserAlias()
                        : "") +
                ", atDataLine=" + atDataLine +
                '}';
    }

    public String toMinString() {
        return "GestureMatcher{" +
                "index=" + index +
//                ", g.id=" + g.getId() +
//                ", g.userAlias=" + g.getUserAlias() +
                ", atDataLine=" + atDataLine +
                '}';
    }
}
