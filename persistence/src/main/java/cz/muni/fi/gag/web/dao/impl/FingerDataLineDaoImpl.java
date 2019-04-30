package cz.muni.fi.gag.web.dao.impl;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import cz.muni.fi.gag.web.dao.FingerDataLineDao;
import cz.muni.fi.gag.web.entity.FingerDataLine;

/**
 * @author Vojtech Prusa
 *
 */
@ApplicationScoped
public class FingerDataLineDaoImpl extends AbstractGenericDao<FingerDataLine>
        implements FingerDataLineDao, Serializable {

    public FingerDataLineDaoImpl() {
        super(FingerDataLine.class);
    }
}
