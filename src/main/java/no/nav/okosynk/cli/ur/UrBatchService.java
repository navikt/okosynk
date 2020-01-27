package no.nav.okosynk.cli.ur;

import no.nav.okosynk.batch.AbstractService;
import no.nav.okosynk.batch.BatchRepository;
import no.nav.okosynk.batch.UrService;
import no.nav.okosynk.cli.AbstractBatchService;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;

public class UrBatchService extends AbstractBatchService {

    public UrBatchService(final IOkosynkConfiguration okosynkConfiguration) {
        super(okosynkConfiguration, Constants.BATCH_TYPE.UR);
    }

    @Override
    protected AbstractService createService(final IOkosynkConfiguration okosynkConfiguration,
                                            final BatchRepository batchRepository) {

        return new UrService(okosynkConfiguration, batchRepository);
    }
}