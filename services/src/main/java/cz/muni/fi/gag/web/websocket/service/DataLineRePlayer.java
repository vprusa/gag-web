package cz.muni.fi.gag.web.websocket.service;

import cz.muni.fi.gag.web.entity.DataLine;
import cz.muni.fi.gag.web.logging.Log;
import cz.muni.fi.gag.web.service.DataLineService;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

/**
 * @author Vojtech Prusa
 *
 */
public class DataLineRePlayer implements Runnable {

    public static final String GESTURE_KEY = "gestureId";
    
    private Session session;

    private DataLineService dataLineService;

    private long gestureId;
    
    public DataLineRePlayer(Session session, DataLineService dataLineService, long gestureId) {
        this.dataLineService = dataLineService;
        this.session = session;
        this.gestureId = gestureId;
    }

    @Override
    public void run() {
        Log.info("Running DataLine re-play");
        // https://stackoverflow.com/questions/16504140/thread-stop-deprecated
        // TODO fix the fix of the fix ?
        if (Thread.interrupted()) {
            return;
        }
        Iterator<DataLine> dli = dataLineService.initIteratorByGesture(gestureId);
        Date before = null;
        Date now = null;
        try {
            while (dli.hasNext()) {
                DataLine dl = dli.next();
                //Log.info(now == null ? "Now null" : now.toString());
                //Log.info(before  == null ? "Before null" : before.toString());
                //Log.info(dl.toString());
                now = dl.getTimestamp();
                //Log.info(now == null ? "New now null" : now.toString());
                if (now != null && before != null) {
                    long timeDiff = now.getTime() - before.getTime();
                    if(timeDiff == 0){
                         return;
                    }
                    session.getBasicRemote().sendObject(dl);
                    Thread.sleep(timeDiff);
                }else if(now != null){
                    session.getBasicRemote().sendObject(dl);
                }
                before = now;
            }
        } catch (InterruptedException | IOException | EncodeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //Log.info(e.getMessage());
        }
    }

}
