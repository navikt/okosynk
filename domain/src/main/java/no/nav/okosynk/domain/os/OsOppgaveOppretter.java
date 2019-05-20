package no.nav.okosynk.domain.os;

import java.util.Comparator;
import no.nav.okosynk.domain.AbstractOppgaveOppretter;

public class OsOppgaveOppretter extends AbstractOppgaveOppretter<OsMelding> {

    private static final String OPPGAVETYPE_KODE   = "OKO_OS";
    private static final int    ANTALL_DAGER_FRIST = 7;

    private final Comparator<OsMelding> meldingComparator = OsMelding.BEREGNINGS_DATO_COMPARATOR;

    @Override
    protected Comparator<OsMelding> getMeldingComparator() {
        return meldingComparator;
    }

    public OsOppgaveOppretter(final OsMappingRegelRepository mappingRegelRepository) {
        super(mappingRegelRepository);
    }

    @Override
    protected String lagBeskrivelse(final OsMelding melding) {
        return new StringBuffer()
                .append(melding.nyesteVentestatus)
                .append(getFeltSeparator())
                .append(melding.hentNettoBelopSomStreng())
                .append("kr")
                .append(getFeltSeparator())
                .append("beregningsdato/id:")
                .append(AbstractOppgaveOppretter.formatAsNorwegianDate(melding.beregningsDato))
                .append("/")
                .append(melding.beregningsId)
                .append(getFeltSeparator())
                .append("periode:")
                .append(AbstractOppgaveOppretter.formatAsNorwegianDate(melding.forsteFomIPeriode))
                .append("-")
                .append(AbstractOppgaveOppretter.formatAsNorwegianDate(melding.sisteTomIPeriode))
                .append(getFeltSeparator())
                .append("feilkonto:")
                .append(melding.flaggFeilkonto)
                .append(getFeltSeparator())
                .append("statusdato:")
                .append(AbstractOppgaveOppretter.formatAsNorwegianDate(melding.datoForStatus))
                .append(getFeltSeparator())
                .append(melding.etteroppgjor == null ? "" : melding.etteroppgjor)
                .append(getFeltSeparator())
                .append("UtbTil:")
                .append(melding.utbetalesTilId)
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
