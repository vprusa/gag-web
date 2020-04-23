package cz.muni.fi.gag.web.services.service.impl;

import cz.muni.fi.gag.web.persistence.dao.GestureDao;
import cz.muni.fi.gag.web.persistence.entity.DataLine;
import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.persistence.entity.User;
import cz.muni.fi.gag.web.services.service.GestureService;
import cz.muni.fi.gag.web.services.service.generic.GenericCRUDServiceImpl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author Vojtech Prusa
 *
 */
@Stateless
public class GestureServiceImpl extends GenericCRUDServiceImpl<Gesture, GestureDao>
        implements GestureService {

    @Inject
    protected GestureDao genericDao;

    @Override
    public GestureDao getDao() {
        return genericDao;
    }

    @Override
    public List<Gesture> findByUser(User u) {
        GestureDao gdao = getDao();
        return gdao.findByUser(u);
    }

    @Override
    public Gesture findRefById(Long u) {
        return getDao().findRefById(u);
    }

    @Override
    public <T extends DataLine> Stream<T> getDataStream() {
        List<DataLine> l = Collections.emptyList();
        // TODO
        return (Stream<T>) l.stream();
    }

    @Override
    public List<Gesture> findActive(User u) {
        return getDao().findUsersActive(u);
    }

}
