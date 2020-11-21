package cz.muni.fi.gag.web.persistence.dao.impl;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import cz.muni.fi.gag.web.persistence.dao.HandDeviceDao;
import cz.muni.fi.gag.web.persistence.entity.HandDevice;

/**
 * @author Vojtech Prusa
 *
 */
@ApplicationScoped
public class HandDeviceDaoImpl extends AbstractGenericDao<HandDevice> implements HandDeviceDao, Serializable {

    public HandDeviceDaoImpl() {
        super(HandDevice.class);
    }
}
