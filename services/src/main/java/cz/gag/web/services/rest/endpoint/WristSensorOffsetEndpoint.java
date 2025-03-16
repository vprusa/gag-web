package cz.gag.web.services.rest.endpoint;

import cz.gag.web.persistence.entity.WristSensorOffset;
import cz.gag.web.services.service.WristSensorOffsetService;

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
@Path("/wristsensoroffset")
public class WristSensorOffsetEndpoint {

    @Inject
    private WristSensorOffsetService dataLineService;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWristSensorOffsetById(@PathParam("id") Long id) {
        Optional<WristSensorOffset> dataLine = dataLineService.findById(id);
        if (!dataLine.isPresent()) {
            Response.status(Status.NOT_FOUND);
        }
        return Response.ok(dataLine.get()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(USER_R)
    public Response createWristSensorOffset(WristSensorOffset dataLine) {
        Response.ResponseBuilder builder;
        try {
            WristSensorOffset created = dataLineService.create(dataLine);
            builder = Response.ok(created);
        } catch (Exception e) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }
        return builder.build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed(USER_R)
    public Response removeWristSensorOffset(@PathParam("id") Long id) throws Exception {
        Response.ResponseBuilder builder;
        Optional<WristSensorOffset> dataLine = dataLineService.findById(id);
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
    public Response updateWristSensorOffset(@PathParam("id") Long id, WristSensorOffset dataLine) {
        Response.ResponseBuilder builder;
        Optional<WristSensorOffset> oldWristSensorOffset = dataLineService.findById(id);
        if (!oldWristSensorOffset.isPresent()) {
            Response.status(Status.NOT_FOUND);
        }
        dataLine.setId(oldWristSensorOffset.get().getId());
        try {
            WristSensorOffset updated = dataLineService.update(dataLine);
            builder = Response.ok(updated);
        } catch (Exception e) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }
        return builder.build();
    }

}
