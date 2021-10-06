package no.nav.okosynk.batch;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.aktoer.IAktoerClient;
import no.nav.okosynk.consumer.aktoer.PdlRestClientWithFallbackToAktoerRegisteret;

public abstract class AbstractBatchBlackBoxTest {

    protected static IAktoerClient createAktoerClient(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType
    ) {
        return new PdlRestClientWithFallbackToAktoerRegisteret(okosynkConfiguration, batchType);
    }
}