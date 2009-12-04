package org.glite.authz.pap.repository;

import java.io.File;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.glite.authz.pap.common.PAPConfiguration;
import org.glite.authz.pap.repository.dao.filesystem.FileSystemPapDAO;
import org.glite.authz.pap.repository.exceptions.RepositoryException;
import org.hibernate.HibernateException;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistenceManager extends RepositoryManager {

    private static final Logger log = LoggerFactory.getLogger(PersistenceManager.class);

    private static boolean initialized = false;
    private static PersistenceManager instance = null;
    private EntityManagerFactory entityManagerFactory;

    /**
     * Call the initialize() method before using this class.
     */

    private PersistenceManager() {
    }

    public static PersistenceManager getInstance() {

        if (instance == null) {
            instance = new PersistenceManager();
        }
        return instance;
    }

    public String getRepositoryVersion() {
        if (!initialized) {
            throw new RepositoryException("FileSytemRepository not initialized");
        }
        return FileSystemPapDAO.getInstance().getVersion();
    }

    public void initialize() {
        log.info("Initializing filesystem repository...");

        Ejb3Configuration ejb3Config = new Ejb3Configuration();

        ejb3Config.setProperty(Environment.DRIVER, "org.hsqldb.jdbcDriver");

        String repositoryPath = PAPConfiguration.instance().getPAPRepositoryDir() + File.separator
                + "reposytory";
        String hsqlDbUrl = "jdbc:hsqldb:file:" + repositoryPath
                + ";shutdown=true;hsqldb.default_table_type=text";

        ejb3Config.setProperty(Environment.URL, hsqlDbUrl);
        ejb3Config.setProperty(Environment.USER, "sa");
        ejb3Config.setProperty(Environment.DIALECT, "org.hibernate.dialect.HSQLDialect");

        ejb3Config.setProperty(Environment.C3P0_MIN_SIZE, "5");
        ejb3Config.setProperty(Environment.C3P0_MAX_SIZE, "20");
        ejb3Config.setProperty(Environment.C3P0_TIMEOUT, "300");
        ejb3Config.setProperty(Environment.C3P0_MAX_STATEMENTS, "50");
        ejb3Config.setProperty(Environment.C3P0_IDLE_TEST_PERIOD, "3000");

        // ejb3Config.setProperty(Environment.SHOW_SQL, "true");
        // ejb3Config.setProperty(Environment.FORMAT_SQL, "true");
        // ejb3Config.setProperty(Environment.HBM2DDL_AUTO, "create");

        // ejb3Config.setProperty("hsqldb.default_table_type", "cached");

        // Add annotated classes
        ejb3Config.addAnnotatedClass(org.glite.authz.pap.common.Pap.class);
        ejb3Config.addAnnotatedClass(org.glite.authz.pap.common.xacml.impl.PolicySetTypeString.class);
        ejb3Config.addAnnotatedClass(org.glite.authz.pap.common.xacml.impl.PolicyTypeString.class);

        entityManagerFactory = ejb3Config.buildEntityManagerFactory();

        dbSchemaExport(ejb3Config);

        initialized = true;
    }

    public EntityManager createEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    /**
     * Hibernate specific automatic schema creation.
     * 
     * @param ejb3Config
     */
    private void dbSchemaExport(Ejb3Configuration ejb3Config) {

        AnnotationConfiguration annotatedConfiguration = ejb3Config.getHibernateConfiguration();

        SchemaValidator schemaValidator = new SchemaValidator(annotatedConfiguration);

        try {
            schemaValidator.validate();
        } catch (HibernateException e) {

            createDb();
//            SchemaExport schemaExport = new SchemaExport(annotatedConfiguration);
//
//            schemaExport.create(false, true);
        }
    }

    private void createDb() {
        EntityManager em = createEntityManager();

        em.getTransaction().begin();

        em.createNativeQuery("CREATE TEXT TABLE PAP(ID VARCHAR(255) NOT NULL PRIMARY KEY,ALIAS VARCHAR(255),"
                  + "DN VARCHAR(255),IS_ENABLED BOOLEAN,HOSTNAME VARCHAR(255),IS_LOCAL BOOLEAN,PATH VARCHAR(255),"
                  + "POLICY_LAST_MODIFICATION_AT BIGINT,PORT VARCHAR(255),PROTOCOL VARCHAR(255),IS_PUBLIC BOOLEAN,"
                  + "CONSTRAINT SYS_CT_47 UNIQUE(ALIAS))")
          .executeUpdate();
        em.createNativeQuery("SET TABLE PAP SOURCE \"Pap.csv\"").executeUpdate();

        em.createNativeQuery("CREATE TEXT TABLE POLICY(ID VARCHAR(255) NOT NULL PRIMARY KEY,"
                + "PAP_ID VARCHAR(255),POLICY_STRING VARCHAR(255))").executeUpdate();
        em.createNativeQuery("SET TABLE POLICY SOURCE \"Policy.csv\"").executeUpdate();

        em.createNativeQuery("CREATE TEXT TABLE POLICYSET(ID VARCHAR(255) NOT NULL PRIMARY KEY,"
                + "PAP_ID VARCHAR(255),POLICYSET_STRING VARCHAR(255))").executeUpdate();
        em.createNativeQuery("SET TABLE POLICYSET SOURCE \"Policyset.csv\"").executeUpdate();
        
        em.getTransaction().commit();
        
        em.close();
    }
}
