package cz.muni.fi.gag.tests.filters;

import cz.muni.fi.gag.web.entity.DataLine;
import cz.muni.fi.gag.web.entity.Gesture;
import cz.muni.fi.gag.web.filters.RecordedDataFilter;
import cz.muni.fi.gag.web.service.GestureService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.Testable;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
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
public class RecordedDataFilterTest
        //extends
//        DataLineEndpointTest {
//        EndpointTestBase<Gesture> {
//        DataFilterTestBase {
        //TestServiceBase {
{

    private static Logger log = Logger.getLogger(RecordedDataFilterTest.class.getSimpleName());

//    public static final String TESTED_ENDPOINT = API_ENDPOINT + "gesture/filter/21/newGesture" + new Random().nextInt();

    @Deployment
    public static WebArchive deployment() {
//        return getDeployment(cz.muni.fi.gag.web.filters.RecordedDataFilterTest.class);

//                .addPackage(GestureServiceImpl.class.getPackage());
//                .addAsServiceProviderAndClasses(GestureService.class, GestureServiceImpl.class);
//                .addManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        Class clazz = RecordedDataFilterTest.class;
        File[] persistenceFiles = Maven.resolver()
                .loadPomFromFile("../persistence/pom.xml")
//                .importCompileAndRuntimeDependencies()
                .importRuntimeAndTestDependencies()
                .resolve()
                .withTransitivity()
                .asFile();

        File[] serviceFiles = Maven.resolver()
                .loadPomFromFile("../pom.xml")
                .importCompileAndRuntimeDependencies()
                .resolve()
                .withTransitivity()
                .asFile();
/*
        File[] filesKeycloak = Maven.resolver()
                .resolve(keycloakGroupId + "keycloak-core" + keycloakVersion,
                        keycloakGroupId + "keycloak-common" + keycloakVersion,
                        keycloakGroupId + "keycloak-adapter-core" + keycloakVersion,
                        keycloakGroupId + "keycloak-adapter-spi" + keycloakVersion,
                        keycloakGroupId + "keycloak-client-registration-api" + keycloakVersion)
                .withTransitivity().asFile();
*/
        // https://stackoverflow.com/questions/52200635/arquillian-runclient-test-as-remote
        return ShrinkWrap.create(WebArchive.class, clazz.getSimpleName() + ".war")
                .addPackages(true, "cz.muni.fi.gag.web.dao")
                .addPackages(true, "cz.muni.fi.gag.web.entity")
                .addPackages(true, "cz.muni.fi.gag.web.validation")
                .addPackages(true, "cz.muni.fi.gag.web.service")
                .addPackages(true, "cz.muni.fi.gag.web.tests.endpoint")
                .addPackages(true, "cz.muni.fi.gag.web.tests.filters")
                .addPackages(true, "cz.muni.fi.gag.web.tests.common")
                .addPackages(true, "cz.muni.fi.gag.web.tests.service")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebResource("index-test.html", "index.html")
//                .addAsWebResource("import-test.sql", "import.sql")
                .addAsWebInfResource("web-test.xml", "web.xml")
                .addAsWebInfResource("keycloak-test.json", "keycloak.json")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
//                .addAsLibraries(persistenceFiles)
//                .addAsLibraries(serviceFiles);
//                .addAsLibraries(filesKeycloak);
    }

//    @Inject
    @EJB
    public GestureService gestureService;

    @Test
//    @RunAsClient
    public void testRecordedDataFilter() {
        log.info("testRecordedDataFilter");
        Long id = 21L;
        log.info((gestureService == null ? "gestureService is null" : gestureService.toString()));

        Optional<Gesture> gopt = gestureService.findById(id);
        if (!gopt.isPresent()) {
            log.info("Not found");
        } else {
            Gesture g = gopt.get();
            Gesture filtered = g;
            filtered.setId(null);
            filtered.setUserAlias(g.getUserAlias() + "-" + new Random().nextInt());
            filtered.setDateCreated(new Date());
            filtered.setData(Collections.emptyList());
            filtered = gestureService.create(filtered);
            RecordedDataFilter<DataLine> rdf = new RecordedDataFilter<DataLine>(g, filtered);
            rdf.filter(2, true);
            filtered = gestureService.update(filtered);
            log.info(filtered.toString());
        }
    }

}
