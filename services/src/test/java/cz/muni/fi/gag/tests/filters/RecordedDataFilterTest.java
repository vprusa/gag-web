package cz.muni.fi.gag.tests.filters;

import cz.muni.fi.gag.tests.common.TestServiceBase;
import cz.muni.fi.gag.web.persistence.entity.Gesture;
import cz.muni.fi.gag.web.services.filters.RecordedDataFilterImpl;
import cz.muni.fi.gag.web.services.service.GestureService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;

/**
 * @author Vojtech Prusa
 *
 */
@RunWith(Arquillian.class)
public class RecordedDataFilterTest extends TestServiceBase {
//        DataFilterEndpointTestBase {

    private static Logger log = Logger.getLogger(RecordedDataFilterTest.class.getSimpleName());

    @Deployment
    public static WebArchive deployment() {
//        return getDeployment(cz.muni.fi.gag.web.filters.RecordedDataFilterTest.class);
//                .addPackage(GestureServiceImpl.class.getPackage());
//                .addAsServiceProviderAndClasses(GestureService.class, GestureServiceImpl.class);
//                .addManifestResource(EmptyAsset.INSTANCE, "beans.xml");

        Class clazz = RecordedDataFilterTest.class;

        File[] filesKeycloak = Maven.resolver()
                .resolve(keycloakGroupId + "keycloak-core" + keycloakVersion,
                        keycloakGroupId + "keycloak-common" + keycloakVersion,
                        keycloakGroupId + "keycloak-adapter-core" + keycloakVersion,
                        keycloakGroupId + "keycloak-adapter-spi" + keycloakVersion,
                        keycloakGroupId + "keycloak-client-registration-api" + keycloakVersion)
                .withTransitivity().asFile();

        // https://stackoverflow.com/questions/52200635/arquillian-runclient-test-as-remote
        return ShrinkWrap.create(WebArchive.class, clazz.getSimpleName() + ".war")
                .addPackages(true, "cz.muni.fi.gag.web.persistence")
                .addPackages(true, "cz.muni.fi.gag.web.services")
                .addPackages(true, "cz.muni.fi.gag.tests.common")
                .addPackages(true, "cz.muni.fi.gag.tests.filters")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebResource("index-test.html", "index.html")
                .addAsWebResource("import-test-live.sql", "import.sql")
                // TODO fix testing web.xml so it would either work with KC or with some other managing of roles
                .addAsWebInfResource("web-test.xml", "web.xml")
                .addAsWebInfResource("keycloak-test.json", "keycloak.json")
//                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource("beans-test.xml", "beans.xml")
                .addAsLibraries(filesKeycloak);
    }

    @Inject
    public GestureService gestureService;

    @Inject
//    public RecordedDataFilter rdf;
    public RecordedDataFilterImpl rdf;

    @Test
//    @RunAsClient
    public void testRecordedDataFilter() {
        log.info("testRecordedDataFilter");
        Long id = 21L;
        log.info((gestureService == null ? "gestureService is null" : "gestureService: " + gestureService.toString()));
        log.info((rdf == null ? "rdf is null" : "rdf: " + rdf.toString()));

        Optional<Gesture> gopt = gestureService.findById(id);
        if (!gopt.isPresent()) {
            log.info("Gesture (gopt) is not present");
        } else {
            Gesture g = gopt.get();
            log.info("To filter: " + g.toString());
            Gesture filtered = new Gesture();
            filtered.setId(null);
            filtered.setUserAlias(g.getUserAlias() + "-" + (Math.abs(new Random().nextInt())));
            filtered.setUser(g.getUser());
            filtered.setDateCreated(new Date());
            filtered.setData(Collections.emptyList());
            filtered = gestureService.create(filtered);
            log.info("Created filtered (base): " + filtered.toString());
            rdf.filter(g,filtered, 2, true);

            filtered.setFiltered(true);
            filtered = gestureService.update(filtered);
            log.info("Created filtered (final): " + filtered.toString());
            log.info(filtered.toString());
        }
    }

}
