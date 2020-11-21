package cz.muni.fi.gag.web.services.service;

import cz.muni.fi.gag.web.persistence.dao.GestureDao;
import cz.muni.fi.gag.web.persistence.entity.DataLine;
import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.persistence.entity.User;
import cz.muni.fi.gag.web.services.service.generic.GenericCRUDService;
import cz.muni.fi.gag.web.services.service.impl.GestureServiceImpl;

import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author Vojtech Prusa
 *
 * {@link GestureServiceImpl}
 *
 */
public interface GestureService extends GenericCRUDService<Gesture, GestureDao> {

    List<Gesture> findByUser(User u);
    Gesture findRefById(Long u);

    <T extends DataLine> Stream<T> getDataStream();
    List<Gesture> findActive(User u);
}
