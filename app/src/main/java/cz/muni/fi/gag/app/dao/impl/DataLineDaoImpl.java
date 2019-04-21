package cz.muni.fi.gag.app.dao.impl;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import cz.muni.fi.gag.app.dao.DataLineDao;
import cz.muni.fi.gag.app.domain.DataLine;

@ApplicationScoped
public class DataLineDaoImpl extends AbstractGenericDao<DataLine> implements DataLineDao, Serializable {

	public DataLineDaoImpl() {
		super(DataLine.class);
	}
}
