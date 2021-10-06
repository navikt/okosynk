package no.nav.okosynk.consumer.aktoer;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.apache.commons.lang3.NotImplementedException;

public class PdlRestClient implements IAktoerClient {

    private final IOkosynkConfiguration okosynkConfiguration;
    private final Constants.BATCH_TYPE batchType;

    public PdlRestClient(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType) {
        this.okosynkConfiguration = okosynkConfiguration;
        this.batchType = batchType;
    }

    @Override
    public AktoerRespons hentGjeldendeAktoerId(final String folkeregisterIdent) {
        throw new NotImplementedException();
    }
}