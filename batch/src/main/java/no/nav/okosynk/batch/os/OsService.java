package no.nav.okosynk.batch.os;

import no.nav.okosynk.batch.AbstractService;
import no.nav.okosynk.batch.BatchRepository;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.domain.AbstractMeldingReader;
import no.nav.okosynk.domain.IMeldingMapper;
import no.nav.okosynk.domain.os.OsMapper;
import no.nav.okosynk.domain.os.OsMelding;
import no.nav.okosynk.domain.os.OsMeldingReader;

public class OsService extends AbstractService<OsMelding> {

    public OsService(

        final IOkosynkConfiguration             okosynkConfiguration,
        final BatchRepository                   batchRepository) {

        super(
            Constants.BATCH_TYPE.OS,
            okosynkConfiguration,
            batchRepository
        );
    }

    @Override
    protected AbstractMeldingReader<OsMelding> createMeldingReader() {
        return new OsMeldingReader(OsMelding::new);
    }

    @Override
    protected IMeldingMapper<OsMelding> createMeldingMapper() {
        return new OsMapper();
    }
}
