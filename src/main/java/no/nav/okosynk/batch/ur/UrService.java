package no.nav.okosynk.batch.ur;

import no.nav.okosynk.batch.AbstractService;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.aktoer.IAktoerClient;
import no.nav.okosynk.domain.AbstractMeldingReader;
import no.nav.okosynk.domain.IMeldingMapper;
import no.nav.okosynk.domain.ur.UrMapper;
import no.nav.okosynk.domain.ur.UrMelding;
import no.nav.okosynk.domain.ur.UrMeldingReader;

public class UrService extends AbstractService<UrMelding> {

    public UrService(final IOkosynkConfiguration okosynkConfiguration) {
        super(Constants.BATCH_TYPE.UR, okosynkConfiguration);
    }

    @Override
    protected AbstractMeldingReader<UrMelding> createMeldingReader() {
        return new UrMeldingReader(UrMelding::new);
    }

    @Override
    protected IMeldingMapper<UrMelding> createMeldingMapper(final IAktoerClient aktoerClient) {
        return new UrMapper(aktoerClient, getOkosynkConfiguration());
    }
}