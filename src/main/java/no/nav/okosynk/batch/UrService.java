package no.nav.okosynk.batch;

import no.nav.okosynk.batch.AbstractService;
import no.nav.okosynk.batch.BatchRepository;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.aktoer.AktoerRestClient;
import no.nav.okosynk.domain.AbstractMeldingReader;
import no.nav.okosynk.domain.IMeldingMapper;
import no.nav.okosynk.domain.ur.UrMapper;
import no.nav.okosynk.domain.ur.UrMelding;
import no.nav.okosynk.domain.ur.UrMeldingReader;

public class UrService extends AbstractService<UrMelding> {

    public UrService(final IOkosynkConfiguration okosynkConfiguration,
                     final BatchRepository       batchRepository) {

        super(Constants.BATCH_TYPE.UR, okosynkConfiguration, batchRepository);
    }

    @Override
    protected AbstractMeldingReader<UrMelding> createMeldingReader() {
        return new UrMeldingReader(UrMelding::new);
    }

    @Override
    protected IMeldingMapper<UrMelding> createMeldingMapper() {
        return new UrMapper(new AktoerRestClient(getOkosynkConfiguration(), getBatchType()));
    }
}
