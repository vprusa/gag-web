package cz.muni.fi.gag.web.services.rest.endpoint;

import cz.muni.fi.gag.web.persistence.entity.FingerDataLine;
import cz.muni.fi.gag.web.services.service.FingerDataLineService;
import cz.muni.fi.gag.web.services.service.GestureService;
import cz.muni.fi.gag.web.services.mapped.MFingerDataLine;
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

import static cz.muni.fi.gag.web.persistence.entity.UserRole.USER_R;

/**
 * @author Vojtech Prusa
 *
 */
@Path("/fingerdataline")
public class FingerDataLineEndpoint {

    @Inject
    private FingerDataLineService fingerDataLineService;
    @Inject
    private GestureService gestureService;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFingerDataLineById(@PathParam("id") Long id) {
        Optional<FingerDataLine> fingerDataLine = fingerDataLineService.findById(id);
        if (!fingerDataLine.isPresent()) {
            Response.status(Status.NOT_FOUND);
        }
        return Response.ok(fingerDataLine.get()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed(USER_R)
    public Response createFingerDataLine(MFingerDataLine mfdl) {
        FingerDataLine fdl = mfdl.getEntity(gestureService);

        Response.ResponseBuilder builder;
        try {
            FingerDataLine created = fingerDataLineService.create(fdl);
            builder = Response.ok(created);
        } catch (Exception e) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }
        return builder.build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed(USER_R)
    public Response removeFingerDataLine(@PathParam("id") Long id) throws Exception {
        Response.ResponseBuilder builder;
        Optional<FingerDataLine> fingerDataLine = fingerDataLineService.findById(id);
        if (!fingerDataLine.isPresent()) {
            Response.status(Status.NOT_FOUND);
        }
        try {
            fingerDataLineService.remove(fingerDataLine.get());
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
    public Response updateFingerDataLine(@PathParam("id") Long id, FingerDataLine fingerDataLine) {
        Response.ResponseBuilder builder;
        Optional<FingerDataLine> oldFingerDataLine = fingerDataLineService.findById(id);
        if (!oldFingerDataLine.isPresent()) {
            Response.status(Status.NOT_FOUND);
        }
        fingerDataLine.setId(oldFingerDataLine.get().getId());
        try {
            FingerDataLine updated = fingerDataLineService.update(fingerDataLine);
            builder = Response.ok(updated);
        } catch (Exception e) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }
        return builder.build();
    }

}