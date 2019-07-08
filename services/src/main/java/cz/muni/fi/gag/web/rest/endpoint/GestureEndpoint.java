package cz.muni.fi.gag.web.rest.endpoint;

import cz.muni.fi.gag.web.entity.Gesture;
import cz.muni.fi.gag.web.entity.User;
import cz.muni.fi.gag.web.service.GestureService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.util.List;
import java.util.Optional;

import static cz.muni.fi.gag.web.entity.UserRole.USER_R;

/**
 * @author Vojtech Prusa
 *
 */
@Path("/gesture")
public class GestureEndpoint extends BaseEndpoint {

    @Inject
    private GestureService dataLineService;
    /*
     * @GET
     * 
     * @Path("/my")
     * 
     * @Produces(MediaType.APPLICATION_JSON) public Response getMyGestures() {
     * LOG.info("getMyGestures"); User u = currentUser(); if(u != null) {
     * List<Gesture> myGestures = dataLineService.findByUser(u); return
     * Response.ok(myGestures).build(); } return getResponseNotLoggedIn(); }
     */

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGestureById(@PathParam("id") String identifier) {
        try {
            Optional<Gesture> dataLine = dataLineService.findById(Long.parseLong(identifier));
            if (!dataLine.isPresent()) {
                Response.status(Status.NOT_FOUND);
            }
            return Response.ok(dataLine.get()).build();
        } catch (NumberFormatException ex) {
            User u = currentUser();
            if (u != null) {
                List<Gesture> myGestures = dataLineService.findByUser(u);
                return Response.ok(myGestures).build();
            }
            return getResponseNotLoggedIn();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(USER_R)
    public Response createGesture(Gesture dataLine) {
        Response.ResponseBuilder builder;
        try {
            Gesture created = dataLineService.create(dataLine);
            builder = Response.ok(created);
        } catch (Exception e) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }
        return builder.build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed(USER_R)
    public Response removeGesture(@PathParam("id") Long id) throws Exception {
        Response.ResponseBuilder builder;
        Optional<Gesture> dataLine = dataLineService.findById(id);
        if (!dataLine.isPresent()) {
            Response.status(Status.NOT_FOUND);
        }
        try {
            dataLineService.remove(dataLine.get());
            builder = Response.ok();
        } catch (Exception e) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }
        return builder.build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(USER_R)
    public Response updateGesture(@PathParam("id") Long id, Gesture dataLine) {
        Response.ResponseBuilder builder;
        Optional<Gesture> oldGesture = dataLineService.findById(id);
        if (!oldGesture.isPresent()) {
            Response.status(Status.NOT_FOUND);
        }
        dataLine.setId(oldGesture.get().getId());
        try {
            Gesture updated = dataLineService.update(dataLine);
            builder = Response.ok(updated);
        } catch (Exception e) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }
        return builder.build();
    }

}
