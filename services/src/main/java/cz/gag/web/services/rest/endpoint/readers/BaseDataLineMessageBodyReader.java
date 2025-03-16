package cz.gag.web.services.rest.endpoint.readers;

import cz.gag.web.persistence.entity.DataLine;
import cz.gag.web.persistence.entity.Gesture;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * https://memorynotfound.com/jax-rs-messagebodyreader/
 * */

//@Provider
//@Consumes("application/xml")
class BaseDataLineMessageBodyReader<T extends DataLine> implements MessageBodyReader<T> {

    @Inject
    private EntityManager em;

    @Override
    public boolean isReadable(Class<?> type, Type genericType,
                              Annotation[] annotations, MediaType mediaType) {
        return type == DataLine.class;
    }

    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations,
                         MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                         InputStream entityStream) throws IOException, WebApplicationException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(type);
            Long gestureId = (Long) jaxbContext.createUnmarshaller().getProperty("gesture");
            Gesture gestureRef = em.getReference(Gesture.class,gestureId);
            return (T) jaxbContext.createUnmarshaller().unmarshal(entityStream);
        } catch (JAXBException e) {
            throw new ProcessingException("Error deserializing user.", e);
        }
    }
}