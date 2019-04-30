package cz.muni.fi.gag.web.dao.impl;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import cz.muni.fi.gag.web.dao.HandDeviceDao;
import cz.muni.fi.gag.web.entity.HandDevice;

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
