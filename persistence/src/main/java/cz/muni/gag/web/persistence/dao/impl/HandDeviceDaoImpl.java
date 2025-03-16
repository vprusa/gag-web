package cz.gag.web.persistence.dao.impl;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import cz.gag.web.persistence.dao.HandDeviceDao;
import cz.gag.web.persistence.entity.HandDevice;

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
