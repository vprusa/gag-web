package cz.gag.web.services.recognition.comparators;

import cz.gag.web.persistence.dao.impl.DataLineGestureIterator;
import cz.gag.web.persistence.entity.FingerDataLine;
import cz.gag.web.persistence.entity.Gesture;
import cz.gag.web.shared.common.Quaternion;
import cz.gag.web.services.logging.Log;
import cz.gag.web.services.recognition.GestureMatchComparator;
import cz.gag.web.services.recognition.matchers.SingleSensorGestureMatcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.Double.isNaN;

/**
 * @author Vojtech Prusa
 * <p>
 * {@link SensorComparator}
 * {@link FingerComparator}
 * {@link WristComparator}
 * {@link HandComparator}
 */
public abstract class BaseComparator<T extends FingerDataLine> implements GestureMatchComparator<T> {

    //    public static Logger log = Logger.getLogger(BaseComparator.class.getSimpleName());
    public static final Log.TypedLogger log = new Log.TypedLogger<Log.LoggerTypeWSRecognizerComparator>(Log.LoggerTypeWSRecognizerComparator.class);

    public static final int BUFFER_SIZE = 40; // TODO guess optimal max/min
    protected T first;
    protected final Gesture gRef;
    protected List<T> refList;

//    static final float matchDistanceThreshold = 0.5f;
    final protected List<SingleSensorGestureMatcher> matchers = new ArrayList<SingleSensorGestureMatcher>();
    protected DataLineGestureIterator dlgIter = null;

    // this contains last match ..
    // useful when obtaining last matches for gesture of whole hand
    public List<SingleSensorGestureMatcher> matched;

    protected long refSize = 0;
    private long refTotalSize = -1;

    public BaseComparator(final Gesture gRef, final DataLineGestureIterator dlgIter) {
        this.gRef = gRef;
        this.dlgIter = dlgIter;
        log.info("BaseComparator.BaseComparator.dlgIter " + dlgIter);
        this.refList = new ArrayList<>();
        this.refTotalSize = this.dlgIter.getTotalSize();
        log.info("BaseComparator.BaseComparator.refTotalSize " + refTotalSize);
        this.matched = new ArrayList<SingleSensorGestureMatcher>();
    }

    public long getRefSize() {
        refSize = refList.size();
        return refSize;
    }

    public long getRefTotalSize() {
        return refTotalSize;
    }

    protected T getDL(int index) {
        log.info("BaseComparator.getDL");
        T dl = null;
        while (getRefSize() < index) {
            if (this.dlgIter.hasNext()) {
                dl = (T) this.dlgIter.next();
                // If not @Override then no filtering and everything is added to refList ...
                refList.add(dl);
            }
        }
        log.info("BaseComparator.getDL" + dl);
        return (T) dl;
    }

    // in case of WristComparator this should be overriden
    protected boolean doesMatch(final T fdlRef, T fdl) {
        Quaternion qRef = new Quaternion(fdlRef.getQuatA(), fdlRef.getQuatX(), fdlRef.getQuatY(), fdlRef.getQuatZ()).normalize();
        Quaternion q = new Quaternion(fdl.getQuatA(), fdl.getQuatX(), fdl.getQuatY(), fdl.getQuatZ()).normalize();
//        RecognitionTest.log.info("doesMatch: ");
//        RecognitionTest.log.info(qRef.toString());
//        RecognitionTest.log.info(q.toString());
        double dist = quatsAbsDist(qRef, q);
//        RecognitionTest.log.info("dist: " + dist + "( dist < matchThreshold)" + (dist < matchDistanceThreshold));
//        return (dist < matchDistanceThreshold);
        return (dist < gRef.getShouldMatch());
    }

