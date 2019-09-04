package cz.muni.fi.gag.web.mapped;

import cz.muni.fi.gag.web.entity.DataLine;
import cz.muni.fi.gag.web.entity.FingerDataLine;
import cz.muni.fi.gag.web.entity.Gesture;
import cz.muni.fi.gag.web.entity.WristDataLine;
import cz.muni.fi.gag.web.service.GestureService;

/**
 * @author Vojtech Prusa
 *
 * TODO .. how to avoid duplicit code in map[Finger|Wrist]DataLine(...) mehtods?
 *
 */
public interface GestureMapper<DataLineEx extends DataLine> {
        //, Service extends GestureService //GenericCRUDService<DataLineEx, GenericDao<DataLineEx>>

    Long getGestureID();
    void setGestureID(Long gestureID);
    //void setGestureFromID(GestureService gservice);
    DataLineEx getEntity(GestureService gservice);

    default Gesture getGestureFromID(GestureService gservice){
        return gservice.findById(getGestureID()).orElseGet(null);
    }

    static DataLine mapDataLine(GestureService gservice, MDataLine mdl, DataLine dl){
        dl.setTimestamp(mdl.getTimestamp());
        dl.setGesture(gservice.findById(mdl.getGestureID()).orElseGet(null));
        dl.setGesture(mdl.getGestureFromID(gservice));
        return dl;
    }

    static FingerDataLine mapFingerDataLine(GestureService gservice, MFingerDataLine mfdl, FingerDataLine fdl){
        fdl.setTimestamp(mfdl.getTimestamp());
        fdl.setGesture(gservice.findById(mfdl.getGestureID()).orElseGet(null));
        fdl.setGesture(mfdl.getGestureFromID(gservice));
        fdl.setAccX(mfdl.getAccX());
        fdl.setAccY(mfdl.getAccY());
        fdl.setAccZ(mfdl.getAccZ());
        fdl.setQuatA(mfdl.getQuatA());
        fdl.setQuatX(mfdl.getQuatX());
        fdl.setQuatY(mfdl.getQuatY());
        fdl.setQuatZ(mfdl.getQuatZ());
        return fdl;
    }

    static WristDataLine mapWristDataLine(GestureService gservice, MWristDataLine mwdl, WristDataLine wdl){
        wdl.setTimestamp(mwdl.getTimestamp());
        wdl.setGesture(gservice.findById(mwdl.getGestureID()).orElseGet(null));
        wdl.setGesture(mwdl.getGestureFromID(gservice));
        wdl.setAccX(mwdl.getAccX());
        wdl.setAccY(mwdl.getAccY());
        wdl.setAccZ(mwdl.getAccZ());
        wdl.setQuatA(mwdl.getQuatA());
        wdl.setQuatX(mwdl.getQuatX());
        wdl.setQuatY(mwdl.getQuatY());
        wdl.setQuatZ(mwdl.getQuatZ());
        wdl.setMagX(mwdl.getMagX());
        wdl.setMagY(mwdl.getMagY());
        wdl.setMagZ(mwdl.getMagZ());
        return wdl;
    }

}