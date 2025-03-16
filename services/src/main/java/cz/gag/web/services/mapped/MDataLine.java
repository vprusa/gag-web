package cz.gag.web.services.mapped;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.gag.web.services.service.GestureService;
import cz.gag.web.persistence.entity.DataLine;

/**
 * @author Vojtech Prusa
 * 
 */
@JsonIgnoreProperties(value = {"gesture"})
public class MDataLine extends DataLine implements GestureMapper<DataLine> {

    @JsonProperty("gid")
    protected Long gestureID;

    public Long getGestureID() {
        return gestureID;
    }

    public void setGestureID(Long gestureID) {
        this.gestureID = gestureID;
    }

    public DataLine getEntity(GestureService gservice) {
        DataLine dl = new DataLine();
        return GestureMapper.mapDataLine(gservice, this, dl);
    }

}