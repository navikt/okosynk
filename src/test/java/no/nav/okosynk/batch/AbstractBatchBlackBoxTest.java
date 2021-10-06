package no.nav.okosynk.batch;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.aktoer.AktoerRestClient;
import no.nav.okosynk.consumer.aktoer.IAktoerClient;

public abstract class AbstractBatchBlackBoxTest {

    protected static IAktoerClient createAktoerClient(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType
    ) {
        return new AktoerRestClient(okosynkConfiguration, batchType);
    }
}