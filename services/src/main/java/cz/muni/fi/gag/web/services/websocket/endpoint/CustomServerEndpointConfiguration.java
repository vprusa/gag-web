package cz.muni.fi.gag.web.services.websocket.endpoint;

import org.jboss.logging.Logger;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

// https://stackoverflow.com/questions/30729287/websocket-java-ee-how-to-get-role-of-current-user/30745128
public class CustomServerEndpointConfiguration extends ServerEndpointConfig.Configurator {

    public static final Logger log = Logger.getLogger(CustomServerEndpointConfiguration.class.getSimpleName());

    @Override
    public void modifyHandshake(ServerEndpointConfig config,
                                HandshakeRequest request,
                                HandshakeResponse response) {
        log.debug("CustomServerEndpointConfiguration.modifyHandshake: " + request.getUserPrincipal());
        config.getUserProperties().put("UserPrincipal", request.getUserPrincipal());
        // TODO .. enable or deal in @OnOpen
//        config.getUserProperties().put("userInRole", request.isUserInRole(""));
    }

    public Class<?> getEndpointClass() {
        return DataLineWsEndpoint.class;
    }

    public ServerEndpointConfig.Configurator getConfigurator() {
        return this;
    }

}