package cz.muni.fi.gag.services.rest.endpoint;

import cz.muni.fi.gag.web.entity.SensorOffset;
import cz.muni.fi.gag.services.service.SensorOffsetService;

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

import static cz.muni.fi.gag.web.entity.UserRole.USER_R;

/**
 * @author Vojtech Prusa
 *
 */
@Path("/sensoroffset")
public class SensorOffsetEndpoint {

    @Inject
    private SensorOffsetService dataLineService;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorOffsetById(@PathParam("id") Long id) {
        Optional<SensorOffset> dataLine = dataLineService.findById(id);
        if (!dataLine.isPresent()) {
            Response.status(Status.NOT_FOUND);
        }
        return Response.ok(dataLine.get()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(USER_R)
    public Response createSensorOffset(SensorOffset dataLine) {
        Response.ResponseBuilder builder;
        try {
            SensorOffset created = dataLineService.create(dataLine);
            builder = Response.ok(created);
        } catch (Exception e) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }
        return builder.build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed(USER_R)
    public Response removeSensorOffset(@PathParam("id") Long id) throws Exception {
        Response.ResponseBuilder builder;
        Optional<SensorOffset> dataLine = dataLineService.findById(id);
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
    public Response updateSensorOffset(@PathParam("id") Long id, SensorOffset dataLine) {
        Response.ResponseBuilder builder;
        Optional<SensorOffset> oldSensorOffset = dataLineService.findById(id);
        if (!oldSensorOffset.isPresent()) {
            Response.status(Status.NOT_FOUND);
        }
        dataLine.setId(oldSensorOffset.get().getId());
        try {
            SensorOffset updated = dataLineService.update(dataLine);
            builder = Response.ok(updated);
        } catch (Exception e) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }
        return builder.build();
    }

}
