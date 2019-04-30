package cz.muni.fi.gag.web.dao.impl;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import cz.muni.fi.gag.web.dao.SensorOffsetDao;
import cz.muni.fi.gag.web.entity.SensorOffset;

/**
 * @author Vojtech Prusa
 *
 */
@ApplicationScoped
public class SensorOffsetDaoImpl extends AbstractGenericDao<SensorOffset> implements SensorOffsetDao, Serializable {

    public SensorOffsetDaoImpl() {
        super(SensorOffset.class);
    }
}
