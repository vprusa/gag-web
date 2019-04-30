package cz.muni.fi.gag.web.dao.impl;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import cz.muni.fi.gag.web.dao.SensorTypeDao;
import cz.muni.fi.gag.web.entity.SensorType;

/**
 * @author Vojtech Prusa
 *
 */
@ApplicationScoped
public class SensorTypeDaoImpl extends AbstractGenericDao<SensorType> implements SensorTypeDao, Serializable {

    public SensorTypeDaoImpl() {
        super(SensorType.class);
    }
}
