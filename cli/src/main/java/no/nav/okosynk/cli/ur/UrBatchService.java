package no.nav.okosynk.cli.ur;

import no.nav.okosynk.batch.AbstractService;
import no.nav.okosynk.batch.BatchRepository;
import no.nav.okosynk.batch.ur.UrService;
import no.nav.okosynk.cli.AbstractBatchService;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.oppgave.OppgaveRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrBatchService extends AbstractBatchService {

    private static final Logger logger = LoggerFactory.getLogger(UrBatchService.class);

    public UrBatchService(final IOkosynkConfiguration okosynkConfiguration) {
        super(okosynkConfiguration, Constants.BATCH_TYPE.UR);
    }

    @Override
    protected AbstractService createService(final IOkosynkConfiguration okosynkConfiguration,
                                            final BatchRepository batchRepository,
                                            final OppgaveRestClient oppgaveRestClient) {

        final AbstractService service = new UrService(okosynkConfiguration, batchRepository, oppgaveRestClient);

        return service;
    }
}
