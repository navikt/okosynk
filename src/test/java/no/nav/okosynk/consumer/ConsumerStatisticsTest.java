package no.nav.okosynk.consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.Collections2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import no.nav.okosynk.config.Constants.BATCH_TYPE;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConsumerStatisticsTest {

  private static final Logger enteringTestHeaderLogger =
      LoggerFactory.getLogger("EnteringTestHeader");

  private static final int primes[] = {3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59,
      61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157,
      163, 167, 173, 179, 181, 191, 193, 197, 199};

  @Test
  public void should_throw_when_added_is_null() {

    enteringTestHeaderLogger.debug(null);

    final ConsumerStatistics consumerStatistics1 = ConsumerStatistics.zero("y");
    final ConsumerStatistics consumerStatistics2 = null;

    assertThrows(
        NullPointerException.class,
        () -> consumerStatistics1.add(consumerStatistics2)
    );
  }

  @Test
  public void should_throw_when_adding_multiple_instances_and_at_least_one_is_null() {

    enteringTestHeaderLogger.debug(null);

    final String consumerStatisticsName = "x";
    final ConsumerStatistics consumerStatistics1 = ConsumerStatistics.zero(consumerStatisticsName);
    final ConsumerStatistics consumerStatistics2 = null;
    final ConsumerStatistics consumerStatistics3 = ConsumerStatistics.zero(consumerStatisticsName);

    assertThrows(
        IllegalArgumentException.class,
        () -> ConsumerStatistics
            .addAll(consumerStatistics1, consumerStatistics2, consumerStatistics3)
    );
  }

  @Test
  public void should_add_three_instances_correctly_when_all_are_valid() {

    enteringTestHeaderLogger.debug(null);

    int i = 0;
    final String consumerStatisticsName = "XX";
    final ConsumerStatistics consumerStatistics1 =
        ConsumerStatistics
            .builder()

            .name(consumerStatisticsName)

            .antallOppgaverSomErHentetFraDatabasen(primes[i++])

            .antallOppgaverSomMedSikkerhetErFerdigstilt(primes[i++])
            .antallOppgaverSomMedSikkerhetErOppdatert(primes[i++])
            .antallOppgaverSomMedSikkerhetErOpprettet(primes[i++])
            .antallOppgaverSomMedSikkerhetIkkeErOpprettet(primes[i++])

            .antallOppgaverSomMedSikkerhetIkkeErFerdigstilt(primes[i++])
            .antallOppgaverSomMedSikkerhetIkkeErOppdatert(primes[i++])

            .numberOfExceptionsReceivedDuringRun(primes[i++])

            .build();

    final ConsumerStatistics consumerStatistics2 =
        ConsumerStatistics
            .builder()

            .name(consumerStatisticsName)

            .antallOppgaverSomErHentetFraDatabasen(primes[i++])

            .antallOppgaverSomMedSikkerhetErFerdigstilt(primes[i++])
            .antallOppgaverSomMedSikkerhetErOppdatert(primes[i++])
            .antallOppgaverSomMedSikkerhetErOpprettet(primes[i++])
            .antallOppgaverSomMedSikkerhetIkkeErOpprettet(primes[i++])

            .antallOppgaverSomMedSikkerhetIkkeErFerdigstilt(primes[i++])
            .antallOppgaverSomMedSikkerhetIkkeErOppdatert(primes[i++])

            .numberOfExceptionsReceivedDuringRun(primes[i++])

            .build();

    final ConsumerStatistics consumerStatistics3 =
        ConsumerStatistics
            .builder()

            .name(consumerStatisticsName)

            .antallOppgaverSomErHentetFraDatabasen(primes[i++])

            .antallOppgaverSomMedSikkerhetErFerdigstilt(primes[i++])
            .antallOppgaverSomMedSikkerhetErOppdatert(primes[i++])
            .antallOppgaverSomMedSikkerhetErOpprettet(primes[i++])
            .antallOppgaverSomMedSikkerhetIkkeErOpprettet(primes[i++])

            .antallOppgaverSomMedSikkerhetIkkeErFerdigstilt(primes[i++])
            .antallOppgaverSomMedSikkerhetIkkeErOppdatert(primes[i++])

            .numberOfExceptionsReceivedDuringRun(primes[i++])

            .build();

    final ConsumerStatistics actualConsumerStatisticsSum =
        ConsumerStatistics.addAll(consumerStatistics1, consumerStatistics2, consumerStatistics3);

    i = 0;
    final int numberOfFields = 8;
    final ConsumerStatistics expectedConsumerStatisticsSum =
        ConsumerStatistics

            .builder()

            .name(consumerStatisticsName)

            .antallOppgaverSomErHentetFraDatabasen(
                primes[i] + primes[i + numberOfFields] + primes[i++ + numberOfFields * 2])

            .antallOppgaverSomMedSikkerhetErFerdigstilt(
                primes[i] + primes[i + numberOfFields] + primes[i++ + numberOfFields * 2])
            .antallOppgaverSomMedSikkerhetErOppdatert(
                primes[i] + primes[i + numberOfFields] + primes[i++ + numberOfFields * 2])
            .antallOppgaverSomMedSikkerhetErOpprettet(
                primes[i] + primes[i + numberOfFields] + primes[i++ + numberOfFields * 2])
            .antallOppgaverSomMedSikkerhetIkkeErOpprettet(
                primes[i] + primes[i + numberOfFields] + primes[i++ + numberOfFields * 2])

            .antallOppgaverSomMedSikkerhetIkkeErFerdigstilt(
                primes[i] + primes[i + numberOfFields] + primes[i++ + numberOfFields * 2])
            .antallOppgaverSomMedSikkerhetIkkeErOppdatert(
                primes[i] + primes[i + numberOfFields] + primes[i++ + numberOfFields * 2])

            .numberOfExceptionsReceivedDuringRun(
                primes[i] + primes[i + numberOfFields] + primes[i++ + numberOfFields * 2])

            .build();

    assertEquals(expectedConsumerStatisticsSum, actualConsumerStatisticsSum);
  }

  @Test
  public void when_two_with_differing_names_are_added_an_exception_should_be_thrown() {

    enteringTestHeaderLogger.debug(null);

    final ConsumerStatistics consumerStatistics1 =
        ConsumerStatistics
            .builder()
            .name("x")
            .build();

    final ConsumerStatistics consumerStatistics2 =
        ConsumerStatistics
            .builder()
            .name("y")
            .build();

    assertThrows(IllegalArgumentException.class,
        () -> consumerStatistics1.add(consumerStatistics2));
  }

  @Test
  public void when_three_with_differing_names_are_added_an_exception_should_be_thrown() {

    enteringTestHeaderLogger.debug(null);

    final ConsumerStatistics consumerStatistics1 =
        ConsumerStatistics.builder().name("x").build();
    final ConsumerStatistics consumerStatistics2 =
        ConsumerStatistics.builder().name("y").build();
    final ConsumerStatistics consumerStatistics3 =
        ConsumerStatistics.builder().name("z").build();

    final List<ConsumerStatistics> allElementsSource = new ArrayList<>();
    allElementsSource.add(consumerStatistics1);
    allElementsSource.add(consumerStatistics2);
    allElementsSource.add(consumerStatistics3);
    final Collection<List<ConsumerStatistics>> allPermutations =
        Collections2.permutations(allElementsSource);

    allPermutations
        .stream()
        .forEach(
            (final List<ConsumerStatistics> allElements) ->
                assertThrows(
                    IllegalArgumentException.class,
                    () -> {
                      ConsumerStatistics
                          .addAll(
                              allElements.get(0),
                              allElements.get(1),
                              allElements.get(2)
                          );
                    }
                )
        );
  }

  @Test
  public void when_instantiating_without_a_name_an_exception_should_be_thrown() {

    enteringTestHeaderLogger.debug(null);

    assertThrows(NullPointerException.class, () -> ConsumerStatistics.builder().build());
  }

  @Test
  public void when_instantiating_with_an_empty_name_an_exception_should_be_thrown() {

    enteringTestHeaderLogger.debug(null);

    assertThrows(IllegalArgumentException.class,
        () -> ConsumerStatistics.builder().name("").build());
  }

  @Test
  public void when_instantiating_with_a_blank_name_an_exception_should_be_thrown() {

    enteringTestHeaderLogger.debug(null);

    assertThrows(IllegalArgumentException.class,
        () -> ConsumerStatistics.builder().name("   ").build());
  }

  @Test
  public void when_instantiating_with_only_a_name_an_exception_should_not_be_thrown() {

    enteringTestHeaderLogger.debug(null);

    assertDoesNotThrow(() -> ConsumerStatistics.builder().name("x").build());
    assertDoesNotThrow(() -> ConsumerStatistics.builder("y").build());
  }

  @Test
  public void when_sufficiently_initialized_toString_should_not_throw() {

    enteringTestHeaderLogger.debug(null);

    assertDoesNotThrow(
        () -> {
          final String s = ConsumerStatistics.builder().name("x").build().toString();
          System.out.println("ConsumerStatistics.toString() => " + s);
        }
    );
  }

  @Test
  public void when_stringified_then_all_field_names_and_values_should_be_present() {

    enteringTestHeaderLogger.debug(null);

    final ConsumerStatistics consumerStatistics =
      ConsumerStatistics.builder()
          .name("x")
          .antallOppgaverSomMedSikkerhetErFerdigstilt(7)
          .antallOppgaverSomMedSikkerhetErOppdatert(11)
          .antallOppgaverSomMedSikkerhetErOpprettet(13)
          .antallOppgaverSomErHentetFraDatabasen(17)
          .antallOppgaverSomMedSikkerhetIkkeErFerdigstilt(19)
          .antallOppgaverSomMedSikkerhetIkkeErOppdatert(23)
          .numberOfExceptionsReceivedDuringRun(41)
          .antallOppgaverSomMedSikkerhetIkkeErOpprettet(43)
          .numberOfExceptionsReceivedDuringRun(47)
          .build();

    final String consumerStatisticsString = consumerStatistics.toString();
    assertTrue(consumerStatisticsString.contains(String.valueOf(consumerStatistics.getAntallOppgaverSomErHentetFraDatabasen())));
    assertTrue(consumerStatisticsString.contains(String.valueOf(consumerStatistics.getAntallOppgaverSomMedSikkerhetErFerdigstilt())));
    assertTrue(consumerStatisticsString.contains(String.valueOf(consumerStatistics.getAntallOppgaverSomMedSikkerhetErOppdatert())));
    assertTrue(consumerStatisticsString.contains(String.valueOf(consumerStatistics.getAntallOppgaverSomMedSikkerhetErOpprettet())));
    assertTrue(consumerStatisticsString.contains(String.valueOf(consumerStatistics.getAntallOppgaverSomMedSikkerhetIkkeErFerdigstilt())));
    assertTrue(consumerStatisticsString.contains(String.valueOf(consumerStatistics.getAntallOppgaverSomMedSikkerhetIkkeErOppdatert())));
    assertTrue(consumerStatisticsString.contains(String.valueOf(consumerStatistics.getAntallOppgaverSomMedSikkerhetIkkeErOpprettet())));
    assertTrue(consumerStatisticsString.contains(String.valueOf(consumerStatistics.getName())));
    assertTrue(consumerStatisticsString.contains(String.valueOf(consumerStatistics.getNumberOfExceptionsReceivedDuringRun())));

    assertTrue(consumerStatisticsString.contains("antallOppgaverSomErHentetFraDatabasen"));
    assertTrue(consumerStatisticsString.contains("antallOppgaverSomMedSikkerhetErFerdigstilt"));
    assertTrue(consumerStatisticsString.contains("antallOppgaverSomMedSikkerhetErOppdatert"));
    assertTrue(consumerStatisticsString.contains("antallOppgaverSomMedSikkerhetErOpprettet"));
    assertTrue(consumerStatisticsString.contains("antallOppgaverSomMedSikkerhetIkkeErFerdigstilt"));
    assertTrue(consumerStatisticsString.contains("antallOppgaverSomMedSikkerhetIkkeErOppdatert"));
    assertTrue(consumerStatisticsString.contains("name"));
    assertTrue(consumerStatisticsString.contains("numberOfExceptionsReceivedDuringRun"));
    assertTrue(consumerStatisticsString.contains("antallOppgaverSomMedSikkerhetIkkeErOpprettet"));
  }

  @Test
  public void when_compared_to_null_should_not_be_equal() {

    enteringTestHeaderLogger.debug(null);

    final ConsumerStatistics consumerStatistics =
        ConsumerStatistics.builder().name("x").build();
    assertFalse(consumerStatistics.equals(null));
  }

  @Test
  public void when_created_with_batch_type_the_name_should_be_as_expected() {

    enteringTestHeaderLogger.debug(null);

    final BATCH_TYPE batchType = BATCH_TYPE.OS;
    final ConsumerStatistics consumerStatistics1 =
        ConsumerStatistics.builder().name(batchType.getConsumerStatisticsName()).build();
    final ConsumerStatistics consumerStatistics2 =
        ConsumerStatistics.builder(batchType).build();
    final ConsumerStatistics consumerStatistics3 =
        ConsumerStatistics.builder(batchType.getConsumerStatisticsName()).build();
    final ConsumerStatistics consumerStatistics4 =
        ConsumerStatistics.builder().name(batchType.getConsumerStatisticsName()).build();

    final List<ConsumerStatistics> allElementsSource = new ArrayList<>();
    allElementsSource.add(consumerStatistics1);
    allElementsSource.add(consumerStatistics2);
    allElementsSource.add(consumerStatistics3);
    allElementsSource.add(consumerStatistics4);
    final Collection<List<ConsumerStatistics>> allPermutations =
        Collections2.permutations(allElementsSource);

    allPermutations
        .stream()
        .forEach(
            (final List<ConsumerStatistics> allConsumerStatistics) ->
            {
              assertEquals(consumerStatistics1, consumerStatistics1);
              assertEquals(consumerStatistics1, consumerStatistics2);
              assertEquals(consumerStatistics1, consumerStatistics3);
              assertEquals(consumerStatistics1, consumerStatistics4);
            }
        );
  }

  @Test
  public void when_created_through_zero_with_batch_type_the_name_should_be_as_expected() {

    enteringTestHeaderLogger.debug(null);

    final BATCH_TYPE batchType = BATCH_TYPE.OS;
    final ConsumerStatistics consumerStatistics1 =
        ConsumerStatistics.builder().name(batchType.getConsumerStatisticsName()).build();
    final ConsumerStatistics consumerStatistics2 =
        ConsumerStatistics.zero(batchType);
    final ConsumerStatistics consumerStatistics3 =
        ConsumerStatistics.zero(batchType.getConsumerStatisticsName());

    final List<ConsumerStatistics> allElementsSource = new ArrayList<>();
    allElementsSource.add(consumerStatistics1);
    allElementsSource.add(consumerStatistics2);
    allElementsSource.add(consumerStatistics3);
    final Collection<List<ConsumerStatistics>> allPermutations =
        Collections2.permutations(allElementsSource);

    allPermutations
        .stream()
        .forEach(
            (final List<ConsumerStatistics> allConsumerStatistics) ->
            {
              assertEquals(consumerStatistics1, consumerStatistics1);
              assertEquals(consumerStatistics1, consumerStatistics2);
              assertEquals(consumerStatistics1, consumerStatistics3);
            }
        );
  }

  @Test
  public void when_all_fields_in_each_of_two_instances_are_different_only_one_instance_of_the_permutated_other_should_equal() {

    enteringTestHeaderLogger.debug(null);

    int ix = 0;
    final Integer[] firstEverySecondPrimes = new Integer[] {
        primes[1 + 2*ix++],
        primes[1 + 2*ix++],
        primes[1 + 2*ix++],
        primes[1 + 2*ix++],
        primes[1 + 2*ix++],
        primes[1 + 2*ix++],
        primes[1 + 2*ix++],
        primes[1 + 2*ix++],
        primes[1 + 2*ix++],
        primes[1 + 2*ix++],
        primes[1 + 2*ix++]
    };
    final String consumerStatisticsName = "x";
    ix = 0;
    final ConsumerStatistics fixedConsumerStatistics =
        ConsumerStatistics
            .builder()
            .name(consumerStatisticsName)
            .antallOppgaverSomErHentetFraDatabasen(firstEverySecondPrimes[ix++])
            .antallOppgaverSomMedSikkerhetErOpprettet(firstEverySecondPrimes[ix])
            .antallOppgaverSomMedSikkerhetIkkeErOpprettet(firstEverySecondPrimes[ix++])
            .antallOppgaverSomMedSikkerhetErOppdatert(firstEverySecondPrimes[ix++])
            .antallOppgaverSomMedSikkerhetIkkeErOppdatert(firstEverySecondPrimes[ix++])
            .antallOppgaverSomMedSikkerhetErFerdigstilt(firstEverySecondPrimes[ix++])
            .antallOppgaverSomMedSikkerhetIkkeErFerdigstilt(firstEverySecondPrimes[ix++])
            .numberOfExceptionsReceivedDuringRun(firstEverySecondPrimes[ix++])
            .build();

    final Random random = new Random(13897654);
    int iiix = 0;
    do {
      final List<Integer> changedValues = Arrays.asList(firstEverySecondPrimes.clone());
      if (iiix < firstEverySecondPrimes.length) {
        changedValues.set(iiix, changedValues.get(iiix) + 1);
      } else {
        Collections.shuffle(changedValues, random);
      }

      int iix = 0;
      final ConsumerStatistics varyingConsumerStatistics =
          ConsumerStatistics
              .builder()
              .name(consumerStatisticsName)
              .antallOppgaverSomErHentetFraDatabasen(changedValues.get(iix++))
              .antallOppgaverSomMedSikkerhetErOpprettet(changedValues.get(iix++))
              .antallOppgaverSomMedSikkerhetIkkeErOpprettet(changedValues.get(iix++))
              .antallOppgaverSomMedSikkerhetErOppdatert(changedValues.get(iix++))
              .antallOppgaverSomMedSikkerhetIkkeErOppdatert(changedValues.get(iix++))
              .antallOppgaverSomMedSikkerhetErFerdigstilt(changedValues.get(iix++))
              .antallOppgaverSomMedSikkerhetIkkeErFerdigstilt(changedValues.get(iix++))
              .numberOfExceptionsReceivedDuringRun(changedValues.get(iix++))
              .build();
      if (fixedConsumerStatistics.equals(varyingConsumerStatistics)) {
        assertEquals(fixedConsumerStatistics.getName(), varyingConsumerStatistics.getName());
        assertEquals(fixedConsumerStatistics.getAntallOppgaverSomMedSikkerhetErFerdigstilt(), varyingConsumerStatistics.getAntallOppgaverSomMedSikkerhetErFerdigstilt());
        assertEquals(fixedConsumerStatistics.getAntallOppgaverSomMedSikkerhetErOppdatert(), varyingConsumerStatistics.getAntallOppgaverSomMedSikkerhetErOppdatert());
        assertEquals(fixedConsumerStatistics.getAntallOppgaverSomMedSikkerhetErOpprettet(), varyingConsumerStatistics.getAntallOppgaverSomMedSikkerhetErOpprettet());
        assertEquals(fixedConsumerStatistics.getAntallOppgaverSomErHentetFraDatabasen(), varyingConsumerStatistics.getAntallOppgaverSomErHentetFraDatabasen());
        assertEquals(fixedConsumerStatistics.getAntallOppgaverSomMedSikkerhetIkkeErFerdigstilt(), varyingConsumerStatistics.getAntallOppgaverSomMedSikkerhetIkkeErFerdigstilt());
        assertEquals(fixedConsumerStatistics.getAntallOppgaverSomMedSikkerhetIkkeErOppdatert(), varyingConsumerStatistics.getAntallOppgaverSomMedSikkerhetIkkeErOppdatert());
        assertEquals(fixedConsumerStatistics.getNumberOfExceptionsReceivedDuringRun(), varyingConsumerStatistics.getNumberOfExceptionsReceivedDuringRun());
      } else {
        assertTrue(
            fixedConsumerStatistics.getAntallOppgaverSomMedSikkerhetErFerdigstilt() != varyingConsumerStatistics.getAntallOppgaverSomMedSikkerhetErFerdigstilt()
                ||
                fixedConsumerStatistics.getAntallOppgaverSomMedSikkerhetErOppdatert() != varyingConsumerStatistics.getAntallOppgaverSomMedSikkerhetErOppdatert()
                ||
                fixedConsumerStatistics.getAntallOppgaverSomMedSikkerhetErOpprettet() != varyingConsumerStatistics.getAntallOppgaverSomMedSikkerhetErOpprettet()
                ||
                fixedConsumerStatistics.getAntallOppgaverSomErHentetFraDatabasen() != varyingConsumerStatistics.getAntallOppgaverSomErHentetFraDatabasen()
                ||
                fixedConsumerStatistics.getAntallOppgaverSomMedSikkerhetIkkeErFerdigstilt() != varyingConsumerStatistics.getAntallOppgaverSomMedSikkerhetIkkeErFerdigstilt()
                ||
                fixedConsumerStatistics.getAntallOppgaverSomMedSikkerhetIkkeErOppdatert() != varyingConsumerStatistics.getAntallOppgaverSomMedSikkerhetIkkeErOppdatert()
                ||
                fixedConsumerStatistics.getNumberOfExceptionsReceivedDuringRun() != varyingConsumerStatistics.getNumberOfExceptionsReceivedDuringRun()
        );
      }

    } while (++iiix < 1000);
  }
}