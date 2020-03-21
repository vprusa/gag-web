package cz.muni.fi.gag.web.persistence.dao.impl;

import cz.muni.fi.gag.web.persistence.dao.FingerDataLineDao;
import cz.muni.fi.gag.web.persistence.entity.FingerDataLine;
import java.io.Serializable;
import javax.enterprise.context.ApplicationScoped;

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
