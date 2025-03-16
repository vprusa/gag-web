package cz.gag.web.persistence.dao.impl;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import cz.gag.web.persistence.dao.FingerSensorOffsetDao;
import cz.gag.web.persistence.entity.FingerSensorOffset;

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
