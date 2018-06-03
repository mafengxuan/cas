package org.apereo.cas.ticket.registry;

import org.apereo.cas.config.CasCoreAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationHandlersConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationMetadataConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPolicyConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPrincipalConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationServiceSelectionStrategyConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationSupportConfiguration;
import org.apereo.cas.config.CasCoreConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreServicesAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketCatalogConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasPersonDirectoryConfiguration;
import org.apereo.cas.config.CouchDbTicketRegistryConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.couchdb.core.CouchDbConnectorFactory;
import org.apereo.cas.couchdb.tickets.TicketRepository;
import org.apereo.cas.logout.config.CasCoreLogoutConfiguration;
import org.apereo.cas.util.junit.ConditionalIgnore;
import org.apereo.cas.util.junit.ConditionalParameterizedRunner;
import org.apereo.cas.util.junit.RunningContinuousIntegrationCondition;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;

import java.util.Arrays;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;

/**
 * This is {@link CouchDbTicketRegistryTests}.
 *
 * @author Timur Duehr
 * @since 5.3.0
 */
@RunWith(ConditionalParameterizedRunner.class)
@ConditionalIgnore(condition = RunningContinuousIntegrationCondition.class)
@SpringBootTest(classes = {CouchDbTicketRegistryConfiguration.class,
    CasCoreTicketsConfiguration.class,
    CasCoreTicketCatalogConfiguration.class,
    CasCoreLogoutConfiguration.class,
    CasCoreHttpConfiguration.class,
    CasCoreServicesConfiguration.class,
    CasCoreAuthenticationConfiguration.class,
    CasCoreServicesAuthenticationConfiguration.class,
    CasCoreConfiguration.class,
    CasCoreWebConfiguration.class,
    CasCoreUtilConfiguration.class,
    CasWebApplicationServiceFactoryConfiguration.class,
    CasCoreAuthenticationServiceSelectionStrategyConfiguration.class,
    CasCoreAuthenticationHandlersConfiguration.class,
    CasCoreAuthenticationMetadataConfiguration.class,
    CasCoreAuthenticationPolicyConfiguration.class,
    CasCoreAuthenticationPrincipalConfiguration.class,
    CasCoreAuthenticationSupportConfiguration.class,
    CasPersonDirectoryConfiguration.class,
    RefreshAutoConfiguration.class},
    properties = {"org.ektorp.support.AutoUpdateViewOnChange=true", "cas.ticket.registry.couchDb.username=", "cas.ticket.registry.couchDb.password="})
@Slf4j
public class CouchDbTicketRegistryTests extends BaseSpringRunnableTicketRegistryTests {
    @Autowired
    @Qualifier("ticketRegistry")
    private TicketRegistry ticketRegistry;

    @Autowired
    @Qualifier("ticketRegistryCouchDbFactory")
    private CouchDbConnectorFactory couchDbFactory;

    @Autowired
    @Qualifier("ticketRegistryCouchDbRepository")
    private TicketRepository ticketRepository;

    public CouchDbTicketRegistryTests(final boolean useEncryption) {
        super(useEncryption);
    }

    @AfterEach
    public void afterEachTest() {
        couchDbFactory.getCouchDbInstance().deleteDatabase(couchDbFactory.getCouchDbConnector().getDatabaseName());
    }

    @Parameterized.Parameters
    public static Collection<Object> getTestParameters() {
        return Arrays.asList(false, true);
    }

    @Override
    public TicketRegistry getNewTicketRegistry() {
        couchDbFactory.getCouchDbInstance().createDatabaseIfNotExists(couchDbFactory.getCouchDbConnector().getDatabaseName());
        ticketRepository.initStandardDesignDocument();
        return ticketRegistry;
    }

    @Override
    protected boolean isIterableRegistry() {
        return false;
    }
}
