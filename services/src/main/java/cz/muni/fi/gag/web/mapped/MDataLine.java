package cz.muni.fi.gag.web.mapped;

import cz.muni.fi.gag.web.entity.DataLine;
import cz.muni.fi.gag.web.service.GestureService;

/**
 * @author Vojtech Prusa
 * 
 */
public class MDataLine extends DataLine implements GestureMapper<DataLine> {

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