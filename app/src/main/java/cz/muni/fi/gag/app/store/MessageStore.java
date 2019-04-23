package cz.muni.fi.gag.app.store;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author Nobody Nobody
 *
 */
@ApplicationScoped
public class MessageStore {

    private List<cz.muni.fi.gag.app.model.Message> messages = Collections.synchronizedList(new LinkedList<>());

    public List<cz.muni.fi.gag.app.model.Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public void addMessage(cz.muni.fi.gag.app.model.Message message) {
        messages.add(message);
    }
}
