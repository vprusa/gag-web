package cz.muni.fi.gag.app.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cz.muni.fi.gag.app.web.model.Message;
import cz.muni.fi.gag.app.web.store.MessageStore;

@Path("/messages")
public class MessagesEndpoint {

    @Inject
    private MessageStore messages;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Message> getMessages() {
        return messages.getMessages();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void addMessage(Message message) {
        messages.addMessage(message);
    }
}
