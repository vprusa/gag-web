package cz.gag.web.services.rest.endpoint;

import cz.gag.web.persistence.entity.HandDevice;
import cz.gag.web.services.service.HandDeviceService;

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

import java.util.Optional;

import static cz.gag.web.persistence.entity.UserRole.USER_R;

/**
 * @author Vojtech Prusa
 *
 */
@Path("/handdevice")
public class HandDeviceEndpoint {

    @Inject
    private HandDeviceService dataLineService;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHandDeviceById(@PathParam("id") Long id) {
        Optional<HandDevice> dataLine = dataLineService.findById(id);
        if (!dataLine.isPresent()) {
            Response.status(Status.NOT_FOUND);
        }
        return Response.ok(dataLine.get()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(USER_R)
    public Response createHandDevice(HandDevice dataLine) {
        Response.ResponseBuilder builder;
        try {
            HandDevice created = dataLineService.create(dataLine);
            builder = Response.ok(created);
        } catch (Exception e) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }
        return builder.build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed(USER_R)
    public Response removeHandDevice(@PathParam("id") Long id) throws Exception {
        Response.ResponseBuilder builder;
        Optional<HandDevice> dataLine = dataLineService.findById(id);
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
    public Response updateHandDevice(@PathParam("id") Long id, HandDevice dataLine) {
        Response.ResponseBuilder builder;
        Optional<HandDevice> oldHandDevice = dataLineService.findById(id);
        if (!oldHandDevice.isPresent()) {
            Response.status(Status.NOT_FOUND);
        }
        dataLine.setId(oldHandDevice.get().getId());
        try {
            HandDevice updated = dataLineService.update(dataLine);
            builder = Response.ok(updated);
        } catch (Exception e) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }
        return builder.build();
    }

}
