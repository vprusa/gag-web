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
//@SessionScoped
//@ApplicationScoped
//@Stateful
public class DataLineRePlayer implements Runnable {

    public static final String GESTURE_KEY = "gestureId";
    
    private Session session;

    private DataLineService dataLineService;

    private long gestureId;
    
    public DataLineRePlayer(Session session, DataLineService dataLineService, long gestureId) {
        this.dataLineService = dataLineService;
        this.session = session;
        this.gestureId = gestureId;
        Log.info(session.toString());
    }

    @Override
    public void run() {
        Log.info("run");
        //while (true) {
            // https://stackoverflow.com/questions/16504140/thread-stop-deprecated
            // TODO fix the fix of the fix ?
            if (Thread.interrupted()) {
                return;
            }
            Log.info("1");
            Iterator<DataLine> dli = dataLineService.initIteratorByGesture(gestureId);
            Date before = null;
            Date now = null;
            Log.info("2");
            try {
                while (dli.hasNext()) {
                    DataLine dl = dli.next();
                    //Log.info("DL: " + dl.toString());
                    //session.getAsyncRemote().sendObject(dl);
                    //Log.info(Boolean.toString(session.isOpen()));
                    Log.info(dl.toString());
                    dl.setGesture(null);
                    session.getBasicRemote().sendObject(dl);
                    now = dl.getTimestamp();
                    if (now != null && before != null) {
                        Thread.sleep(now.getTime() - before.getTime());                        
                    }
                    before = now;
                }
            } catch (InterruptedException | IOException | EncodeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.info(e.getMessage());
            }
        //}
    }

}
