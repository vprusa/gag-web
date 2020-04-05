package cz.muni.fi.gag.web.services.recognition.comparators;

import cz.muni.fi.gag.web.persistence.entity.FingerDataLine;
import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.services.recognition.GestureComparator;
import cz.muni.fi.gag.web.services.recognition.GestureMatcher;
import cz.muni.fi.gag.web.services.recognition.Quaternion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Vojtech Prusa
 */
public class GestureR<T extends FingerDataLine> implements GestureComparator<T> {

    public static final int BUFFER_SIZE = 20;
    // List<T> firstNBuffer = new ArrayList<T>();
    protected T first;
    protected final Gesture gRef;
    protected List<T> ref;
    static final float matchDistanceThreshold = 0.5f;
    final protected List<GestureMatcher> matches = new ArrayList<GestureMatcher>();

    public GestureR(final Gesture gRef) {
        this.gRef = gRef;
    }

    protected List<T> filterDataLines(List<T> dll) {
        return dll;
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
        // TODO brainstorm 'matched = new List<GestureMatcher>()' so multiple gestures could be matched?
        // most likely not necessary and waste of CPU
        GestureMatcher matched = null;
        Iterator<GestureMatcher> matchesIt = matches.iterator();
        int matcherIndex = 0; // this variable is here is for debug purposes
        while (matchesIt.hasNext()) {
            GestureMatcher matcher = matchesIt.next();
            if (matcher.getIndex() >= ref.size()) {
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
            T fdlRef1 = ref.get(newPossibleMatchersIndex);
            //            if(ref.size() > matcher.getIndex()+1) {
            //                T fdlRef2 = ref.get(matcher.getIndex()+1);
            //            }
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
            if (matcher.getIndex() > 40) {
//                matchesIt.remove();
            }

            matcherIndex++;
        }
        // add new if matches at the end
        // TODO consider moving this before rest?
        // there may happen a skip for match here when |list<DL>|==1 because matched is skipped here on purpose
        // but that should not be allowed
        if (matched == null && doesMatch(ref.get(0), fdl)) {
            GestureMatcher newMatcher = new GestureMatcher(1, null);
            matches.add(newMatcher);
        }
        return matched;
    }

    // http://www.boris-belousov.net/2016/12/01/quat-dist/
    static double quatsAbsDist(Quaternion q1, Quaternion q2) {
        double sum = q1.getQ0() * q2.getQ0() +
                q1.getQ1() * q2.getQ1() +
                q1.getQ2() * q2.getQ2() +
                q1.getQ3() * q2.getQ3();
        return 2.f * Math.acos(sum);
    }
}
    