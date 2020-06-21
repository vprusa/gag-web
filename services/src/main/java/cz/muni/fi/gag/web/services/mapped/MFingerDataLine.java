package cz.muni.fi.gag.web.services.mapped;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.muni.fi.gag.web.services.service.GestureService;
import cz.muni.fi.gag.web.persistence.entity.FingerDataLine;

/**
 * @author Vojtech Prusa
 *
 */
@JsonIgnoreProperties(value = {"gesture"})
public class MFingerDataLine extends FingerDataLine implements GestureMapper<FingerDataLine> {

    @JsonProperty("gid")
    protected Long gestureID;

    public Long getGestureID() {
        return gestureID;
    }

    public void setGestureID(Long gestureID) {
        this.gestureID = gestureID;
    }

    public FingerDataLine getEntity(GestureService gservice) {
        // TODO dela with destruction or avoid init at all?
        // change DB
        FingerDataLine fdl = new FingerDataLine();
        return GestureMapper.mapFingerDataLine(gservice, this, fdl);

    }
}