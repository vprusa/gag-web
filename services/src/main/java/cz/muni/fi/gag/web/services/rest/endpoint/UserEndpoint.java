package cz.muni.fi.gag.web.services.rest.endpoint;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import cz.muni.fi.gag.web.persistence.entity.User;
import cz.muni.fi.gag.web.persistence.entity.UserRole;
import cz.muni.fi.gag.web.services.service.UserService;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.constants.ServiceUrlConstants;
import org.keycloak.representations.AccessToken;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Vojtech Prusa
 *
 */
@Path("user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserEndpoint extends BaseEndpoint {

    public static final Logger LOG = Logger.getLogger(UserEndpoint.class.getSimpleName());

    @Inject
    private UserService userService;

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(User user) {
        Response.ResponseBuilder builder;
        try {
            user.setRole(UserRole.USER);
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
            // req.getSession().invalidate();
            req.logout();
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
            builder = Response.ok(userService.findByIdentificator(req.getRemoteUser()).getRole());
        } catch (Exception e) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage());
        }
        return builder.build();
    }

    //@Context
    //SecurityContext sc;

    @GET
    @Path("/currentdetail")
    public Response currentdetail() {
        if (getSc().getUserPrincipal() instanceof KeycloakPrincipal) {
            // https://stackoverflow.com/questions/31864062/
            // fetch-logged-in-username-in-a-webapp-secured-with-keycloak
            if (getSc().getUserPrincipal() instanceof KeycloakPrincipal) {
                @SuppressWarnings("unchecked")
                KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) getSc()
                        .getUserPrincipal();
                return Response.ok(kp.getKeycloakSecurityContext().getIdToken()).build();
            }
        }
        return Response.ok("noDetails").build();
    }

    @GET
    @Path("/current")
    public Response currentsimple() {
        User currentUser = current();
        if(currentUser != null) {
            return Response.ok(currentUser).build();
        }
        // TODO fix messages
        return getResponseNotLoggedIn();
    }

    // Some other methods copy-pasted from
    // https://github.com/keycloak/keycloak-quickstarts/
    // blob/latest/app-profile-jee-jsp/src/main/java/org/keycloak/quickstart/profilejee/Controller.java
    // TODO .. idk, remove unnecessary?
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        MAPPER.setSerializationInclusion(Include.NON_NULL);
    }

    static {
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        MAPPER.setSerializationInclusion(Include.NON_NULL);
    }

    public void handleLogout(HttpServletRequest req) throws ServletException {
        if (req.getParameter("logout") != null) {
            req.logout();
        }
    }

    public boolean isLoggedIn(HttpServletRequest req) {
        return getSession(req) != null;
    }

    public boolean showToken(HttpServletRequest req) {
        return req.getParameter("showToken") != null;
    }

    public AccessToken getIDToken(HttpServletRequest req) {
        return getSession(req).getToken();
    }

    public AccessToken getToken(HttpServletRequest req) {
        return getSession(req).getToken();
    }

    public String getAccountUri(HttpServletRequest req) {
        KeycloakSecurityContext session = getSession(req);
        String baseUrl = getAuthServerBaseUrl(req);
        String realm = session.getRealm();
        // TODO idk, fix..? queryParam("referrer", "gagweb")
        return KeycloakUriBuilder.fromUri(baseUrl).path(ServiceUrlConstants.ACCOUNT_SERVICE_PATH)
                .queryParam("referrer", "gagweb").queryParam("referrer_uri", getReferrerUri(req)).build(realm)
                .toString();
    }

    private String getReferrerUri(HttpServletRequest req) {
        StringBuffer uri = req.getRequestURL();
        String q = req.getQueryString();
        if (q != null) {
            uri.append("?").append(q);
        }
        return uri.toString();
    }

    private String getAuthServerBaseUrl(HttpServletRequest req) {
        AdapterDeploymentContext deploymentContext = (AdapterDeploymentContext) req.getServletContext()
                .getAttribute(AdapterDeploymentContext.class.getName());
        KeycloakDeployment deployment = deploymentContext.resolveDeployment(null);
        return deployment.getAuthServerBaseUrl();
    }

    public String getTokenString(HttpServletRequest req) throws IOException {
        return MAPPER.writeValueAsString(getToken(req));
    }

    private KeycloakSecurityContext getSession(HttpServletRequest req) {
        return (KeycloakSecurityContext) req.getAttribute(KeycloakSecurityContext.class.getName());
    }

    // https://stackoverflow.com/questions/31864062/fetch-logged-in-username-in-a-webapp-secured-with-keycloak
    /*
     * @Context SecurityContext sc;
     * 
     * @GET
     * 
     * @Produces(MediaType.APPLICATION_JSON) public User getCurrentUser() {
     * 
     * // this will set the user id as userName String userName =
     * sc.getUserPrincipal().getName();
     * 
     * if (sc.getUserPrincipal() instanceof KeycloakPrincipal) {
     * KeycloakPrincipal<KeycloakSecurityContext> kp =
     * (KeycloakPrincipal<KeycloakSecurityContext>) sc .getUserPrincipal();
     * 
     * // this is how to get the real userName (or rather the login name) userName =
     * kp.getKeycloakSecurityContext().getIdToken().getPreferredUsername(); }
     * 
     * return "{ message : \"Hello " + userName + "\" }"; }
     */

}
