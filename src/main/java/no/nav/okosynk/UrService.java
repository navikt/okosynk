package no.nav.okosynk;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.UrMapper;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.model.UrMelding;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.IMeldingMapper;
import no.nav.okosynk.hentbatchoppgaver.parselinje.MeldingReader;

public class UrService extends AbstractService<UrMelding> {

    public UrService(final IOkosynkConfiguration okosynkConfiguration) {
        super(Constants.BATCH_TYPE.UR, okosynkConfiguration);
    }

    @Override
    protected MeldingReader<UrMelding> createMeldingReader() {
        return new MeldingReader<>(UrMelding::new);
    }

    @Override
    protected IMeldingMapper<UrMelding> createMeldingMapper(final IAktoerClient aktoerClient) {
        return new UrMapper(aktoerClient, getOkosynkConfiguration());
    }
}