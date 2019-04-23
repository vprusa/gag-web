package cz.muni.fi.gag.web.dao.impl;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import cz.muni.fi.gag.web.dao.DataLineDao;
import cz.muni.fi.gag.web.entity.DataLine;

/**
 * @author Miloslav Zezulka
 *
 */
@ApplicationScoped
public class DataLineDaoImpl extends AbstractGenericDao<DataLine> implements DataLineDao, Serializable {

    public DataLineDaoImpl() {
        super(DataLine.class);
    }
}