    public List<SingleSensorGestureMatcher> compare(T fdl) {
        if (fdl == null) {
            return null;
        }
        log.info("BaseComparator.compare");
        // TODO brainstorm 'matched = new List<GestureMatcher>()' so multiple gestures could be matched?
        // most likely not necessary and waste of CPU
//        matched = new ArrayList<GestureMatcher>();
        matched.clear();
//        log.info("BaseComparator.compare.matchers " + matchers.toString());
//        log.info("BaseComparator.compare.matched " + matched.toString());
        int matcherIndex = 0; // this variable is here is for debug purposes
        for (Iterator<SingleSensorGestureMatcher> matchesIt = matchers.iterator(); matchesIt.hasNext(); ) {
            SingleSensorGestureMatcher matcher = matchesIt.next();

            int newPossibleMatchersIndex = matcher.getIndex();
//            if(ref.size() >= newPossibleMatchersIndex){
//            T fdlRef1 = refList.get(newPossibleMatchersIndex);
            T fdlRef1 = getDL(newPossibleMatchersIndex);
            if (fdlRef1 == null) {
                continue;
            }

            if (doesMatch(fdlRef1, fdl)) {
                // Wee match, lets continue
                matcher.incIndex();
                log.info("BaseComparator.compare.matcher inc-index " + matcher.toString());
            } else {
                // TODO allow 10% data mismatch?
                // if 9 out of 10 match in then ignore next wrong and continue recognizing one more time
                //matchesIt.remove();
            }
            // Boundary for memory usage..
            if (matcher.getIndex() > BUFFER_SIZE) {
//                matchesIt.remove();
            }

            if (matcher.getIndex() >= getRefTotalSize()) {
                // Matched!
//                matcher.setG(gRef);
                matcher.setAtDataLine(fdl);
                log.info("BaseComparator.compare.matcher (nth) " + matcher.toString());

                matched.add(matcher);
                // also this will be overwritten by reverse-chronologically previous match
                // or not? break;
                // should not matter considering this is just for single gesture..
                matchesIt.remove();
                // uncomment if only single recognition can occur per gesture
//                matches.clear();
                //break;
            }

//            T fdlRef1 = null;
            // TODO brainstorm:
            //  fdlRef1 cannot be null and has to be refList.get(newPossibleMatchersIndex);
            //  is it possible to obtain field at index with Stream..
            //  no..
            //  get it directly from DB (+ cache)? ..
            //  I have to implement my own caching of GestureDataLines
            //  - own because of future nativity (C++)
            //  - it should work by loading data into memory and then adding it to list
            //  -- fortunately i have
            matcherIndex++;
        }
        // add new if matches at the end
        // TODO consider moving this before rest?
        // there may happen a skip for match here when |list<DL>|==1 because matched is skipped here on purpose
        // but that should not be allowed

        if (first != null && doesMatch(first, fdl)) {
            SingleSensorGestureMatcher matcher = new SingleSensorGestureMatcher(1, gRef);
            if (matcher.getIndex() >= getRefTotalSize()) {
                // Matched!
//                matcher.setG(gRef);
                matcher.setAtDataLine(fdl);
                matched.add(matcher);
//                log.info("BaseComparator.compare.matcher (nth) " + matcher.toString());
                // also this will be overwritten by reverse-chronologically previous match
                // or not? break;
                // should not matter considering this is just for single gesture..
                // TODO in practice gesture should not be matched just by 1 Hand<DataLine> set and should be restricted
                // by some lower boundary (3?)
//                matchesIt.remove();
//                matches.clear();
            } else {
                matchers.add(matcher);
            }
        }
        log.info("BaseComparator.compare.matched " + matched.toString());
        log.info("BaseComparator.compare.matchers " + matchers.toString());
        return matched;
    }

    // http://www.boris-belousov.net/2016/12/01/quat-dist/
    static float quatsAbsDist(Quaternion q1, Quaternion q2) {
        double sum = q1.getQ0() * q2.getQ0() +
                q1.getQ1() * q2.getQ1() +
                q1.getQ2() * q2.getQ2() +
                q1.getQ3() * q2.getQ3();
        float dist = (float) Math.acos(2.d * sum - 1);
        // TODO fix .. also make sure it works on 32bit for micro-controllers
        if (isNaN(dist)) {
            return 0;
        }
        return dist;
    }

}
    