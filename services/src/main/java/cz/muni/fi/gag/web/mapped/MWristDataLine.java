package cz.muni.fi.gag.web.mapped;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.muni.fi.gag.web.entity.WristDataLine;
import cz.muni.fi.gag.web.service.GestureService;

/**
 * @author Vojtech Prusa
 *
 */
@JsonIgnoreProperties(value = {"gesture"})
public class MWristDataLine extends WristDataLine implements GestureMapper {

    protected Long gestureID;

    public Long getGestureID() {
        return gestureID;
    }

    public void setGestureID(Long gestureID) {
        this.gestureID = gestureID;
    }

    public WristDataLine getEntity(GestureService gservice) {
        WristDataLine fdl = new WristDataLine();
        return GestureMapper.mapWristDataLine(gservice, this, fdl);
    }
}