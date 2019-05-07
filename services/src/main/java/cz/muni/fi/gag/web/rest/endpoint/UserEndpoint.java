package cz.muni.fi.gag.web.rest.endpoint;

import cz.muni.fi.gag.web.entity.UserRole;
import cz.muni.fi.gag.web.entity.User;
import cz.muni.fi.gag.web.service.UserService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * @author Vojtech Prusa
 *
 */
@Path("user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserEndpoint {

    @Inject
    private UserService userService;

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(User user) {
        Response.ResponseBuilder builder;
        try {
            user.setType(UserRole.USER);
            User created = userService.create(user);
            builder = Response.ok(created);
        } catch (Exception e) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }
        return builder.build();
    }

    @GET
    @Path("/logout")
    public Response logout(@Context HttpServletRequest req) {
        Response.ResponseBuilder builder;
        try {
            req.getSession().invalidate();
            builder = Response.ok();
        } catch (Exception e) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }
        return builder.build();
    }

    @GET
    @Path("/role")
    public Response getRole(@Context HttpServletRequest req) {
        Response.ResponseBuilder builder;
        try {
            builder = Response.ok(userService.findByEmail(req.getRemoteUser()).getType());
        } catch (Exception e) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }
        return builder.build();
    }

    @GET
    public Response get() {
        return Response.ok(userService.findAll()).build();
    }

}
