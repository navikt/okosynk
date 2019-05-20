package no.nav.okosynk.consumer;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import org.jsoup.helper.Validate;

@Builder(toBuilder = true)
public class ConsumerStatistics {

    public static ConsumerStatistics zero() {
        return ConsumerStatistics.builder().build();
    }

    public static ConsumerStatistics addAll(final ConsumerStatistics ... consumerStatisticsArray) {

        Validate.noNullElements(consumerStatisticsArray, "At least one of the elements to sum is null");

        ConsumerStatistics accumulatedConsumerStatistics = ConsumerStatistics.zero();
        for (int i = 0; i < consumerStatisticsArray.length; i++) {
            accumulatedConsumerStatistics = accumulatedConsumerStatistics.add(consumerStatisticsArray[i]);
        }

        return accumulatedConsumerStatistics;
    }
    @Getter(AccessLevel.PUBLIC) private final int antallOppgaverSomErHentetFraDatabasen;

    @Getter(AccessLevel.PUBLIC) private final int antallOppgaverSomMedSikkerhetErOpprettet;
    @Getter(AccessLevel.PUBLIC) private final int antallOppgaverSomKanVaereOpprettet;

    @Getter(AccessLevel.PUBLIC) private final int antallOppgaverSomMedSikkerhetErOppdatert;
    @Getter(AccessLevel.PUBLIC) private final int antallOppgaverSomMedSikkerhetIkkeErOppdatert;
    @Getter(AccessLevel.PUBLIC) private final int antallOppgaverSomKanVaereOppdatert;

    @Getter(AccessLevel.PUBLIC) private final int antallOppgaverSomMedSikkerhetErFerdigstilt;
    @Getter(AccessLevel.PUBLIC) private final int antallOppgaverSomMedSikkerhetIkkeErFerdigstilt;
    @Getter(AccessLevel.PUBLIC) private final int antallOppgaverSomKanVaereFerdigstilt;

    @Getter(AccessLevel.PUBLIC) private final int numberOfExceptionReceivedDuringRun;

    public ConsumerStatistics add(final ConsumerStatistics other) {

        Validate.notNull(other);

        final ConsumerStatistics newConsumerStatistics =
            ConsumerStatistics
                .builder()
                .antallOppgaverSomErHentetFraDatabasen(other.antallOppgaverSomErHentetFraDatabasen + this.antallOppgaverSomErHentetFraDatabasen)
                .antallOppgaverSomMedSikkerhetErFerdigstilt(other.antallOppgaverSomMedSikkerhetErFerdigstilt + this.antallOppgaverSomMedSikkerhetErFerdigstilt)
                .antallOppgaverSomMedSikkerhetErOppdatert(other.antallOppgaverSomMedSikkerhetErOppdatert + this.antallOppgaverSomMedSikkerhetErOppdatert)
                .antallOppgaverSomMedSikkerhetErOpprettet(other.antallOppgaverSomMedSikkerhetErOpprettet + this.antallOppgaverSomMedSikkerhetErOpprettet)
                .antallOppgaverSomKanVaereFerdigstilt(other.antallOppgaverSomKanVaereFerdigstilt + this.antallOppgaverSomKanVaereFerdigstilt)
                .antallOppgaverSomKanVaereOppdatert(other.antallOppgaverSomKanVaereOppdatert + this.antallOppgaverSomKanVaereOppdatert)
                .antallOppgaverSomKanVaereOpprettet(other.antallOppgaverSomKanVaereOpprettet + this.antallOppgaverSomKanVaereOpprettet)
                .antallOppgaverSomMedSikkerhetIkkeErFerdigstilt(other.antallOppgaverSomMedSikkerhetIkkeErFerdigstilt + this.antallOppgaverSomMedSikkerhetIkkeErFerdigstilt)
                .antallOppgaverSomMedSikkerhetIkkeErOppdatert(other.antallOppgaverSomMedSikkerhetIkkeErOppdatert + this.antallOppgaverSomMedSikkerhetIkkeErOppdatert)
                .numberOfExceptionReceivedDuringRun(other.numberOfExceptionReceivedDuringRun + this.numberOfExceptionReceivedDuringRun)
                .build();

        return newConsumerStatistics;
    }

    @Override
    public String toString() {
        return this.toBuilder().toString();
    }

    @Override
    public boolean equals(final Object other) {

        final boolean equals;
        if(
            other == null
            ||
            !(other instanceof ConsumerStatistics)
        ) {
            equals = false;
        } else {
            final ConsumerStatistics otherConsumerStatistics = (ConsumerStatistics)other;
            equals =
                (
                    otherConsumerStatistics == this
                )
                ||
                (
                    this.antallOppgaverSomErHentetFraDatabasen == otherConsumerStatistics.getAntallOppgaverSomErHentetFraDatabasen()
                    &&
                    this.antallOppgaverSomMedSikkerhetErOpprettet == otherConsumerStatistics.getAntallOppgaverSomMedSikkerhetErOpprettet()
                    &&
                    this.antallOppgaverSomKanVaereOpprettet == otherConsumerStatistics.getAntallOppgaverSomKanVaereOpprettet()
                    &&
                    this.antallOppgaverSomMedSikkerhetErOppdatert == otherConsumerStatistics.getAntallOppgaverSomMedSikkerhetErOppdatert()
                    &&
                    this.antallOppgaverSomMedSikkerhetIkkeErOppdatert == otherConsumerStatistics.getAntallOppgaverSomMedSikkerhetIkkeErOppdatert()
                    &&
                    this.antallOppgaverSomKanVaereOppdatert == otherConsumerStatistics.getAntallOppgaverSomKanVaereOppdatert()
                    &&
                    this.antallOppgaverSomMedSikkerhetErFerdigstilt == otherConsumerStatistics.getAntallOppgaverSomMedSikkerhetErFerdigstilt()
                    &&
                    this.antallOppgaverSomMedSikkerhetIkkeErFerdigstilt == otherConsumerStatistics.getAntallOppgaverSomMedSikkerhetIkkeErFerdigstilt()
                    &&
                    this.antallOppgaverSomKanVaereFerdigstilt == otherConsumerStatistics.getAntallOppgaverSomKanVaereFerdigstilt()
                    &&
                    this.numberOfExceptionReceivedDuringRun == otherConsumerStatistics.getNumberOfExceptionReceivedDuringRun()
                )
            ;
        };

        return equals;
    }
}
