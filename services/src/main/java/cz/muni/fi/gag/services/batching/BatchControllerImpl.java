package cz.muni.fi.gag.services.batching;

import cz.muni.fi.gag.services.logging.Log;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.enterprise.context.ApplicationScoped;
import java.util.Properties;

/**
 * @author Vojtech Prusa
 *
 */
@ApplicationScoped
public class BatchControllerImpl implements BatchController {

    private String commentaryJobName = "job";

    @Override
    public void startJob() {
        Properties jobParams = new Properties();

        JobOperator jobOperator = BatchRuntime.getJobOperator();
        long jobId = jobOperator.start(commentaryJobName, jobParams);
        Log.info("Job id" + jobId);
    }

}
