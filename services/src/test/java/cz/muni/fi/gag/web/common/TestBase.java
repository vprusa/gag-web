package cz.muni.fi.gag.web.common;

import cz.muni.fi.gag.web.entity.AbstractEntity;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * @author Vojtech Prusa
 */
public class TestBase {

    private static Logger log = Logger.getLogger(TestBase.class.getSimpleName());

    public static String keycloakGroupId = "org.keycloak:";
    public static String keycloakVersion = ":6.0.1";

    public static WebArchive getDeployment(Class clazz) {

        File[] files = Maven.resolver()
                .loadPomFromFile("../pom.xml")
                .importCompileAndRuntimeDependencies()
                .resolve()
                .withTransitivity()
                .asFile();

        File[] filesKeycloak = Maven.resolver()
                .resolve(keycloakGroupId + "keycloak-core" + keycloakVersion,
                        keycloakGroupId + "keycloak-common" + keycloakVersion,
                        keycloakGroupId + "keycloak-adapter-core" + keycloakVersion,
                        keycloakGroupId + "keycloak-adapter-spi" + keycloakVersion,
                        keycloakGroupId + "keycloak-client-registration-api" + keycloakVersion)
                .withTransitivity().asFile();

        log.info("Loading Dependency Files - KeyCloak");

        for (File file : filesKeycloak) {
            //log.info(file.getAbsolutePath());
        }

        log.info("Loading Dependency Files - other");

        for (File file : files) {
            //log.info(file.getAbsolutePath());
        }

        return ShrinkWrap.create(WebArchive.class, clazz.getSimpleName() + ".war")
                .addPackages(true, "cz.muni.fi.gag.web")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("web.xml", "WEB-INF/web.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsLibraries(files)
                .addAsLibraries(filesKeycloak);
    }

    public void printTestEntities(List<AbstractEntity> entities){
        int i = 0;
        for (Iterator<AbstractEntity> iter = entities.iterator(); iter.hasNext();) {
            log.info("Using " + iter.next().getClass().getSimpleName() + " test" + i + "");
            i++;
        }
    }

}
