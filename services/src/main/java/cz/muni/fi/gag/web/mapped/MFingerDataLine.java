package cz.muni.fi.gag.web.mapped;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.muni.fi.gag.web.entity.FingerDataLine;
import cz.muni.fi.gag.web.service.GestureService;

/**
 * @author Vojtech Prusa
 *
 */
@JsonIgnoreProperties(value = {"gesture"})
public class MFingerDataLine extends FingerDataLine implements GestureMapper {

    protected Long gestureID;

    public Long getGestureID() {
        return gestureID;
    }

    public void setGestureID(Long gestureID) {
        this.gestureID = gestureID;
    }

    public FingerDataLine getEntity(GestureService gservice) {
        FingerDataLine fdl = new FingerDataLine();
        return GestureMapper.mapFingerDataLine(gservice, this, fdl);

    }
}