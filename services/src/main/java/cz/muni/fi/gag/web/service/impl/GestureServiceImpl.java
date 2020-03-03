package cz.muni.fi.gag.web.service.impl;

import cz.muni.fi.gag.web.dao.GestureDao;
import cz.muni.fi.gag.web.entity.Gesture;
import cz.muni.fi.gag.web.entity.User;
import cz.muni.fi.gag.web.service.GestureService;
import cz.muni.fi.gag.web.service.generic.GenericCRUDServiceImpl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

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

}
