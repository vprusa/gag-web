package cz.muni.fi.gag.web.websocket.endpoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cz.muni.fi.gag.web.entity.DataLine;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import java.util.List;

/**
 * @author Vojtech Prusa
 *
 */
public class DataLineSerializer implements Encoder.Text<List<DataLine.Aggregate>> {
    private ObjectMapper objectMapper;

    @Override
    public String encode(List<DataLine.Aggregate> object) throws EncodeException {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new EncodeException(object, e.getMessage(), e);
        }
    }

    @Override
    public void init(EndpointConfig config) {
        objectMapper = new ObjectMapper();
    }

    @Override
    public void destroy() {

    }
}
