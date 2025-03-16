package cz.gag.web.persistence.dao.impl;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import cz.gag.web.persistence.dao.WristSensorOffsetDao;
import cz.gag.web.persistence.entity.WristSensorOffset;

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
