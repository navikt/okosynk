package no.nav.okosynk.consumer;

import static no.nav.okosynk.config.Constants.SHOULD_USE_SOAP_KEY;

import lombok.AccessLevel;
import lombok.Getter;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConsumerGatewayFactory {

    private static final Logger logger = LoggerFactory.getLogger(AbstractConsumerGatewayFactory.class);

    @Getter(AccessLevel.PROTECTED)
    private final IOkosynkConfiguration okosynkConfiguration;

    protected AbstractConsumerGatewayFactory(final IOkosynkConfiguration okosynkConfiguration) {

        this.okosynkConfiguration = okosynkConfiguration;
    }

    protected boolean shouldUseSoap() {

        final boolean shouldUseSoap =
            getOkosynkConfiguration().getBoolean(SHOULD_USE_SOAP_KEY, true);

        return shouldUseSoap;
    }
}
