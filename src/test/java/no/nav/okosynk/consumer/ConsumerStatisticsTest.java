package no.nav.okosynk.consumer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConsumerStatisticsTest {

    private static final int primes[] = {3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199};

    @Test
    public void should_throw_when_added_is_null() {
        final ConsumerStatistics consumerStatistics1 = ConsumerStatistics.zero();
        final ConsumerStatistics consumerStatistics2 = null;

        assertThrows(
            NullPointerException.class,
            () -> consumerStatistics1.add(consumerStatistics2)
        );
    }

    @Test
    public void should_throw_when_adding_multiple_instances_and_at_least_one_is_null() {
        final ConsumerStatistics consumerStatistics1 = ConsumerStatistics.zero();
        final ConsumerStatistics consumerStatistics2 = null;
        final ConsumerStatistics consumerStatistics3 = ConsumerStatistics.zero();

        assertThrows(
            IllegalArgumentException.class,
            () -> ConsumerStatistics.addAll(consumerStatistics1, consumerStatistics2, consumerStatistics3)
        );
    }

    @Test
    public void should_add_three_instances_correctly_when_all_are_valid() {

        int i = 0;
        final ConsumerStatistics consumerStatistics1 =
            ConsumerStatistics
                .builder()

                .antallOppgaverSomErHentetFraDatabasen(primes[i++])

                .antallOppgaverSomMedSikkerhetErFerdigstilt(primes[i++])
                .antallOppgaverSomMedSikkerhetErOppdatert(primes[i++])
                .antallOppgaverSomMedSikkerhetErOpprettet(primes[i++])

                .antallOppgaverSomMedSikkerhetIkkeErFerdigstilt(primes[i++])
                .antallOppgaverSomMedSikkerhetIkkeErOppdatert(primes[i++])


                .antallOppgaverSomKanVaereFerdigstilt(primes[i++])
                .antallOppgaverSomKanVaereOppdatert(primes[i++])
                .antallOppgaverSomKanVaereOpprettet(primes[i++])

                .numberOfExceptionReceivedDuringRun(primes[i++])

                .build();

        final ConsumerStatistics consumerStatistics2 =
            ConsumerStatistics
                .builder()

                .antallOppgaverSomErHentetFraDatabasen(primes[i++])

                .antallOppgaverSomMedSikkerhetErFerdigstilt(primes[i++])
                .antallOppgaverSomMedSikkerhetErOppdatert(primes[i++])
                .antallOppgaverSomMedSikkerhetErOpprettet(primes[i++])

                .antallOppgaverSomMedSikkerhetIkkeErFerdigstilt(primes[i++])
                .antallOppgaverSomMedSikkerhetIkkeErOppdatert(primes[i++])


                .antallOppgaverSomKanVaereFerdigstilt(primes[i++])
                .antallOppgaverSomKanVaereOppdatert(primes[i++])
                .antallOppgaverSomKanVaereOpprettet(primes[i++])

                .numberOfExceptionReceivedDuringRun(primes[i++])

                .build();

        final ConsumerStatistics consumerStatistics3 =
            ConsumerStatistics
                .builder()

                .antallOppgaverSomErHentetFraDatabasen(primes[i++])

                .antallOppgaverSomMedSikkerhetErFerdigstilt(primes[i++])
                .antallOppgaverSomMedSikkerhetErOppdatert(primes[i++])
                .antallOppgaverSomMedSikkerhetErOpprettet(primes[i++])

                .antallOppgaverSomMedSikkerhetIkkeErFerdigstilt(primes[i++])
                .antallOppgaverSomMedSikkerhetIkkeErOppdatert(primes[i++])


                .antallOppgaverSomKanVaereFerdigstilt(primes[i++])
                .antallOppgaverSomKanVaereOppdatert(primes[i++])
                .antallOppgaverSomKanVaereOpprettet(primes[i++])

                .numberOfExceptionReceivedDuringRun(primes[i++])

                .build();

        final ConsumerStatistics actualConsumerStatisticsSum =
            ConsumerStatistics.addAll(consumerStatistics1, consumerStatistics2, consumerStatistics3);

        i = 0;
        final int numberOfFields = 10;
        final ConsumerStatistics expectedConsumerStatisticsSum =
            ConsumerStatistics

                .builder()

                .antallOppgaverSomErHentetFraDatabasen(primes[i] + primes[i + numberOfFields] + primes[i++ + numberOfFields*2])

                .antallOppgaverSomMedSikkerhetErFerdigstilt(primes[i] + primes[i + numberOfFields] + primes[i++ + numberOfFields*2])
                .antallOppgaverSomMedSikkerhetErOppdatert(primes[i] + primes[i + numberOfFields] + primes[i++ + numberOfFields*2])
                .antallOppgaverSomMedSikkerhetErOpprettet(primes[i] + primes[i + numberOfFields] + primes[i++ + numberOfFields*2])

                .antallOppgaverSomMedSikkerhetIkkeErFerdigstilt(primes[i] + primes[i + numberOfFields] + primes[i++ + numberOfFields*2])
                .antallOppgaverSomMedSikkerhetIkkeErOppdatert(primes[i] + primes[i + numberOfFields] + primes[i++ + numberOfFields*2])

                .antallOppgaverSomKanVaereFerdigstilt(primes[i] + primes[i + numberOfFields] + primes[i++ + numberOfFields*2])
                .antallOppgaverSomKanVaereOppdatert(primes[i] + primes[i + numberOfFields] + primes[i++ + numberOfFields*2])
                .antallOppgaverSomKanVaereOpprettet(primes[i] + primes[i + numberOfFields] + primes[i++ + numberOfFields*2])

                .numberOfExceptionReceivedDuringRun(primes[i] + primes[i + numberOfFields] + primes[i++ + numberOfFields*2])

                .build();

        assertEquals(expectedConsumerStatisticsSum, actualConsumerStatisticsSum);
    }
}
