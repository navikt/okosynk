package no.nav.okosynk.consumer;

import org.apache.commons.lang3.Validate;

public class ConsumerStatistics {
    private int antallOppgaverSomErHentetFraDatabasen;
    private int antallOppgaverSomMedSikkerhetErOpprettet;
    private int antallOppgaverSomKanVaereOpprettet;
    private int antallOppgaverSomMedSikkerhetErOppdatert;
    private int antallOppgaverSomMedSikkerhetIkkeErOppdatert;
    private int antallOppgaverSomKanVaereOppdatert;
    private int antallOppgaverSomMedSikkerhetErFerdigstilt;
    private int antallOppgaverSomMedSikkerhetIkkeErFerdigstilt;
    private int antallOppgaverSomKanVaereFerdigstilt;
    private int numberOfExceptionReceivedDuringRun;

    private ConsumerStatistics(Builder builder) {
        this.antallOppgaverSomErHentetFraDatabasen = builder.antallOppgaverSomErHentetFraDatabasen;
        this.antallOppgaverSomMedSikkerhetErOpprettet = builder.antallOppgaverSomMedSikkerhetErOpprettet;
        this.antallOppgaverSomKanVaereOpprettet = builder.antallOppgaverSomKanVaereOpprettet;
        this.antallOppgaverSomMedSikkerhetErOppdatert = builder.antallOppgaverSomMedSikkerhetErOppdatert;
        this.antallOppgaverSomMedSikkerhetIkkeErOppdatert = builder.antallOppgaverSomMedSikkerhetIkkeErOppdatert;
        this.antallOppgaverSomKanVaereOppdatert = builder.antallOppgaverSomKanVaereOppdatert;
        this.antallOppgaverSomMedSikkerhetErFerdigstilt = builder.antallOppgaverSomMedSikkerhetErFerdigstilt;
        this.antallOppgaverSomMedSikkerhetIkkeErFerdigstilt = builder.antallOppgaverSomMedSikkerhetIkkeErFerdigstilt;
        this.antallOppgaverSomKanVaereFerdigstilt = builder.antallOppgaverSomKanVaereFerdigstilt;
        this.numberOfExceptionReceivedDuringRun = builder.numberOfExceptionReceivedDuringRun;
    }

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

    public ConsumerStatistics add(final ConsumerStatistics other) {
        Validate.notNull(other);

        return ConsumerStatistics
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
    }

    public static ConsumerStatistics.Builder builder() {
        return new ConsumerStatistics.Builder();
    }



    public static final class Builder {
        private int antallOppgaverSomErHentetFraDatabasen;
        private int antallOppgaverSomMedSikkerhetErOpprettet;
        private int antallOppgaverSomKanVaereOpprettet;
        private int antallOppgaverSomMedSikkerhetErOppdatert;
        private int antallOppgaverSomMedSikkerhetIkkeErOppdatert;
        private int antallOppgaverSomKanVaereOppdatert;
        private int antallOppgaverSomMedSikkerhetErFerdigstilt;
        private int antallOppgaverSomMedSikkerhetIkkeErFerdigstilt;
        private int antallOppgaverSomKanVaereFerdigstilt;
        private int numberOfExceptionReceivedDuringRun;

        Builder() {

        }

        public ConsumerStatistics build() {
            return new ConsumerStatistics(this);
        }

        public Builder antallOppgaverSomErHentetFraDatabasen(final int verdi) {
            this.antallOppgaverSomErHentetFraDatabasen = verdi;
            return this;
        }
        Builder antallOppgaverSomMedSikkerhetErOpprettet(final int verdi) {
            this.antallOppgaverSomMedSikkerhetErOpprettet = verdi;
            return this;
        }
        Builder antallOppgaverSomKanVaereOpprettet(final int verdi) {
            this.antallOppgaverSomKanVaereOpprettet = verdi;
            return this;
        }
        Builder antallOppgaverSomMedSikkerhetErOppdatert(final int verdi) {
            this.antallOppgaverSomMedSikkerhetErOppdatert = verdi;
            return this;
        }
        Builder antallOppgaverSomMedSikkerhetIkkeErOppdatert(final int verdi) {
            this.antallOppgaverSomMedSikkerhetIkkeErOppdatert = verdi;
            return this;
        }
        Builder antallOppgaverSomKanVaereOppdatert(final int verdi) {
            this.antallOppgaverSomKanVaereOppdatert = verdi;
            return this;
        }
        Builder antallOppgaverSomMedSikkerhetErFerdigstilt(final int verdi) {
            this.antallOppgaverSomMedSikkerhetErFerdigstilt = verdi;
            return this;
        }
        Builder antallOppgaverSomMedSikkerhetIkkeErFerdigstilt(final int verdi) {
            this.antallOppgaverSomMedSikkerhetIkkeErFerdigstilt = verdi;
            return this;
        }
        Builder antallOppgaverSomKanVaereFerdigstilt(final int verdi) {
            this.antallOppgaverSomKanVaereFerdigstilt = verdi;
            return this;
        }
        Builder numberOfExceptionReceivedDuringRun(final int verdi) {
            this.numberOfExceptionReceivedDuringRun = verdi;
            return this;
        }

    }

//    @Override
//    public String toString() {
//        return this.toBuilder().toString();
//    }

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

    public int getAntallOppgaverSomErHentetFraDatabasen() {
        return antallOppgaverSomErHentetFraDatabasen;
    }

    public int getAntallOppgaverSomMedSikkerhetErOpprettet() {
        return antallOppgaverSomMedSikkerhetErOpprettet;
    }

    public int getAntallOppgaverSomKanVaereOpprettet() {
        return antallOppgaverSomKanVaereOpprettet;
    }

    public int getAntallOppgaverSomMedSikkerhetErOppdatert() {
        return antallOppgaverSomMedSikkerhetErOppdatert;
    }

    public int getAntallOppgaverSomMedSikkerhetIkkeErOppdatert() {
        return antallOppgaverSomMedSikkerhetIkkeErOppdatert;
    }

    public int getAntallOppgaverSomKanVaereOppdatert() {
        return antallOppgaverSomKanVaereOppdatert;
    }

    public int getAntallOppgaverSomMedSikkerhetErFerdigstilt() {
        return antallOppgaverSomMedSikkerhetErFerdigstilt;
    }

    public int getAntallOppgaverSomMedSikkerhetIkkeErFerdigstilt() {
        return antallOppgaverSomMedSikkerhetIkkeErFerdigstilt;
    }

    public int getAntallOppgaverSomKanVaereFerdigstilt() {
        return antallOppgaverSomKanVaereFerdigstilt;
    }

    public int getNumberOfExceptionReceivedDuringRun() {
        return numberOfExceptionReceivedDuringRun;
    }
}
