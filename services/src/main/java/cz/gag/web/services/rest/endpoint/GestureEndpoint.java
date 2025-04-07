package cz.gag.web.services.rest.endpoint;

import cz.gag.web.persistence.entity.*;
import cz.gag.web.services.filters.RecordedDataFilter;
import cz.gag.web.services.service.DataLineService;
import cz.gag.web.services.service.FingerDataLineService;
import cz.gag.web.services.service.GestureService;
import cz.gag.web.services.service.WristDataLineService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cz.gag.web.persistence.entity.UserRole.*;

/**
 * @author Vojtech Prusa
 */
@Path("/gesture/")
public class GestureEndpoint extends BaseEndpoint {

    @Inject
    private GestureService gestureService;

    @Inject
    private DataLineService dataLineService;

    @Inject
    private FingerDataLineService fingerDataLineService;

    @Inject
    private WristDataLineService wristDataLineService;


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
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGestureById(@PathParam("id") String identifier) {
//        log.info("getGestureById");
        try {
            Optional<Gesture> gestureOpt = gestureService.findById(Long.parseLong(identifier));
            if (!gestureOpt.isPresent()) {
                Response.status(Status.NOT_FOUND);
            }
//            log.info("getGestureById");
            return Response.ok(gestureOpt.get()).build();
        } catch (NumberFormatException ex) {
            User u = current();
            if (u != null) {
                List<Gesture> myGestures = gestureService.findByUser(u);
//                log.info("getGestureByUser");
                return Response.ok(myGestures).build();
            }
            return getResponseNotLoggedIn();
        }
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
//    @RolesAllowed(USER_R)
    @RolesAllowed({ANONYMOUS_R, USER_R, ADMIN_R})
    @Path("from/{oldGestureId}/{newAlias}")
    public Response gestureFrom(@PathParam("oldGestureId") Long oldGestureId, @PathParam("newAlias") String newAlias, List<Long> dataLineIds) {
        // $http.post("/gagweb/api/gesture-from/" + oldGestureId + "/" + newAlias, dataLineIds).then(function (response) {
        log.info("Creating gestureFrom oldGestureId:" + oldGestureId + " newAlias: " + newAlias);
        Response.ResponseBuilder builder;
        try {
            Gesture g = new Gesture();
            g.setUserAlias(newAlias);
            g.setFiltered(true);
            g.setDateCreated(new Date());
            g.setUser(current());
            Gesture newGesture = gestureService.create(g);
            List<DataLine> oldDLs = dataLineService.findByGestureId(oldGestureId);
            // Filter out only the data lines that match the provided IDs
            List<DataLine> filteredOldDataLines = oldDLs.stream()
                    .filter(dl -> dataLineIds.contains(dl.getId()))
                    .collect(Collectors.toList());

            for (DataLine oldDataLine : filteredOldDataLines) {
                // check if this will override the old data, i do not want to override old dataline gestureId, I want to create new dataline from the old
                DataLine newDataLine = oldDataLine.deepCopy();
                // Save the newly created data line
                // TODO check if I need save dataline and [Wrist|Finger]DataLines, or just [Wrist|Finger]DataLines,
                //  forgot the javax datastructure inheritance
                DataLine savedDataLine = dataLineService.create(newDataLine);
                if (oldDataLine.getPosition() == Sensor.WRIST) {
                    WristDataLine oldWristDataLine = wristDataLineService.findById(oldDataLine.getId()).get();
                    WristDataLine newWristDataLine = oldWristDataLine.deepCopy();
                    newWristDataLine.setGesture(newGesture);
                    newWristDataLine = wristDataLineService.create(newWristDataLine);
                } else {
                    // finger
                    FingerDataLine oldFingerDataLine = fingerDataLineService.findById(oldDataLine.getId()).get();
                    FingerDataLine newFingerDataLine = oldFingerDataLine.deepCopy();
                    newFingerDataLine.setGesture(newGesture);
                    newFingerDataLine = fingerDataLineService.create(newFingerDataLine);
//                    DataLine savedDataLine = dataLineService.create(newDataLine);
                }
            }
            builder = Response.ok(newGesture);
        } catch (Exception e) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }
        return builder.build();
    }

    // TODO add consumption of json object of now gesture data
    @POST
    //@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(USER_R)
    @Path("create/{userAlias}")
    public Response createGesture(@PathParam("userAlias") String userAlias) {
        Response.ResponseBuilder builder;
        try {
            Gesture g = new Gesture();
            g.setUserAlias(userAlias);
            g.setFiltered(false);
            g.setDateCreated(new Date());
            g.setUser(current());
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
        Optional<Gesture> g = gestureService.findById(id);
        if (!g.isPresent()) {
            Response.status(Status.NOT_FOUND);
        }
        try {
            gestureService.remove(g.get());
            builder = Response.ok();
        } catch (Exception e) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }
        return builder.build();
    }

    @PUT
    @Path("/setGestureActive/{id}/{active}")
    @RolesAllowed(USER_R)
    public Response setGestureActive(@PathParam("id") Long id, @PathParam("active") Long active) throws Exception {
        Response.ResponseBuilder builder;
        Optional<Gesture> gOpt = gestureService.findById(id);
        if (!gOpt.isPresent()) {
            Response.status(Status.NOT_FOUND);
        }
        try {
            Gesture g = gOpt.get();
            g.setActive(active != 0);
            gestureService.update(g);
            builder = Response.ok();
        } catch (Exception e) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }
        return builder.build();
    }

    @PUT
    @Path("/setGestureShouldMatch/{id}/{active}")
    @RolesAllowed(USER_R)
    public Response setGestureShouldMatch(
            @PathParam("id") Long id,
            @PathParam("active") Float shouldMatch
    ) throws Exception {
        Response.ResponseBuilder builder;
        Optional<Gesture> gOpt = gestureService.findById(id);
        if (!gOpt.isPresent()) {
            Response.status(Status.NOT_FOUND);
        }
        try {
            Gesture g = gOpt.get();
            g.setShouldMatch(shouldMatch);
            gestureService.update(g);
            builder = Response.ok();
        } catch (Exception e) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }
        return builder.build();
    }

    @DELETE
    @Path("/clear/{id}")
    @RolesAllowed(USER_R)
    public Response clearGesture(@PathParam("id") Long id) throws Exception {
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

    @Inject
    private RecordedDataFilter rdf;

    @PUT
    @Path("/filter/{id}/{newGestureName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(USER_R)
    public Response filterGesture(@PathParam("id") Long id, @PathParam("newGestureName") String newGestureName) {
        Response.ResponseBuilder builder;
        Optional<Gesture> gopt = gestureService.findById(id);
        if (!gopt.isPresent()) {
            Response.status(Status.NOT_FOUND);
            builder = Response.noContent();
        } else {
            Gesture g = gopt.get();
            Gesture filtered = g;
            filtered.setId(null);
            filtered.setUserAlias(g.getUserAlias() + "-" + newGestureName);
            filtered.setDateCreated(new Date());
            filtered.setData(Collections.emptyList());
            filtered = gestureService.create(filtered);
            //RecordedDataFilter<DataLine> rdf = new RecordedDataFilter<DataLine>(g, filtered);
            rdf.filter(g, filtered, 2, true);
            filtered = gestureService.update(filtered);
            builder = Response.ok(filtered);
        }
        return builder.build();
    }

}
