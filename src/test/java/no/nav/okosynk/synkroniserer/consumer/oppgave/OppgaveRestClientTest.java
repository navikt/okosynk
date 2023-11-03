package no.nav.okosynk.synkroniserer.consumer.oppgave;

import no.nav.okosynk.comm.AzureAdAuthenticationClient;
import no.nav.okosynk.config.Constants.BATCH_TYPE;
import no.nav.okosynk.config.OkosynkConfiguration;
import no.nav.okosynk.model.Oppgave;
import no.nav.okosynk.model.Oppgave.OppgaveBuilder;
import no.nav.okosynk.synkroniserer.consumer.ConsumerStatistics;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static no.nav.okosynk.config.Constants.NAIS_APP_NAME_KEY;
import static no.nav.okosynk.config.Constants.OPPGAVE_URL_KEY;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OppgaveRestClientTest {

    @BeforeAll
    static void beforeAll() throws IOException {
        AzureAdAuthenticationClient mockedAzureAdAuthenticationClient = mock(AzureAdAuthenticationClient.class);
        when(mockedAzureAdAuthenticationClient.getToken()).thenReturn("RUBBISH TEST TOKEN WHICH IS NO TOKEN AT ALL BUT ONLY A PLACEHOLDER");
    }

    @BeforeEach
    void beforeEach() {
        System.setProperty(NAIS_APP_NAME_KEY, "okosynkur");
        System.setProperty(OkosynkConfiguration.AZURE_APP_CLIENT_ID_KEY, "nisdn-hvh7-eds-hv7-eh7--v-10ab-ghv87g-h-e87v-87eg8");
    }

    @AfterEach
    void afterEach() {
        System.clearProperty(OkosynkConfiguration.AZURE_APP_CLIENT_ID_KEY);
        System.clearProperty(NAIS_APP_NAME_KEY);
        System.clearProperty(OPPGAVE_URL_KEY);
    }

    @Test
    void when_finnOppgaver_finds_no_oppgaver_then_no_exception_should_be_thrown_and_the_return_value_should_contain_a_correct_report()
            throws IOException {
        final OppgaveRestClient mockedOppgaveRestClient =
                OppgaveRestClientTestUtils.prepareAMockedOppgaveRestClientThatSucceedsInFindingZeroOppgaver();
        AzureAdAuthenticationClient mockedAzureAdAuthenticationClient = mock(AzureAdAuthenticationClient.class);
        when(mockedOppgaveRestClient.getAzureAdAuthenticationClient()).thenReturn(mockedAzureAdAuthenticationClient);

        final Set<Oppgave> oppgaver = new HashSet<>();
        assertDoesNotThrow(
                () -> {

                    final ConsumerStatistics consumerStatistics =
                            mockedOppgaveRestClient.finnOppgaver(oppgaver);
                    assertEquals(0, oppgaver.size());
                    assertEquals(consumerStatistics, ConsumerStatistics.zero(BATCH_TYPE.UR));
                }
        );
    }

    @Test
    void when_finnOppgaver_finds_one_oppgave_then_no_exception_should_be_thrown_and_the_return_value_should_contain_a_correct_report()
            throws IOException {

        final OppgaveRestClient mockedOppgaveRestClient =
                OppgaveRestClientTestUtils.prepareAMockedOppgaveRestClientThatSucceedsInFindingOneOppgave();
        AzureAdAuthenticationClient mockedAzureAdAuthenticationClient = mock(AzureAdAuthenticationClient.class);
        when(mockedOppgaveRestClient.getAzureAdAuthenticationClient()).thenReturn(mockedAzureAdAuthenticationClient);

        final Set<Oppgave> oppgaver = new HashSet<>();
        assertDoesNotThrow(
                () -> {
                    final ConsumerStatistics consumerStatistics = mockedOppgaveRestClient.finnOppgaver(oppgaver);
                    assertEquals(1, oppgaver.size());
                    assertEquals(1, consumerStatistics.getAntallOppgaverSomErHentetFraDatabasen());
                }
        );
    }

    @Test
    void when_finnOppgaver_finds_one_oppgave_less_than_bulkSize_then_no_exception_should_be_thrown_and_the_return_value_should_contain_a_correct_report()
            throws IOException {
        final OppgaveRestClient mockedOppgaveRestClient =
                OppgaveRestClientTestUtils.prepareAMockedOppgaveRestClientThatSucceedsInFinding19Oppgaver();
        AzureAdAuthenticationClient mockedAzureAdAuthenticationClient = mock(AzureAdAuthenticationClient.class);
        when(mockedOppgaveRestClient.getAzureAdAuthenticationClient()).thenReturn(mockedAzureAdAuthenticationClient);

        final Set<Oppgave> oppgaver = new HashSet<>();
        assertDoesNotThrow(
                () -> {
                    final ConsumerStatistics consumerStatistics =
                            mockedOppgaveRestClient.finnOppgaver(oppgaver);
                    assertEquals(19, oppgaver.size());
                    assertEquals(19, consumerStatistics.getAntallOppgaverSomErHentetFraDatabasen());
                }
        );
    }

    @Test
    void when_finnOppgaver_finds_bulkSize_number_of_oppgaver_then_no_exception_should_be_thrown_and_the_return_value_should_contain_a_correct_report()
            throws IOException {

        final OppgaveRestClient mockedOppgaveRestClient =
                OppgaveRestClientTestUtils.prepareAMockedOppgaveRestClientThatSucceedsInFinding50Oppgaver();
        AzureAdAuthenticationClient mockedAzureAdAuthenticationClient = mock(AzureAdAuthenticationClient.class);
        when(mockedOppgaveRestClient.getAzureAdAuthenticationClient()).thenReturn(mockedAzureAdAuthenticationClient);

        final Set<Oppgave> oppgaver = new HashSet<>();
        assertDoesNotThrow(
                () -> {
                    final ConsumerStatistics consumerStatistics =
                            mockedOppgaveRestClient.finnOppgaver(oppgaver);
                    assertEquals(50, oppgaver.size());
                    assertEquals(50, consumerStatistics.getAntallOppgaverSomErHentetFraDatabasen());
                }
        );
    }

    @Test
    void when_finnOppgaver_finds_one_oppgave_more_than_bulkSize_then_no_exception_should_be_thrown_and_the_return_value_should_contain_a_correct_report()
            throws IOException {

        final OppgaveRestClient mockedOppgaveRestClient =
                OppgaveRestClientTestUtils.prepareAMockedOppgaveRestClientThatSucceedsInFinding51Oppgaver();
        AzureAdAuthenticationClient mockedAzureAdAuthenticationClient = mock(AzureAdAuthenticationClient.class);
        when(mockedOppgaveRestClient.getAzureAdAuthenticationClient()).thenReturn(mockedAzureAdAuthenticationClient);

        final Set<Oppgave> oppgaver = new HashSet<>();
        assertDoesNotThrow(
                () -> {
                    final ConsumerStatistics consumerStatistics =
                            mockedOppgaveRestClient.finnOppgaver(oppgaver);
                    assertEquals(51, oppgaver.size());
                    assertEquals(51, consumerStatistics.getAntallOppgaverSomErHentetFraDatabasen());
                }
        );
    }

    @Test
    void when_finnOppgaver_rest_call_fails_the_result_should_reflect_it() throws IOException {
        final OppgaveRestClient mockedOppgaveRestClient =
                OppgaveRestClientTestUtils.prepareAMockedFinnOppgaveRestClientBaseThatDoesNotFail();
        when(mockedOppgaveRestClient.executeRequest(any(), any(HttpUriRequest.class)))
                .thenReturn(OppgaveRestClientTestUtils.reponseWithErrorCodeGreaterThan400);
        AzureAdAuthenticationClient mockedAzureAdAuthenticationClient = mock(AzureAdAuthenticationClient.class);
        when(mockedOppgaveRestClient.getAzureAdAuthenticationClient()).thenReturn(mockedAzureAdAuthenticationClient);

        final Set<Oppgave> oppgaver = new HashSet<>();
        assertThrows(
                IllegalStateException.class,
                () -> mockedOppgaveRestClient.finnOppgaver(oppgaver)
        );
    }

    @Test
    void when_opprettOppgaver_gets_zero_oppgaver_then_no_exception_should_be_thrown_and_the_result_should_reflect_the_number()
            throws IOException {

        final OppgaveRestClient oppgaveRestClient =
                OppgaveRestClientTestUtils.prepareAMockedOppgaveRestClientThatSucceedsInCreatingZeroOppgaver();

        final List<Oppgave> oppgaver = new ArrayList<>();

        assertDoesNotThrow(
                () -> {
                    final ConsumerStatistics consumerStatistics = oppgaveRestClient.opprettOppgaver(oppgaver);
                    assertEquals(consumerStatistics, ConsumerStatistics.zero(BATCH_TYPE.UR));
                }
        );
    }

    @Test
    void when_opprettOppgaver_gets_one_oppgave_then_no_exception_should_be_thrown_and_the_result_should_reflect_the_number()
            throws IOException {

        final Random random = new Random(38762486);

        final OppgaveRestClient mockedOppgaveRestClient =
                OppgaveRestClientTestUtils.prepareAMockedOppgaveRestClientThatSucceedsInCreatingOneOppgave();

        final Oppgave oppgave =
                new OppgaveBuilder()
                        .withBeskrivelse(RandomStringUtils.randomAlphanumeric(23))
                        .withAktivFra(LocalDate.of(
                                2010 + random.nextInt(10),
                                1 + random.nextInt(12),
                                1 + random.nextInt(27)))
                        .withAktivTil(LocalDate.of(
                                2010 + random.nextInt(10),
                                1 + random.nextInt(12),
                                1 + random.nextInt(27)))
                        .build();
        final List<Oppgave> oppgaver = new ArrayList<>();
        oppgaver.add(oppgave);

        assertDoesNotThrow(
                () -> {
                    final ConsumerStatistics expectedConsumerStatistics =
                            ConsumerStatistics.zero(BATCH_TYPE.UR)
                                    .add(
                                            ConsumerStatistics
                                                    .builder()
                                                    .antallOppgaverSomMedSikkerhetErOpprettet(1)
                                                    .name(BATCH_TYPE.UR.getConsumerStatisticsName())
                                                    .build()
                                    );
                    final ConsumerStatistics actualConsumerStatistics =
                            mockedOppgaveRestClient.opprettOppgaver(oppgaver);
                    assertEquals(expectedConsumerStatistics, actualConsumerStatistics);
                }
        );
    }

    @Test
    void when_opprettOppgaver_rest_call_fails_with_an_http_code_greater_than_400_then_the_result_should_reflect_it() throws IOException {

        final OppgaveRestClient mockedOppgaveRestClient =
                OppgaveRestClientTestUtils.prepareAMockedOpprettOppgaveRestClientThatFailsWithAnHttpCodeGreaterThan400();
        AzureAdAuthenticationClient mockedAzureAdAuthenticationClient = mock(AzureAdAuthenticationClient.class);
        when(mockedOppgaveRestClient.getAzureAdAuthenticationClient()).thenReturn(mockedAzureAdAuthenticationClient);

        final Set<Oppgave> oppgaver = new HashSet<>();
        final Random random = new Random(919286);
        final Oppgave oppgave =
                new OppgaveBuilder()
                        .withBeskrivelse(RandomStringUtils.randomAlphanumeric(23))
                        .withAktivFra(LocalDate.of(
                                2010 + random.nextInt(10),
                                1 + random.nextInt(12),
                                1 + random.nextInt(27)))
                        .withAktivTil(LocalDate.of(
                                2010 + random.nextInt(10),
                                1 + random.nextInt(12),
                                1 + random.nextInt(27)))

                        .build();
        oppgaver.add(oppgave);
        assertDoesNotThrow(
                () -> {
                    final ConsumerStatistics actualConsumerStatistics =
                            mockedOppgaveRestClient.opprettOppgaver(oppgaver);
                    final ConsumerStatistics expectedConsumerStatistics =
                            ConsumerStatistics.zero(BATCH_TYPE.UR)
                                    .add(
                                            ConsumerStatistics
                                                    .builder()
                                                    .antallOppgaverSomMedSikkerhetIkkeErOpprettet(1)
                                                    .name(BATCH_TYPE.UR.getConsumerStatisticsName())
                                                    .build()
                                    );
                    assertEquals(expectedConsumerStatistics, actualConsumerStatistics);
                }
        );
    }

    @Test
    void when_patchOppgaver_gets_zero_oppgaver_for_ferdigstilling_then_no_exception_should_be_thrown_and_the_result_should_reflect_the_number()
            throws IOException {

        when_patchOppgaver_gets_zero_oppgaver_then_no_exception_should_be_thrown_and_the_result_should_reflect_the_number(true);
    }

    @Test
    void when_patchOppgaver_gets_zero_oppgaver_for_non_ferdigstilling_then_no_exception_should_be_thrown_and_the_result_should_reflect_the_number()
            throws IOException {

        when_patchOppgaver_gets_zero_oppgaver_then_no_exception_should_be_thrown_and_the_result_should_reflect_the_number(false);
    }

    @Test
    void when_patchOppgaver_gets_one_oppgave_for_ferdigstilling_then_no_exception_should_be_thrown_and_the_result_should_reflect_the_number()
            throws IOException {

        when_patchOppgaver_gets_one_oppgave_then_no_exception_should_be_thrown_and_the_result_should_reflect_the_number(true);
    }

    @Test
    void when_patchOppgaver_gets_one_oppgave_for_non_ferdigstilling_then_no_exception_should_be_thrown_and_the_result_should_reflect_the_number()
            throws IOException {

        when_patchOppgaver_gets_one_oppgave_then_no_exception_should_be_thrown_and_the_result_should_reflect_the_number(false);
    }

    @Test
    void when_patchOppgaver_rest_call_fails_with_an_http_code_greater_than_400_then_the_result_should_reflect_it()
            throws IOException {

        final OppgaveRestClient mockedOppgaveRestClient =
                OppgaveRestClientTestUtils.prepareAMockedPatchOppgaverRestClientThatFailsWithAnHttpCodeGreaterThan400();
        AzureAdAuthenticationClient mockedAzureAdAuthenticationClient = mock(AzureAdAuthenticationClient.class);
        when(mockedOppgaveRestClient.getAzureAdAuthenticationClient()).thenReturn(mockedAzureAdAuthenticationClient);

        final Random random = new Random(38762486);
        final Oppgave oppgave =
                new OppgaveBuilder()
                        .withBeskrivelse(RandomStringUtils.randomAlphanumeric(23))
                        .withAktivFra(LocalDate.of(
                                2010 + random.nextInt(10),
                                1 + random.nextInt(12),
                                1 + random.nextInt(27)))
                        .withAktivTil(LocalDate.of(
                                2010 + random.nextInt(10),
                                1 + random.nextInt(12),
                                1 + random.nextInt(27)))
                        .build();
        final List<Oppgave> oppgaver = new ArrayList<>();
        oppgaver.add(oppgave);

        final boolean shouldFerdigstille = true;
        assertDoesNotThrow(
                () -> {
                    final ConsumerStatistics actualConsumerStatistics =
                            mockedOppgaveRestClient.patchOppgaver(oppgaver, shouldFerdigstille);
                    final ConsumerStatistics expectedConsumerStatistics =
                            ConsumerStatistics.zero(BATCH_TYPE.UR)
                                    .add(
                                            ConsumerStatistics
                                                    .builder()
                                                    .antallOppgaverSomMedSikkerhetIkkeErFerdigstilt(1)
                                                    .name(BATCH_TYPE.UR.getConsumerStatisticsName())
                                                    .build()
                                    );
                    assertEquals(expectedConsumerStatistics, actualConsumerStatistics);
                }
        );
    }

    private void when_patchOppgaver_gets_zero_oppgaver_then_no_exception_should_be_thrown_and_the_result_should_reflect_the_number(final boolean shouldFerdigstille)
            throws IOException {

        final OppgaveRestClient oppgaveRestClient =
                OppgaveRestClientTestUtils.prepareAMockedPatchRestClientThatSucceedsInCreatingZeroOppgaver();
        final Set<Oppgave> oppgaver = new HashSet<>();

        assertDoesNotThrow(
                () -> {
                    final ConsumerStatistics consumerStatistics = oppgaveRestClient.patchOppgaver(oppgaver, shouldFerdigstille);
                    assertEquals(consumerStatistics, ConsumerStatistics.zero(BATCH_TYPE.UR));
                }
        );
    }

    private void when_patchOppgaver_gets_one_oppgave_then_no_exception_should_be_thrown_and_the_result_should_reflect_the_number(final boolean shouldFerdigstille)
            throws IOException {

        final Random random = new Random(38762486);

        final OppgaveRestClient mockedOppgaveRestClient =
                OppgaveRestClientTestUtils.prepareAMockedPatchRestClientThatSucceedsInUpdatingOneOppgave();
        AzureAdAuthenticationClient mockedAzureAdAuthenticationClient = mock(AzureAdAuthenticationClient.class);
        when(mockedOppgaveRestClient.getAzureAdAuthenticationClient()).thenReturn(mockedAzureAdAuthenticationClient);

        final Oppgave oppgave =
                new OppgaveBuilder()
                        .withBeskrivelse(RandomStringUtils.randomAlphanumeric(23))
                        .withAktivFra(LocalDate.of(
                                2010 + random.nextInt(10),
                                1 + random.nextInt(12),
                                1 + random.nextInt(27)))
                        .withAktivTil(LocalDate.of(
                                2010 + random.nextInt(10),
                                1 + random.nextInt(12),
                                1 + random.nextInt(27)))
                        .build();
        final List<Oppgave> oppgaver = new ArrayList<>();
        oppgaver.add(oppgave);

        assertDoesNotThrow(
                () -> {
                    final ConsumerStatistics expectedConsumerStatistics =
                            ConsumerStatistics.zero(BATCH_TYPE.UR)
                                    .add(
                                            ConsumerStatistics
                                                    .builder()
                                                    .antallOppgaverSomMedSikkerhetErOppdatert(shouldFerdigstille ? 0 : 1)
                                                    .antallOppgaverSomMedSikkerhetErFerdigstilt(shouldFerdigstille ? 1 : 0)
                                                    .name(BATCH_TYPE.UR.getConsumerStatisticsName())
                                                    .build()
                                    );
                    final ConsumerStatistics actualConsumerStatistics =
                            mockedOppgaveRestClient.patchOppgaver(oppgaver, shouldFerdigstille);
                    assertEquals(expectedConsumerStatistics, actualConsumerStatistics);
                }
        );
    }
}
