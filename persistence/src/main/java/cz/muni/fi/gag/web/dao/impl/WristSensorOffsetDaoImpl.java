package cz.muni.fi.gag.web.dao.impl;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import cz.muni.fi.gag.web.dao.WristSensorOffsetDao;
import cz.muni.fi.gag.web.entity.WristSensorOffset;

/**
 * @author Vojtech Prusa
 *
 */
@ApplicationScoped
public class WristSensorOffsetDaoImpl extends AbstractGenericDao<WristSensorOffset>
        implements WristSensorOffsetDao, Serializable {

    public WristSensorOffsetDaoImpl() {
        super(WristSensorOffset.class);
    }
}
