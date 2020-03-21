package cz.muni.fi.gag.web.persistence.dao.impl;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import cz.muni.fi.gag.web.persistence.entity.WristDataLine;
import cz.muni.fi.gag.web.persistence.dao.WristDataLineDao;

/**
 * @author Vojtech Prusa
 *
 */
@ApplicationScoped
public class WristDataLineDaoImpl extends AbstractGenericDao<WristDataLine> implements WristDataLineDao, Serializable {

    public WristDataLineDaoImpl() {
        super(WristDataLine.class);
    }
}
