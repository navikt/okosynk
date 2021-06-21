package no.nav.okosynk.consumer.security;

import no.nav.okosynk.config.IOkosynkConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

/**
 * https://doc.nais.io/appendix/zero-trust/index.html
 * https://doc.nais.io/nais-application/application/
 * https://doc.nais.io/security/auth/
 * https://doc.nais.io/security/auth/azure-ad
 * https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-auth-code-flow
 * https://github.com/navikt/security-blueprints
 * https://github.com/navikt/security-blueprints/tree/master/examples/service-to-service/daemon-clientcredentials-tokensupport/src/main/java/no/nav/security/examples/tokensupport/clientcredentials
 * https://security.labs.nais.io/
 * https://security.labs.nais.io/pages/flows/oauth2/client_credentials.html
 * https://security.labs.nais.io/pages/guide/api-kall/maskin_til_maskin_uten_bruker.html
 * https://security.labs.nais.io/pages/idp/azure-ad.html#registrere-din-applikasjon-i-azure-ad
 */
public class AzureAdClient {

    private static final Logger logger = LoggerFactory.getLogger(AzureAdClient.class);

    final IOkosynkConfiguration okosynkConfiguration;

    public AzureAdClient(final IOkosynkConfiguration okosynkConfiguration) {
        this.okosynkConfiguration = okosynkConfiguration;

        // TODO: Remove when finished developement
        logger.info("***** BEGIN Development info (to be removed when in prod: *****");
        Stream.of("AZURE_APP_CLIENT_ID", "AZURE_APP_WELL_KNOWN_URL")
                .forEach(envVar -> logger.info("{}: {}", envVar, okosynkConfiguration.getString(envVar)));
        logger.info("***** END Development info (to be removed when in prod *****");
    }
}