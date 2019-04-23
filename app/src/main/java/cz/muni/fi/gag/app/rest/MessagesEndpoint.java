package cz.muni.fi.gag.app.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Nobody Nobody
 *
 */
@Path("/messages")
public class MessagesEndpoint {

    @Inject
    private cz.muni.fi.gag.app.store.MessageStore messages;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<cz.muni.fi.gag.app.model.Message> getMessages() {
        return messages.getMessages();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void addMessage(cz.muni.fi.gag.app.model.Message message) {
        messages.addMessage(message);
    }
}
