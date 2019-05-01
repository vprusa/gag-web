package cz.muni.fi.gag.web.service;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * @author Vojtech Prusa
 *
 */
@Singleton
@Startup
public class ApplicationStartUpService {

    // @Inject
    // private ???Service ???Service;

    /**
     * We insert sample data into database, but we don't deal with images. Download
     * them async on application start up
     */
    @PostConstruct
    public void onStartUp() {
        // ???Service.findAll().stream()
        // .filter(??? -> ???.get??????() == null)
        // .forEach(???Service::fetch????????);
    }

}
