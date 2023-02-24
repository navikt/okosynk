package no.nav.okosynk;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.PdlRestClient;

public abstract class AbstractBatchBlackBoxTest {

    protected static IAktoerClient createAktoerClient(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType
    ) {
        return new PdlRestClient(okosynkConfiguration, batchType);
    }
}