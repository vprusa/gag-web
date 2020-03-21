/*
Copyright (c) 2018 Vojtěch Průša
*/
package cz.muni.fi.gag.web.services.filters;

import cz.muni.fi.gag.web.persistence.entity.DataLine;

/**
 * @author Vojtech Prusa
 *
 * @param <T> todo fix?
 * 
 *        This class is used to handle parsing data files of row format
 * 
 *        <YYYY-MM-DD_HH:mm:ss.SSS> <handId> <sensorId> <q0> <q1> <q2> <q3> <a0>
 *        <a1> <a2>
 * 
 *        into LineData class base for further operations line replaying file,
 *        etc.
 * 
 *        Why to use generic class T: because then i can use LineDate children
 *        class instances to store just necessary data and/or implement specific
 *        methods in given context. TODO .. fix constructors
 * 
 */
public interface Parser<T extends DataLine> {

    T parseLine();

}
