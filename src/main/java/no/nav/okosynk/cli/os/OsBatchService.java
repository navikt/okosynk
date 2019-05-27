package no.nav.okosynk.cli.os;

import no.nav.okosynk.batch.AbstractService;
import no.nav.okosynk.batch.BatchRepository;
import no.nav.okosynk.batch.OsService;
import no.nav.okosynk.cli.AbstractBatchService;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsBatchService
    extends AbstractBatchService {

    private static final Logger logger = LoggerFactory.getLogger(OsBatchService.class);

    public OsBatchService(final IOkosynkConfiguration okosynkConfiguration) {
        super(okosynkConfiguration, Constants.BATCH_TYPE.OS);
    }

    @Override
    protected AbstractService createService(final IOkosynkConfiguration okosynkConfiguration,
                                            final BatchRepository batchRepository) {

        return new OsService(okosynkConfiguration, batchRepository);
    }
}
