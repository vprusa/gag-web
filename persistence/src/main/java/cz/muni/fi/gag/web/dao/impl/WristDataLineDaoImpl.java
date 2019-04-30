package cz.muni.fi.gag.web.dao.impl;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import cz.muni.fi.gag.web.dao.WristDataLineDao;
import cz.muni.fi.gag.web.entity.WristDataLine;

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
