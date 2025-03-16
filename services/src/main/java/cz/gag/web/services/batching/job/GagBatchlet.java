package cz.gag.web.services.batching.job;

//import cz.muni.gag.web.dao.???Dao;
//import cz.muni.gag.web.entity.???;
import cz.gag.web.services.logging.Log;
//import cz.muni.gag.web.rest.client.???RestClient;

import javax.batch.api.Batchlet;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 * @author Vojtech Prusa
 *
 * TODO here could actually by gesture filtering to obtain new gesture with referential data for recongition
 * because that may take soooo long
 *
 */
@Named("commentaryBatchlet")
@ApplicationScoped
public class GagBatchlet implements Batchlet {

    // @Inject
    // ???Dao artistDao;

    // @Inject
    // private ???RestClient lastFmRestClient;

    @Override
    public String process() throws Exception {
        Log.info(getClass(), "process");
        // TODO write process
        return "END";
    }

    @Override
    public void stop() throws Exception {
        Log.info(getClass(), "stop");
    }

}
