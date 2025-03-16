package cz.gag.web.services.rest.endpoint;

import cz.gag.web.persistence.dao.impl.UserDaoImpl;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * @author Vojtech Prusa
 *
 */
@Path("/ds")
public class DataSourcingEndpoint extends BaseEndpoint {

    @Inject
    private UserDaoImpl userDao;

    @GET
    @Path("/export")
    @Produces(MediaType.APPLICATION_JSON)
    public Response export(/*@PathParam("table") String table*/) {
//        try (Connection c = userDao.getEm().getConnection(); Statement s = c.createStatement()) {
        try {
            //Session session = (Session)userDao.getEm().unwrap(Session.class).connection();
            //Session session = (Session)userDao.getEm().unwrap(Session.class).connection();
            //Connection conn = session.createConnection(true);
            String filePath = "/home/vprusa/test.sql";
            Session session = (Session) userDao.getEm().getDelegate();
            NativeQuery nq = session.createSQLQuery("SCRIPT TO '" + filePath + "'");
            boolean callable =  nq.isCallable();
            int i = nq.executeUpdate();

            //String contents = new String(Files.readAllBytes(Paths.get(filePath)));
            return Response.ok("ok callable: " + callable + " i: " + i).build();
        } catch (Exception e/*SQLException |IOException e*/) {
            e.printStackTrace();
        }
        return Response.ok("Err?").build();
    }

    /*
    @GET
    @Path("/import")
    @Produces(MediaType.APPLICATION_JSON)
    public Response _import(/*@PathParam("table") String table * /) {

        try (Connection c = userDao.getEm().getConnection(); Statement s = c.createStatement()) {

            String filePath = "./test.sql";
            //s.execute("SCRIPT TO '/home/artur/tmp/test/test.sql'");
            // TODO change SCRIPT TO to import data.... with dropping/overriding previous as endpoint Param?
            s.execute("SCRIPT TO '"+filePath+"'");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Response.ok("Err?").build();
    }*/

}