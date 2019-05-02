package cz.muni.fi.gag.web.service.impl;

import cz.muni.fi.gag.web.service.GestureService;
import cz.muni.fi.gag.web.service.generic.GenericCRUDServiceImpl;
import cz.muni.fi.gag.web.dao.GestureDao;
import cz.muni.fi.gag.web.entity.Gesture;

import javax.ejb.Stateless;
import javax.inject.Inject;

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

}
