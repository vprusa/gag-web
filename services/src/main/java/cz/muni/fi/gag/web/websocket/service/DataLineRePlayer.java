package cz.muni.fi.gag.web.websocket.service;

import cz.muni.fi.gag.web.entity.DataLine;
import cz.muni.fi.gag.web.logging.Log;
import cz.muni.fi.gag.web.service.DataLineService;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.ListIterator;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import org.jboss.logging.Logger;

/**
 * @author Vojtech Prusa
 *
 */
@Named
//@Singleton
//@Stateful
//@SessionScoped
@ApplicationScoped
public class DataLineRePlayer implements Runnable, Serializable {

    public static final String GESTURE_KEY = "gestureId";
    public static final int TIME_DIFF_ZERO_LIMIT = 5;
    public static final Logger log = Logger.getLogger(DataLineRePlayer.class.getSimpleName());

    private Session session;
    private Date before;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public long getGestureId() {
        return gestureId;
    }

    public void setGestureId(long gestureId) {
        this.gestureId = gestureId;
    }

    @Inject
    private DataLineService dataLineService;

    volatile private long gestureId;
    /*
    public DataLineRePlayer(Session session, DataLineService dataLineService, long gestureId) {
        //public DataLineRePlayer(Session session, long gestureId) {
        this.dataLineService = dataLineService;
        this.session = session;
        this.gestureId = gestureId;
    }*/

    @Override
    public void run() {
        Log.info("Running DataLine re-play");
        // https://stackoverflow.com/questions/16504140/thread-stop-deprecated
        // TODO fix the fix of the fix ?
        if (Thread.interrupted()) {
            return;
        }

        /*
        // TODO check if thread-safe?
        Stream<DataLine> sdl = dataLineService.getStream(gestureId);
        //sdl.forEachOrdered(dl -> {
        sdl.forEach(dl -> {
            log.info(dl.toString());

            try {
                //session.getBasicRemote().sendObject(dl);
                before = dl.getTimestamp();
                Date now = dl.getTimestamp();
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
            } catch (IOException e) {
                e.printStackTrace();
            } catch (EncodeException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
        */
        //DataLineGestureIterator dli = dataLineService.initIteratorByGesture(gestureId);
        //DataLineGestureIterator dli = dataLineService.initIteratorByGesture(gestureId);
        //List<DataLine> dll = dataLineService.findByGestureId(1);
        /*
        try {
            session.getBasicRemote().sendObject(dll.get(0));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncodeException e) {
            e.printStackTrace();
        }
        */
        //FingerDataLineEncoder dle = new FingerDataLineEncoder();
        /*DataLineEncoder dle = new DataLineEncoder();
        dll.forEach(dl -> {
            try {
                String dls = dle.encode(dl);
                session.getBasicRemote().sendObject(dls);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (EncodeException e) {
                e.printStackTrace();
            }
        });*/
        //String dlds = dld.decode(dlds);


        //List<DataLine> dli = dataLineService.findByGestureId(gestureId);
        ListIterator<DataLine> dli = dataLineService.findByGestureId(gestureId).listIterator();

        //ListIterator<DataLine> dli = dll.listIterator();
        Date before = null;
        Date now = null;
        log.info("-1");
        //dll.forEach(dl -> {log.info(dl.toString());});
        //Response res = Response.ok(dll).build();
        //log.info(res.getEntity().toString());

        try {
            log.info("1");
            int diffZeroLimitCounter = 0;
            while (dli.hasNext()) {
                log.info("2");
                DataLine dl = dli.next();
                Log.info(now == null ? "Now null" : now.toString());
                Log.info(before  == null ? "Before null" : before.toString());
                Log.info(dl.toString());
                now = dl.getTimestamp();

                Log.info(now == null ? "New now null" : now.toString());
                if (now != null && before != null) {
                    long timeDiff = now.getTime() - before.getTime();
                    if(timeDiff == 0 && (++diffZeroLimitCounter) > DataLineRePlayer.TIME_DIFF_ZERO_LIMIT){
                         continue;
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
