package cz.muni.fi.gag.web.rest.endpoint;

import cz.muni.fi.gag.web.entity.Gesture;
import cz.muni.fi.gag.web.entity.User;
import cz.muni.fi.gag.web.service.GestureService;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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

import static cz.muni.fi.gag.web.entity.UserRole.USER_R;

/**
 * @author Vojtech Prusa
 *
 */
@Path("/gesture")
public class GestureEndpoint extends BaseEndpoint {

    @Inject
    private GestureService gestureService;
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
        log.info("getGestureById");
        try {
            Optional<Gesture> dataLine = gestureService.findById(Long.parseLong(identifier));
            if (!dataLine.isPresent()) {
                Response.status(Status.NOT_FOUND);
            }
            log.info("getGestureById");
            return Response.ok(dataLine.get()).build();
        } catch (NumberFormatException ex) {
            User u = currentUser();
            if (u != null) {
                List<Gesture> myGestures = gestureService.findByUser(u);
                log.info("getGestureByUser");
                return Response.ok(myGestures).build();
            }
            return getResponseNotLoggedIn();
        }
    }

    // TODO add consumption of json object of now gesture data
    @POST
    //@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(USER_R)
    @Path("/{userAlias}/{filtered}")
    //public Response createGesture(Gesture dataLine) {
    public Response createGesture(@PathParam("userAlias") String userAlias, @PathParam("filtered") int filtered) {
        Response.ResponseBuilder builder;
        try {
            Gesture g = new Gesture();
            g.setUserAlias(userAlias);
            g.setFiltered(filtered != 0);
            g.setDateCreated(new Date());
            g.setUser(currentUser());
            Gesture created = gestureService.create(g);
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
        Optional<Gesture> dataLine = gestureService.findById(id);
        if (!dataLine.isPresent()) {
            Response.status(Status.NOT_FOUND);
        }
        try {
            gestureService.remove(dataLine.get());
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
        Optional<Gesture> oldGesture = gestureService.findById(id);
        if (!oldGesture.isPresent()) {
            Response.status(Status.NOT_FOUND);
        }
        dataLine.setId(oldGesture.get().getId());
        try {
            Gesture updated = gestureService.update(dataLine);
            builder = Response.ok(updated);
        } catch (Exception e) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }
        return builder.build();
    }

}
