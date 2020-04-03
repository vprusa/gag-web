package cz.muni.fi.gag.tests.recognition;

import cz.muni.fi.gag.web.persistence.entity.FingerDataLine;
import cz.muni.fi.gag.web.persistence.entity.Sensor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Vojtech Prusa
 */
class SensorGestureI<T extends FingerDataLine> {
    // extends SensorGestureA<T> {

    public static final int BUFFER_SIZE = 20;
    //    List<T> firstNBuffer = new ArrayList<T>();
    final List<T> ref;
    final T first;
    final List<GestureMatcher> matches = new ArrayList<GestureMatcher>();

    public SensorGestureI(final Sensor s, final List<T> ref) {
        this.s = s;
        this.ref = ref;
        this.first = ref.get(0);
    }

    static float matchThreshold = 0.1f;
    float curMatch = 0;

    protected Sensor s;

    Sensor getSensor() {
        return s;
    }

    boolean doesMatch(final T fdlRef, T fdl) {
        Quaternion qRef = new Quaternion(fdlRef.getQuatA(), fdlRef.getQuatX(), fdlRef.getQuatY(), fdlRef.getQuatZ());
        Quaternion q = new Quaternion(fdl.getQuatA(), fdl.getQuatX(), fdl.getQuatY(), fdl.getQuatZ());
        double dist = quatsAbsDist(qRef, q);
        return (dist < matchThreshold);
    }

    GestureMatcher compare(T fdl) {
        // TODO pass Gesture
        // TODO do not even add it, if not matches just pass by

        // TODO brainstorm 'matched = new List<GestureMatcher>()' so multiple gestures could be matched?
        // most likely not necessary and waste of CPU
        GestureMatcher matched = null;
        Iterator<GestureMatcher> matchesIt = matches.iterator();
        int matcherIndex = 0;
        while (matchesIt.hasNext()) {
            GestureMatcher matcher = matchesIt.next();
            if (matcher.getIndex() >= ref.size()) {
                // Matched!
                matched = matcher;
                // also this will be overwritten by reverse-chronologically previous match
                // or not? break;
                // should not matter considering this is just for single gesture..
                matchesIt.remove();
                matches.clear();
                break;

            }
            // This works as sliding window
            // for each
            T fdlRef = ref.get(matcher.getIndex());
            if (doesMatch(fdlRef, fdl)) {
                // Wee match, lets continue
                matcher.incIndex();
            } else {
                // TODO allow 10% data mismatch?
                // if 9 out of 10 match in then ignore next wrong and continue recognizing one more time
                matchesIt.remove();
            }
//            if (matcher.getIndex() >= ref.size() && matcher.getIndex() > BUFFER_SIZE) { }
            // Boundary for memory usage..
            if (matcher.getIndex() > BUFFER_SIZE) {
                matchesIt.remove();
            }

            matcherIndex++;
        }
        // add new if matches at the end
        // TODO consider moving this before rest?
        // there may happen a skip for match here when |list<DL>|==1 because matched is skipped here on purpose
        // but that should not be allowed
        if (doesMatch(ref.get(0), fdl)) {
            GestureMatcher newMatcher = new GestureMatcher(1, null);
            matches.add(newMatcher);
        }
        return matched;
    }

    // http://www.boris-belousov.net/2016/12/01/quat-dist/
    double quatsAbsDist(Quaternion q1, Quaternion q2) {
        double sum = q1.getQ0() * q2.getQ0() +
                q1.getQ1() * q2.getQ1() +
                q1.getQ2() * q2.getQ2() +
                q1.getQ3() * q2.getQ3();
        return 2.f * Math.acos(sum);
    }
}
    