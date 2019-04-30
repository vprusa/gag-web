package cz.muni.fi.gag.web.dao.impl;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import cz.muni.fi.gag.web.dao.FingerSensorOffsetDao;
import cz.muni.fi.gag.web.entity.FingerSensorOffset;

/**
 * @author Vojtech Prusa
 *
 */
@ApplicationScoped
public class FingerSensorOffsetDaoImpl extends AbstractGenericDao<FingerSensorOffset>
        implements FingerSensorOffsetDao, Serializable {

    public FingerSensorOffsetDaoImpl() {
        super(FingerSensorOffset.class);
    }
}
