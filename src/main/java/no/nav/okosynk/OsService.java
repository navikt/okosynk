package no.nav.okosynk;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.OsMapper;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.model.OsMelding;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.IMeldingMapper;
import no.nav.okosynk.hentbatchoppgaver.parselinje.MeldingReader;

public class OsService extends AbstractService<OsMelding> {

    public OsService(final OkosynkConfiguration okosynkConfiguration) {
        super(Constants.BATCH_TYPE.OS, okosynkConfiguration);
    }

    @Override
    protected MeldingReader<OsMelding> createMeldingReader() {
        return new MeldingReader<>(OsMelding::new);
    }

    @Override
    protected IMeldingMapper<OsMelding> createMeldingMapper(final IAktoerClient aktoerClient) {
        return new OsMapper(aktoerClient);
    }
}
