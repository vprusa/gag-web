package cz.muni.fi.gag.web.rest.endpoint;

import java.util.Iterator;
import java.util.Set;

import org.jboss.logging.Logger;
//import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;

import cz.muni.fi.gag.web.entity.User;
import cz.muni.fi.gag.web.entity.UserRole;
import cz.muni.fi.gag.web.service.UserService;

/**
 * @author Vojtech Prusa
 *
 * @UserEndpoint
 * @GestureEndpoint
 *
 */
public class BaseEndpoint {
   
    protected static final Logger log = Logger.getLogger(BaseEndpoint.class.getSimpleName());
    
    @Inject
    private UserService userService;

    @Context
    SecurityContext sc;
    
    public User currentUser() {
        // https://stackoverflow.com/questions/31864062/
        // fetch-logged-in-username-in-a-webapp-secured-with-keycloak

        User currentUser = null;
        if (sc.getUserPrincipal() instanceof KeycloakPrincipal) {
            @SuppressWarnings("unchecked")
            KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) sc
                    .getUserPrincipal();
            // this is how to get the real userName (or rather the login name)
            User existingUser = null;
            currentUser = new User();
            AccessToken at = kp.getKeycloakSecurityContext().getToken();

            currentUser.setThirdPartyIdAsEmail(at.getEmail());
            // TODO fix should not be necessary here ..
            if ((existingUser = userService.findByIdentificator(currentUser.getThirdPartyId())) == null) {
                Set<String> currentUserRoles = at.getRealmAccess().getRoles();
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
                currentUser = userService.create(currentUser);
                return currentUser;
            }
            return existingUser;
        } else {
            if (sc.getUserPrincipal() != null) {
                currentUser = userService.findByIdentificator(sc.getUserPrincipal().getName());
                return currentUser;
            }
        }
        return null;
    }
    
    
    public Response getResponseNotLoggedIn(){
        return Response.ok("Not logged in").build();
    }
}
