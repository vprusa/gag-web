package cz.gag.web.services.websocket.endpoint.config;

import cz.gag.web.services.websocket.endpoint.BaseWSEndpoint;
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

//    public Class<?> getEndpointClass() {
//        return DataLineWsEndpoint.class;
//    }
    public Class<?> getEndpointClass() {
        return BaseWSEndpoint.class;
    }

    public ServerEndpointConfig.Configurator getConfigurator() {
        return this;
    }

}