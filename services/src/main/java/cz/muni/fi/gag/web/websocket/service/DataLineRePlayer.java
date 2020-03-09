package cz.muni.fi.gag.web.websocket.service;

import cz.muni.fi.gag.web.entity.DataLine;
import cz.muni.fi.gag.web.logging.Log;
import cz.muni.fi.gag.web.service.DataLineService;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.ListIterator;

import static cz.muni.fi.gag.web.websocket.service.DataLineRePlayer.PlayerState.*;
import static cz.muni.fi.gag.web.websocket.service.DataLineRePlayer.PlayerState.IDLE;

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
    private ListIterator<DataLine> dli;

    public PlayerState getState() {
        synchronized (state) {
            return state;
        }
    }

    private PlayerState state = IDLE;

    public void play() {
        synchronized (state) {
            this.state = PLAYING;
        }
    }


    public void stop() {
        synchronized (state) {
            this.state = STOPPED;
        }
    }

    public void pause() {
        synchronized (state){
            state = PAUSED;
        }
    }

    // preparing for further states
    public enum PlayerState {IDLE, PLAYING, PAUSED, STOPPED};

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

    public void prepare() {
        prepare(false);
    }
    public void prepare(boolean force){
        play();
        if(dli == null){
            dli = dataLineService.findByGestureId(gestureId).listIterator();
        }else if(force){
            dli = dataLineService.findByGestureId(gestureId).listIterator();
        }
    }

    // https://stackoverflow.com/questions/28922040/alternative-to-thread-suspend-and-resume
    @Override
    public void run() {
        prepare();
        Log.info("Running DataLine re-play");
        // https://stackoverflow.com/questions/16504140/thread-stop-deprecated
        // TODO fix the fix of the fix ?
        if (Thread.interrupted()) {
            return;
        }
        Date before = null;
        Date now = null;

        try {
            int diffZeroLimitCounter = 0;
            while (dli != null && dli.hasNext()) {
                DataLine dl = dli.next();
                    switch (getState()) {
                        case IDLE:
                            // so far this should not happen, but that may be changed in the future when multiple
                            // player over single WS connection are implemented
                            // anyway ... lets continue to STOPPED state ..
                        case STOPPED:
                            return;
                        case PAUSED:
                            Thread cur = Thread.currentThread();
                            synchronized (cur) {
                                cur.wait();
                            }
                            break;
                        case PLAYING:
                            // noting, keep looping
                            break;
                    }

                if(dl == null && dli!= null && dli.hasNext()){
                    dl = dli.next();
                }
                Log.info(now == null ? "Now null" : now.toString());
                Log.info(before == null ? "Before null" : before.toString());
                Log.info(dl.toString());
                now = dl.getTimestamp();

                Log.info(now == null ? "New now null" : now.toString());
                if (now != null && before != null) {
                    long timeDiff = now.getTime() - before.getTime();
                    if (timeDiff == 0 && (++diffZeroLimitCounter) > DataLineRePlayer.TIME_DIFF_ZERO_LIMIT) {
                        continue;
                    }
                    session.getBasicRemote().sendObject(dl);
                    Thread.sleep(timeDiff);
                } else if (now != null) {
                    session.getBasicRemote().sendObject(dl);
                }
                before = now;
            }
        } catch (InterruptedException | IOException | EncodeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
