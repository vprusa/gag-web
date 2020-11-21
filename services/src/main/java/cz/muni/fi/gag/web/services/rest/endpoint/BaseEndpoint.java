package cz.muni.fi.gag.web.services.rest.endpoint;

import cz.muni.fi.gag.web.persistence.entity.User;
import cz.muni.fi.gag.web.persistence.entity.UserRole;
import cz.muni.fi.gag.web.services.service.UserService;
import org.jboss.logging.Logger;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Vojtech Prusa
 * @UserEndpoint
 * @GestureEndpoint
 */
public class BaseEndpoint {

    protected static final Logger log = Logger.getLogger(BaseEndpoint.class.getSimpleName());

    @Inject
    private UserService userService;

    @Context
    SecurityContext sc;

    public SecurityContext getSc() {
        return sc;
    }

    // This is a workaround for being unable to @Inject SecurityContext in JAX-WS (in JAX-RS injection works...)
    // TODO add custom EJB that is fed via CustomServerEndpointConfiguration data propagation or smth...
    public static User current(Principal principal, UserService userService) {
        User currentUser = null;
        if (principal instanceof KeycloakPrincipal) {
            @SuppressWarnings("unchecked")
            KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) principal;
            // this is how to get the real userName (or rather the login name)
            AccessToken at = kp.getKeycloakSecurityContext().getToken();
            // TODO move appending prefix 'User.THIRD_PARTY_ID_EMAIL_PREFIX +' to deeper/lower backend/layer
            User existingUser = userService.findByIdentificator(User.THIRD_PARTY_ID_EMAIL_PREFIX + at.getEmail());
            if (existingUser == null) {
                Set<String> currentUserRoles = at.getRealmAccess().getRoles();
                currentUser = new User();
                currentUser.setRole(UserRole.ANONYMOUS);
                Iterator<String> currentUserRolesIterator = currentUserRoles.iterator();
                while (currentUserRolesIterator.hasNext()) {
                    String role = currentUserRolesIterator.next();
                    if (role.matches(UserRole.USER_R)) {
                        currentUser.setRole(UserRole.USER);
                    } else if (role.matches(UserRole.ADMIN_R)) {
                        currentUser.setRole(UserRole.ADMIN);
                    }
                }
                currentUser.setThirdPartyIdAsEmail(at.getEmail());
                currentUser = userService.create(currentUser);
                return currentUser;
            }
            return existingUser;
        } else {
            if (principal != null) {
                currentUser = userService.findByIdentificator(principal.getName());
                return currentUser;
            }
        }
        return null;
    }

    public User current() {
        // https://stackoverflow.com/questions/31864062/
        // fetch-logged-in-username-in-a-webapp-secured-with-keycloak
        return current(getSc().getUserPrincipal(), userService);
    }

    public Response getResponseNotLoggedIn() {
        return Response.ok("Not logged in").build();
    }
}
