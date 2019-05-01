package cz.muni.fi.gag.web.batching.job;

//import cz.muni.gag.web.dao.???Dao;
//import cz.muni.gag.web.entity.???;
import cz.muni.fi.gag.web.logging.LogMessages;
//import cz.muni.gag.web.rest.client.???RestClient;

import javax.batch.api.Batchlet;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 * @author Vojtech Prusa
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
        LogMessages.info(getClass(), "process");

        // TODO write process

        return "END";
    }

    @Override
    public void stop() throws Exception {
        LogMessages.info(getClass(), "stop");
    }

}
