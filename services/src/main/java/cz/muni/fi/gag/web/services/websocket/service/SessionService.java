package cz.muni.fi.gag.web.services.websocket.service;

import javax.websocket.Session;
import java.util.Collection;

/**
 * @author Vojtech Prusa
 *
 * Websocket session service
 *
 */
public interface SessionService {

    Collection<Session> getAllSessions();

    void addSession(Session session);

    void removeSession(Session session);
}
