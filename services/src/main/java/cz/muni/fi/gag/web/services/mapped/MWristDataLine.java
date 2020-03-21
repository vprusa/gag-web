package cz.muni.fi.gag.web.services.mapped;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.muni.fi.gag.web.persistence.entity.WristDataLine;
import cz.muni.fi.gag.web.services.service.GestureService;

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