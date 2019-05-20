package no.nav.okosynk.consumer;

import lombok.AccessLevel;
import lombok.Getter;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.hjelper.ConsumerHjelper;
import no.nav.okosynk.consumer.interceptor.SystemSAMLOutInterceptorMedBruker;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConsumerConfig<SOAPSERVICE>
    implements IMockableConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractConsumerConfig.class);

    @Getter(AccessLevel.PROTECTED)
    private final IOkosynkConfiguration okosynkConfiguration;

    @Getter(AccessLevel.PROTECTED)
    private final Constants.CONSUMER_TYPE consumerType;

    @Getter(AccessLevel.PRIVATE)
    final Class<SOAPSERVICE>              soapServiceClass;

    protected AbstractConsumerConfig(
        final IOkosynkConfiguration   okosynkConfiguration,
        final Constants.CONSUMER_TYPE consumerType,
        final Class<SOAPSERVICE>      soapServiceClass) {

        this.okosynkConfiguration = okosynkConfiguration;
        this.consumerType         = consumerType;
        this.soapServiceClass     = soapServiceClass;
    }

    @Override
    public String getTjenestebeskrivelse() {
        return getConsumerType().getTjenestebeskrivelse();
    }

    @Override
    public String getMockPropertyNavn() {

        return getConsumerType().getMockKey();
    }

    protected String getEndPointUrl() {

        final String endPointUrlKey = getEndPointUrlKey();
        final String endPointUrl    = getOkosynkConfiguration().getString(endPointUrlKey);

        return endPointUrl;
    }

    protected CXFClient<SOAPSERVICE> factory(final String bruker) {

        final String endPointUrl = getEndPointUrl();

        final CXFClient<SOAPSERVICE> cxfClient;
        if (endPointUrl == null) {
            final String msg =
                  "The endPointUrl %s system property is not set. "
                + "This implies that no oppgave can be retrieved using SOAP";
            final String fMsg =String.format(msg, getEndPointUrlKey());
            logger.error(fMsg);
            throw new IllegalStateException(fMsg);
        } else {
            final String msg = "The endPointUrl {} system property is set to {}";
            logger.debug(msg, getEndPointUrlKey(), endPointUrl);
            final AbstractSAMLOutInterceptor systemSAMLOutInterceptor =
                getSystemSAMLOutInterceptor(bruker);

            cxfClient =
                new CXFClient<SOAPSERVICE>(getSoapServiceClass())
                    .address(endPointUrl)
                    .withOutInterceptor(systemSAMLOutInterceptor)
                    .timeout(getConnectionTimeout(), getReceiveTimeout());
        }

        return cxfClient;
    }

    protected String getTjenesteBeskrivelseForPing() {

        final IOkosynkConfiguration okosynkConfiguration = getOkosynkConfiguration();

        final String tjenesteBeskrivelseForPing =
            ConsumerHjelper.getAvbruddStatus(okosynkConfiguration, getMockPropertyNavn())
                + ConsumerHjelper.getMockStatus(okosynkConfiguration, getMockPropertyNavn())
                + getTjenestebeskrivelse();

        return tjenesteBeskrivelseForPing;
    }

    private int getConnectionTimeout() {
        return getConsumerType().getTimeout();
    }

    private int getReceiveTimeout() {
        return getConsumerType().getTimeout();
    }

    private String getEndPointUrlKey() {
        return getConsumerType().getEndpointUrlKey();
    }

    private AbstractSAMLOutInterceptor getSystemSAMLOutInterceptor(final String bruker) {

        final AbstractSAMLOutInterceptor samlOutInterceptor =
            (bruker == null)
            ?
            new SystemSAMLOutInterceptor()
            :
            new SystemSAMLOutInterceptorMedBruker(bruker);

        return samlOutInterceptor;
    }
}
