package cz.muni.fi.gag.web.dao.impl;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import cz.muni.fi.gag.web.dao.FingerPositionDao;
import cz.muni.fi.gag.web.entity.FingerPosition;

/**
 * @author Vojtech Prusa
 *
 */
@ApplicationScoped
public class FingerPositionDaoImpl extends AbstractGenericDao<FingerPosition>
        implements FingerPositionDao, Serializable {

    public FingerPositionDaoImpl() {
        super(FingerPosition.class);
    }
}
