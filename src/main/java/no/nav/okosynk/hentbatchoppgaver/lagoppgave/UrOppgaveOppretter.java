package no.nav.okosynk.hentbatchoppgaver.lagoppgave;

import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.hentbatchoppgaver.lagoppgave.aktoer.IAktoerClient;
import no.nav.okosynk.hentbatchoppgaver.model.UrMelding;

import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UrOppgaveOppretter extends AbstractOppgaveOppretter<UrMelding> {

    private static final String OPPGAVETYPE_KODE = "OKO_UR";
    private static final int ANTALL_DAGER_FRIST = 3;

    private static final Comparator<UrMelding> MELDINGCOMPARATOR = UrMelding.DATO_POSTERT_COMPARATOR;

    UrOppgaveOppretter(
            final UrMappingRegelRepository mappingRegelRepository,
            final IAktoerClient aktoerClient,
            final IOkosynkConfiguration okosynkConfiguration) {

        super(mappingRegelRepository, aktoerClient, okosynkConfiguration);
    }

    @Override
    protected Comparator<UrMelding> getMeldingComparator() {
        return MELDINGCOMPARATOR;
    }

    @Override
    protected String lagBeskrivelse(final UrMelding melding) {
        return Stream.of(melding.nyesteVentestatus,
                        melding.arsaksTekst,
                        "postert/bilagsnummer:" + formatAsNorwegianDate(melding.datoPostert) + "/" + melding.bilagsId,
                        melding.hentNettoBelopSomStreng() + "kr",
                        "statusdato:" + formatAsNorwegianDate(melding.datoForStatus),
                        "UtbTil:" + melding.mottakerId,
                        melding.brukerId)
                .collect(Collectors.joining(getFeltSeparator()))
                .trim();
    }

    @Override
    protected String oppgaveTypeKode() {
        return OPPGAVETYPE_KODE;
    }

    @Override
    protected int antallDagerFrist() {
        return ANTALL_DAGER_FRIST;
    }
}
