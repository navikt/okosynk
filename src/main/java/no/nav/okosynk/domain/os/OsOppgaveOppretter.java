package no.nav.okosynk.domain.os;

import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.aktoer.AktoerRestClient;
import no.nav.okosynk.domain.AbstractOppgaveOppretter;

import java.util.Comparator;

public class OsOppgaveOppretter extends AbstractOppgaveOppretter<OsMelding> {

    private static final String OPPGAVETYPE_KODE = "OKO_OS";
    private static final int ANTALL_DAGER_FRIST = 7;

    private final Comparator<OsMelding> meldingComparator = OsMelding.BEREGNINGS_DATO_COMPARATOR;

    OsOppgaveOppretter(
            final OsMappingRegelRepository mappingRegelRepository,
            final AktoerRestClient aktoerRestClient,
            final IOkosynkConfiguration okosynkConfiguration) {

        super(mappingRegelRepository, aktoerRestClient, okosynkConfiguration);
    }

    @Override
    protected Comparator<OsMelding> getMeldingComparator() {
        return meldingComparator;
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
                .append(formatAsNorwegianDate(melding.beregningsDato))
                .append("/")
                .append(melding.beregningsId)
                .append(getFeltSeparator())
                .append("periode:")
                .append(formatAsNorwegianDate(melding.forsteFomIPeriode))
                .append("-")
                .append(formatAsNorwegianDate(melding.sisteTomIPeriode))
                .append(getFeltSeparator())
                .append("feilkonto:")
                .append(melding.flaggFeilkonto)
                .append(getFeltSeparator())
                .append("statusdato:")
                .append(formatAsNorwegianDate(melding.datoForStatus))
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
