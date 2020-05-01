package cz.muni.fi.gag.web.services.recognition.comparators;

import cz.muni.fi.gag.web.persistence.dao.impl.DataLineGestureIterator;
import cz.muni.fi.gag.web.persistence.entity.FingerDataLine;
import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.services.recognition.GestureMatchComparator;
import cz.muni.fi.gag.web.services.recognition.GestureMatcher;
import cz.muni.fi.gag.web.services.recognition.Quaternion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

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

    public static Logger log = Logger.getLogger(BaseComparator.class.getSimpleName());

    public static final int BUFFER_SIZE = 40; // TODO guess optimal max/min
    protected T first;
    protected final Gesture gRef;
    protected List<T> refList;
//    protected Stream<T> ref;

    static final float matchDistanceThreshold = 0.5f;
    final protected List<GestureMatcher> matches = new ArrayList<GestureMatcher>();
    protected DataLineGestureIterator dlgIter = null;

    protected long refSize = 0;
    private long refTotalSize = -1;

    public BaseComparator(final Gesture gRef, final DataLineGestureIterator dlgIter) {
        this.gRef = gRef;
        this.dlgIter = dlgIter;
        this.refList = new ArrayList<>();
        this.refTotalSize = this.dlgIter.getTotalSize();
    }

    public long getRefSize() {
        refSize = refList.size();
//        refSize = ref.count();
        return refSize;
    }

    public long getRefTotalSize() {
        return refTotalSize;
    }

    protected T getDL(int index) {
        T dl = null;
        while (getRefSize() < index) {
            if (this.dlgIter.hasNext()) {
                dl = (T) this.dlgIter.next();
                // If not @Override then no filtering and everything is added to refList ...
                refList.add(dl);
            }
        }
        return (T) dl;
    }

    protected List<T> filterDataLines(List<T> dll) {
        return dll;
    }

    protected Stream<T> filterDataLines(Stream<T> dls) {
        return dls;
    }

    boolean doesMatch(final T fdlRef, T fdl) {
        Quaternion qRef = new Quaternion(fdlRef.getQuatA(), fdlRef.getQuatX(), fdlRef.getQuatY(), fdlRef.getQuatZ()).normalize();
        Quaternion q = new Quaternion(fdl.getQuatA(), fdl.getQuatX(), fdl.getQuatY(), fdl.getQuatZ()).normalize();
//        RecognitionTest.log.info("doesMatch: ");
//        RecognitionTest.log.info(qRef.toString());
//        RecognitionTest.log.info(q.toString());
        double dist = quatsAbsDist(qRef, q);

//        RecognitionTest.log.info("dist: " + dist + "( dist < matchThreshold)" + (dist < matchDistanceThreshold));
        return (dist < matchDistanceThreshold);
    }

    public GestureMatcher compare(T fdl) {
        if (fdl == null) {
            return null;
        }
        // TODO brainstorm 'matched = new List<GestureMatcher>()' so multiple gestures could be matched?
        // most likely not necessary and waste of CPU
        GestureMatcher matched = null;
        Iterator<GestureMatcher> matchesIt = matches.iterator();
        int matcherIndex = 0; // this variable is here is for debug purposes
        while (matchesIt.hasNext()) {
            GestureMatcher matcher = matchesIt.next();
            if (matcher.getIndex() >= getRefTotalSize()) {
                // Matched!
                matcher.setG(gRef);
                matcher.setAtDataLine(fdl);
                matched = matcher;
                // also this will be overwritten by reverse-chronologically previous match
                // or not? break;
                // should not matter considering this is just for single gesture..
                matchesIt.remove();
                matches.clear();
                break;
            }
            int newPossibleMatchersIndex = matcher.getIndex();
//            if(ref.size() >= newPossibleMatchersIndex){

//            T fdlRef1 = refList.get(newPossibleMatchersIndex);
            T fdlRef1 = getDL(newPossibleMatchersIndex);
            if (fdlRef1 == null) {
                break;
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

            // if(ref.size() > matcher.getIndex()+1) {
            //     T fdlRef2 = ref.get(matcher.getIndex()+1);
            // }
            if (doesMatch(fdlRef1, fdl)) {
                // Wee match, lets continue
                matcher.incIndex();
            } else {
                // TODO allow 10% data mismatch?
                // if 9 out of 10 match in then ignore next wrong and continue recognizing one more time
                //matchesIt.remove();
            }
//            }

//            if (matcher.getIndex() >= ref.size() && matcher.getIndex() > BUFFER_SIZE) { }
            // Boundary for memory usage..
            if (matcher.getIndex() > BUFFER_SIZE) {
//                matchesIt.remove();
            }

            matcherIndex++;
        }
        // add new if matches at the end
        // TODO consider moving this before rest?
        // there may happen a skip for match here when |list<DL>|==1 because matched is skipped here on purpose
        // but that should not be allowed
        if (matched == null && first != null && doesMatch(first, fdl)) {
            GestureMatcher matcher = new GestureMatcher(1, gRef);
            matches.add(matcher);
            if (matcher.getIndex() >= getRefTotalSize()) {
                // Matched!
                matcher.setG(gRef);
                matcher.setAtDataLine(fdl);
                matched = matcher;
                // also this will be overwritten by reverse-chronologically previous match
                // or not? break;
                // should not matter considering this is just for single gesture..
                // TODO in practice gesture should not be matched just by 1 Hand<DataLine> set and should be restricted
                // by some lower boundary (3?)
//                matchesIt.remove();
//                matches.clear();
            }
        }
        return matched;
    }

    // http://www.boris-belousov.net/2016/12/01/quat-dist/
    static float quatsAbsDist(Quaternion q1, Quaternion q2) {
        double sum = q1.getQ0() * q2.getQ0() +
                q1.getQ1() * q2.getQ1() +
                q1.getQ2() * q2.getQ2() +
                q1.getQ3() * q2.getQ3();
        float dist = (float) Math.acos(2.d*sum -1 );
        // TODO fix .. also make sure it works on 32bit for micro-controllers
        if (isNaN(dist)) {
            return 0;
        }
        return dist;
    }

}
    