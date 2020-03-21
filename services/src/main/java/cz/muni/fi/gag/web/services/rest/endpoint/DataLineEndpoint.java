package cz.muni.fi.gag.web.services.rest.endpoint;

import cz.muni.fi.gag.web.persistence.entity.DataLine;
import cz.muni.fi.gag.web.services.service.DataLineService;

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

import static cz.muni.fi.gag.web.persistence.entity.UserRole.USER_R;

/**
 * @author Vojtech Prusa
 *
 */
@Path("/dataline")
public class DataLineEndpoint {

    @Inject
    private DataLineService dataLineService;

    @GET
    @Path("/plain/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDataLineById(@PathParam("id") Long id) {
        Optional<DataLine> dataLine = dataLineService.findById(id);
        if (!dataLine.isPresent()) {
            Response.status(Status.NOT_FOUND);
        }
        return Response.ok(dataLine.get()).build();
    }
    
    @GET
    @Path("/gesture/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDataLinesByGestureId(@PathParam("id") Long gestureId) {
        List<DataLine> dls = dataLineService.findByGestureId(gestureId);
        if (dls.isEmpty()) {
            Response.status(Status.NOT_FOUND);
        }
        return Response.ok(dls).build();
    }

    @GET
    @Path("/clearGesture/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearGesture(@PathParam("id") Long gestureId) {
        int count = dataLineService.removeBy(gestureId);
        if(count <= 0) {
            Response.status(Status.NOT_FOUND);
        }
        return Response.ok(count).build();
    }

    @GET
    @Path("/interesting/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response interesting(@PathParam("id") Long gestureId) {
        List<DataLine> dls = dataLineService.getInteresting(gestureId);
        if (dls.isEmpty()) {
            Response.status(Status.NOT_FOUND);
        }
        return Response.ok(dls).build();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(USER_R)
    public Response createDataLine(DataLine dataLine) {
        Response.ResponseBuilder builder;
        try {
            DataLine created = dataLineService.create(dataLine);
            builder = Response.ok(created);
        } catch (Exception e) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }
        return builder.build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed(USER_R)
    public Response removeDataLine(@PathParam("id") Long id) throws Exception {
        Response.ResponseBuilder builder;
        Optional<DataLine> dataLine = dataLineService.findById(id);
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
    public Response updateDataLine(@PathParam("id") Long id, DataLine dataLine) {
        Response.ResponseBuilder builder;
        Optional<DataLine> oldDataLine = dataLineService.findById(id);
        if (!oldDataLine.isPresent()) {
            Response.status(Status.NOT_FOUND);
        }
        dataLine.setId(oldDataLine.get().getId());
        try {
            DataLine updated = dataLineService.update(dataLine);
            builder = Response.ok(updated);
        } catch (Exception e) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }
        return builder.build();
    }

}
