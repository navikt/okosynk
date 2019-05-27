package no.nav.okosynk.domain.ur;

import java.util.Comparator;
import no.nav.okosynk.domain.AbstractOppgaveOppretter;

public class UrOppgaveOppretter extends AbstractOppgaveOppretter<UrMelding> {

    private static final String OPPGAVETYPE_KODE   = "OKO_UR";
    private static final int    ANTALL_DAGER_FRIST = 3;

    private final Comparator<UrMelding> meldingComparator = UrMelding.DATO_POSTERT_COMPARATOR;

    @Override
    protected Comparator<UrMelding> getMeldingComparator() {
        return meldingComparator;
    }

    public UrOppgaveOppretter(final UrMappingRegelRepository mappingRegelRepository) {
        super(mappingRegelRepository);
    }

    @Override
    protected String lagBeskrivelse(final UrMelding melding) {
        return new StringBuffer()
                .append(melding.nyesteVentestatus)
                .append(getFeltSeparator())
                .append(melding.arsaksTekst)
                .append(getFeltSeparator())
                .append("postert/bilagsnummer:")
                .append(formatAsNorwegianDate(melding.datoPostert))
                .append("/")
                .append(melding.bilagsId)
                .append(getFeltSeparator())
                .append(melding.hentNettoBelopSomStreng())
                .append("kr")
                .append(getFeltSeparator())
                .append("statusdato:")
                .append(formatAsNorwegianDate(melding.datoForStatus))
                .append(getFeltSeparator())
                .append("UtbTil:")
                .append(melding.mottakerId)
                .append(getFeltSeparator())
                .append(melding.brukerId)
                .toString()
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
