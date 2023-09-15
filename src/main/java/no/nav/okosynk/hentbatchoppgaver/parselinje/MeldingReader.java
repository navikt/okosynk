package no.nav.okosynk.hentbatchoppgaver.parselinje;

import lombok.Getter;
import no.nav.okosynk.hentbatchoppgaver.lesfrafil.exceptions.MeldingUnreadableException;
import no.nav.okosynk.hentbatchoppgaver.model.AbstractMelding;
import no.nav.okosynk.hentbatchoppgaver.parselinje.exceptions.IncorrectMeldingFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;

@Getter
public class MeldingReader<T extends AbstractMelding>
        implements IMeldingReader<T> {

    private static final Logger logger = LoggerFactory.getLogger(MeldingReader.class);

    private final Function<String, T> spesifikkMeldingCreator;

    public void setLinjeNummer(int linjeNummer) {
        this.linjeNummer = linjeNummer;
    }

    private int linjeNummer;

    public MeldingReader(
            final Function<String, T> spesifikkMeldingCreator
    ) {
        this.spesifikkMeldingCreator = spesifikkMeldingCreator;
    }

    @Override
    public List<T> opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(
            final List<String> linjerMedUspesifikkeMeldinger
    ) throws MeldingUnreadableException {

        setLinjeNummer(0);
        List<T> spesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger = null;
        try {
            spesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger =
                    linjerMedUspesifikkeMeldinger.stream().map(
                                    (final String melding) -> {
                                        ++linjeNummer;
                                        return getSpesifikkMeldingCreator().apply(melding);
                                    }
                            )
                            .toList();
        } catch (IncorrectMeldingFormatException e) {
            handterIncorrectMeldingFormatException(e);
        }

        return spesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger;
    }

    private void handterIncorrectMeldingFormatException(
            final IncorrectMeldingFormatException e
    ) throws MeldingUnreadableException {
        String formatted = String.format("AbstractMelding på linje %d i inputfilen har feil format, og kan derfor ikke tolkes.", getLinjeNummer());
        logger.error(formatted, e);

        throw new MeldingUnreadableException(e);
    }
}
