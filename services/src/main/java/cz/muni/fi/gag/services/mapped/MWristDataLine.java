package cz.muni.fi.gag.services.mapped;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.muni.fi.gag.services.service.GestureService;
import cz.muni.fi.gag.web.entity.WristDataLine;

/**
 * @author Vojtech Prusa
 *
 */
@JsonIgnoreProperties(value = {"gesture"})
public class MWristDataLine extends WristDataLine implements GestureMapper<WristDataLine> {

    @JsonProperty("gid")
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