package cz.muni.fi.gag.web.websocket.service;

import org.infinispan.Cache;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.Session;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Vojtech Prusa
 *
 */
@ApplicationScoped
public class SessionServiceImpl implements SessionService {

    @Inject
    private Cache<String, Session> sessions;

    public Collection<Session> getAllSessions() {
        return Collections.unmodifiableCollection(sessions.values());
    }

    public void addSession(Session session) {
        sessions.put(session.getId(), session);
        //LogMessages.LOGGER.logAddSession(session.getId(), sessions.size());
    }

    public void removeSession(Session session) {
        sessions.remove(session.getId());
        //LogMessages.LOGGER.logRemoveSession(session.getId(), sessions.size());
    }
}
