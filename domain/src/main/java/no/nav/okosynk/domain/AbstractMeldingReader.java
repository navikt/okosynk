package no.nav.okosynk.domain;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMeldingReader<SPESIFIKKMELDINGTYPE extends AbstractMelding>
    implements IMeldingReader<SPESIFIKKMELDINGTYPE> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMeldingReader.class);

    @Getter(AccessLevel.PRIVATE)
    private final IMeldingCreator<SPESIFIKKMELDINGTYPE> spesifikkMeldingCreator;

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private int linjeNummer;

    protected AbstractMeldingReader(
        final IMeldingCreator<SPESIFIKKMELDINGTYPE> spesifikkMeldingCreator
    ) {
       this.spesifikkMeldingCreator = spesifikkMeldingCreator;
    }

    private int preIncreaseLinjeNummer() {
        return ++linjeNummer;
    }

    @Override
    public List<SPESIFIKKMELDINGTYPE> opprettSpesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger(
        final Stream<String> linjerMedUspesifikkeMeldinger
    ) throws MeldingUnreadableException {

        setLinjeNummer(0);
        List<SPESIFIKKMELDINGTYPE> spesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger = null;
        try {
            spesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger =
                linjerMedUspesifikkeMeldinger.map(
                    (final String melding) -> {
                        preIncreaseLinjeNummer();
                        return getSpesifikkMeldingCreator().apply(melding);
                    }
                )
                .collect(Collectors.toList());
        } catch (IncorrectMeldingFormatException e) {
            handterIncorrectMeldingFormatException(e);
        }

        return spesifikkeMeldingerFraLinjerMedUspesifikkeMeldinger;
    }

    private void handterIncorrectMeldingFormatException(
        final IncorrectMeldingFormatException e
    ) throws MeldingUnreadableException {

        logger.error(String.format("AbstractMelding på linje %d i inputfilen har feil format, og kan derfor ikke tolkes.", getLinjeNummer()), e);

        throw new MeldingUnreadableException(e);
    }
}
