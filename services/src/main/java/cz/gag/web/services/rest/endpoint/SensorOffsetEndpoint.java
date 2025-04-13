package cz.gag.web.services.rest.endpoint;

import cz.gag.web.persistence.entity.FingerSensorOffset;
import cz.gag.web.persistence.entity.Sensor;
import cz.gag.web.persistence.entity.SensorOffset;
import cz.gag.web.persistence.entity.WristSensorOffset;
import cz.gag.web.services.service.SensorOffsetService;

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

import static cz.gag.web.persistence.entity.UserRole.USER_R;

/**
 * @author Vojtech Prusa
 *
 */
@Path("/sensoroffset")
public class SensorOffsetEndpoint {

    @Inject
    private SensorOffsetService sensorOffsetService;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorOffsetById(@PathParam("id") Long id) {
        Optional<SensorOffset> dataLine = sensorOffsetService.findById(id);
        if (!dataLine.isPresent()) {
            Response.status(Status.NOT_FOUND);
        }
        return Response.ok(dataLine.get()).build();
    }

    @GET
    @Path("/specific/{handDevice}/{position}/{sensorType}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorOffsetBySensorType(
            @PathParam("handDevice") Long handDevice,
            @PathParam("position") Long position,
            @PathParam("sensorType") Long sensorType
    ) {
        List<SensorOffset> sensorOffsets = sensorOffsetService.findByOffsetsAndPosition(
                handDevice,
                position,
                sensorType
        );

        if (sensorOffsets.isEmpty()) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(sensorOffsets.get(0)).build();
    }

    @POST
    @Path("/specific/{handDevice}/{position}/{sensorType}/{values}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getSensorOffsetBySensorType(
            @PathParam("handDevice") Long handDevice,
            @PathParam("position") Long position,
            @PathParam("sensorType") Long sensorType,
            @PathParam("values") List<Integer> values
    ) {
        List<SensorOffset> sensorOffsets = sensorOffsetService.findByOffsetsAndPosition(
                handDevice,
                position,
                sensorType
        );

        if (position == Sensor.WRIST.ordinal()) { // TODO rework to String types?
            WristSensorOffset sensorOffset = new WristSensorOffset();
            sensorOffset.setX(values.get(0).shortValue());
            sensorOffset.setY(values.get(1).shortValue());
            sensorOffset.setZ(values.get(2).shortValue());
            if (sensorOffsets.isEmpty()) {
                sensorOffsetService.create(sensorOffset);
            } else {
                sensorOffset.setId(sensorOffsets.get(0).getId());
                sensorOffsetService.update(sensorOffset);
            }
            return Response.ok(sensorOffsets.get(0)).build();
        } else {
            FingerSensorOffset sensorOffset = new FingerSensorOffset();
            sensorOffset.setX(values.get(0).shortValue());
            sensorOffset.setY(values.get(1).shortValue());
            sensorOffset.setZ(values.get(2).shortValue());
            if (sensorOffsets.isEmpty()) {
                sensorOffsetService.create(sensorOffset);
            } else {
                sensorOffset.setId(sensorOffsets.get(0).getId());
                sensorOffsetService.update(sensorOffset);
            }
            return Response.ok(sensorOffsets.get(0)).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(USER_R)
    public Response createSensorOffset(SensorOffset dataLine) {
        Response.ResponseBuilder builder;
        try {
            SensorOffset created = sensorOffsetService.create(dataLine);
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
        Optional<SensorOffset> dataLine = sensorOffsetService.findById(id);
        if (!dataLine.isPresent()) {
            Response.status(Status.NOT_FOUND);
        }
        try {
            sensorOffsetService.remove(dataLine.get());
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
        Optional<SensorOffset> oldSensorOffset = sensorOffsetService.findById(id);
        if (!oldSensorOffset.isPresent()) {
            Response.status(Status.NOT_FOUND);
        }
        dataLine.setId(oldSensorOffset.get().getId());
        try {
            SensorOffset updated = sensorOffsetService.update(dataLine);
            builder = Response.ok(updated);
        } catch (Exception e) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }
        return builder.build();
    }

}
