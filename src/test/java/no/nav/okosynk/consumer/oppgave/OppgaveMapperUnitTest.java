package no.nav.okosynk.consumer.oppgave;


import no.nav.okosynk.domain.Oppgave;
import no.nav.okosynk.testutils.RandUt;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OppgaveMapperUnitTest {

    private static final Logger enteringTestHeaderLogger =
            LoggerFactory.getLogger("EnteringTestHeader");

    private static final Random random = new Random(76786);

    private static Stream<Arguments> provideActorTypeCombinations() {

        return Stream.of(
                Arguments.of(new HashSet<Character>(){{add('A');add('B');}}),
                Arguments.of(new HashSet<Character>(){{add('A');add('N');}}),
                Arguments.of(new HashSet<Character>(){{add('A');add('O');}}),
                Arguments.of(new HashSet<Character>(){{add('A');add('S');}}),
                Arguments.of(new HashSet<Character>(){{add('B');add('N');}}),
                Arguments.of(new HashSet<Character>(){{add('B');add('O');}}),
                Arguments.of(new HashSet<Character>(){{add('B');add('S');}}),
                Arguments.of(new HashSet<Character>(){{add('N');add('O');}}),
                Arguments.of(new HashSet<Character>(){{add('N');add('S');}}),
                Arguments.of(new HashSet<Character>(){{add('O');add('S');}})
        );
    }

    @ParameterizedTest
    @ValueSource(chars = {'A', 'B', 'N', 'O', 'S'})
    void when_mapping_from_oppgave_to_oppgaveDto_then_all_fields_should_have_expected_values(final char aktorType) throws OppgaveMapperException_MoreThanOneActorType, OppgaveMapperException_AktivTilFraNull {

        enteringTestHeaderLogger.debug(null);

        final Oppgave expectedOppgave =
                new Oppgave.OppgaveBuilder()
                        .withAktivFra(LocalDate.of(1970 + random.nextInt(51), 1 + random.nextInt(12), 1 + random.nextInt(28)))
                        .withAktivTil(LocalDate.of(1970 + random.nextInt(52), 1 + random.nextInt(12), 1 + random.nextInt(28)))
                        .withAntallMeldinger(31)
                        .withSistEndret(LocalDateTime.of(1999, 3, 5, 23, 19, 13, 17))
                        .withAktoerId(aktorType == 'A' ? RandUt.constructRandomAlphaNumString(random.nextInt(99), OppgaveMapperUnitTest.random): null)
                        .withAnsvarligEnhetId(RandUt.constructRandomAlphaNumString(random.nextInt(99), OppgaveMapperUnitTest.random))
                        .withAnsvarligSaksbehandlerIdent(RandUt.constructRandomAlphaNumString(random.nextInt(99), OppgaveMapperUnitTest.random))
                        .withBehandlingstema(RandUt.constructRandomAlphaNumString(random.nextInt(99), OppgaveMapperUnitTest.random))
                        .withBehandlingstype(RandUt.constructRandomAlphaNumString(random.nextInt(99), OppgaveMapperUnitTest.random))
                        .withBeskrivelse(RandUt.constructRandomAlphaNumString(103 + random.nextInt(99), OppgaveMapperUnitTest.random))
                        .withBnr(aktorType == 'B' ? RandUt.constructRandomAlphaNumString(random.nextInt(97), OppgaveMapperUnitTest.random): null)
                        .withFagomradeKode(RandUt.constructRandomAlphaNumString(random.nextInt(91), OppgaveMapperUnitTest.random))
                        .withLest(OppgaveMapperUnitTest.random.nextInt(2) == 1)
                        .withMappeId(RandUt.constructRandomAlphaNumString(random.nextInt(89), OppgaveMapperUnitTest.random))
                        .withNavPersonIdent(aktorType == 'N' ? RandUt.constructRandomAlphaNumString(random.nextInt(87), OppgaveMapperUnitTest.random): null)
                        .withOppgaveId(RandUt.constructRandomAlphaNumString(random.nextInt(83), OppgaveMapperUnitTest.random))
                        .withOppgavetypeKode(RandUt.constructRandomAlphaNumString(random.nextInt(79), OppgaveMapperUnitTest.random))
                        .withOrgnr(aktorType == 'O' ? RandUt.constructRandomAlphaNumString(random.nextInt(73), OppgaveMapperUnitTest.random): null)
                        .withPrioritetKode(RandUt.constructRandomAlphaNumString(random.nextInt(71), OppgaveMapperUnitTest.random))
                        .withSamhandlernr(aktorType == 'S' ? RandUt.constructRandomAlphaNumString(random.nextInt(61), OppgaveMapperUnitTest.random): null)
                        .withVersjon(Math.abs(OppgaveMapperUnitTest.random.nextInt()))
                        .build();

        final OppgaveDto actualOppgaveDto = OppgaveMapper.map(expectedOppgave);

        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        assertEquals(OppgaveMapper.ENHET_ID_FOR_ANDRE_EKSTERNE, actualOppgaveDto.getOpprettetAvEnhetsnr(), "opprettetAvEnhetsnr");
        assertEquals(expectedOppgave.aktivFra.format(dateFormatter), actualOppgaveDto.getAktivDato(), "aktivFra");
        assertEquals(expectedOppgave.aktivTil.format(dateFormatter), actualOppgaveDto.getFristFerdigstillelse(), "fristFerdigstillelse");
        assertEquals(expectedOppgave.aktoerId, actualOppgaveDto.getAktoerId(), "aktoerId");
        assertEquals(expectedOppgave.ansvarligEnhetId, actualOppgaveDto.getTildeltEnhetsnr(), "tildeltEnhetsnr");
        assertEquals(expectedOppgave.behandlingstema, actualOppgaveDto.getBehandlingstema(), "behandlingstema");
        assertEquals(expectedOppgave.behandlingstema, actualOppgaveDto.getBehandlingstema(), "behandlingstema");
        assertEquals(expectedOppgave.behandlingstype, actualOppgaveDto.getBehandlingstype(), "behandlingstype");
        assertEquals(expectedOppgave.beskrivelse, actualOppgaveDto.getBeskrivelse(), "beskrivelse");
        assertEquals(expectedOppgave.bnr, actualOppgaveDto.getBnr(), "bnr");
        assertEquals(expectedOppgave.navPersonIdent, actualOppgaveDto.getNavPersonIdent(), "navPersonIdent");
        assertEquals(expectedOppgave.oppgavetypeKode, actualOppgaveDto.getOppgavetype(), "oppgavetypeKode");
        assertEquals(expectedOppgave.orgnr, actualOppgaveDto.getOrgnr(), "orgnr");
        assertEquals(expectedOppgave.prioritetKode, actualOppgaveDto.getPrioritet(), "prioritetKode");
        assertEquals(expectedOppgave.samhandlernr, actualOppgaveDto.getSamhandlernr(), "samhandlernr");

        assertEquals(null, actualOppgaveDto.getBehandlesAvApplikasjon(), "behandlesAvApplikasjon");
        assertEquals(null, actualOppgaveDto.getEndretAv(), "endretAv");
        assertEquals(null, actualOppgaveDto.getEndretAvEnhetsnr(), "endretAvEnhetsnr");
        assertEquals(null, actualOppgaveDto.getEndretTidspunkt(), "endretTidspunkt");
        assertEquals(null, actualOppgaveDto.getFerdigstiltTidspunkt(), "ferdigstiltTidspunkt");
        assertEquals(null, actualOppgaveDto.getId(), "id");
        assertEquals(null, actualOppgaveDto.getJournalpostId(), "journalpostId");
        assertEquals(null, actualOppgaveDto.getJournalpostkilde(), "journalpostkilde");
        assertEquals(null, actualOppgaveDto.getMappeId(), "mappeId");
        assertEquals(null, actualOppgaveDto.getMetadata(), "metadata");
        assertEquals(null, actualOppgaveDto.getOpprettetAv(), "opprettetAv");
        assertEquals(null, actualOppgaveDto.getOpprettetTidspunkt(), "opprettetTidspunkt");
        assertEquals(null, actualOppgaveDto.getSaksreferanse(), "saksreferanse");
        assertEquals(null, actualOppgaveDto.getStatus(), "status");
        assertEquals(null, actualOppgaveDto.getStatus(), "status");
        assertEquals(null, actualOppgaveDto.getTilordnetRessurs(), "tilordnetRessurs");
        assertEquals(null, actualOppgaveDto.getVersjon(), "versjon");
    }

    @ParameterizedTest
    @MethodSource("provideActorTypeCombinations")
    void when_more_than_one_actorType_is_set_then_an_exception_should_be_thrown(final Set<Character> actorTypes) {

        enteringTestHeaderLogger.debug(null);

        final Oppgave expectedOppgave =
                new Oppgave.OppgaveBuilder()
                        .withAktivFra(LocalDate.of(1970 + random.nextInt(51), 1 + random.nextInt(12), 1 + random.nextInt(28)))
                        .withAktivTil(LocalDate.of(1970 + random.nextInt(52), 1 + random.nextInt(12), 1 + random.nextInt(28)))
                        .withAktoerId(actorTypes.contains('A') ? RandUt.constructRandomAlphaNumString(random.nextInt(99), OppgaveMapperUnitTest.random): null)
                        .withBnr(actorTypes.contains('B') ? RandUt.constructRandomAlphaNumString(random.nextInt(97), OppgaveMapperUnitTest.random): null)
                        .withNavPersonIdent(actorTypes.contains('N') ? RandUt.constructRandomAlphaNumString(random.nextInt(87), OppgaveMapperUnitTest.random): null)
                        .withOrgnr(actorTypes.contains('O') ? RandUt.constructRandomAlphaNumString(random.nextInt(73), OppgaveMapperUnitTest.random): null)
                        .withSamhandlernr(actorTypes.contains('S') ? RandUt.constructRandomAlphaNumString(random.nextInt(61), OppgaveMapperUnitTest.random): null)
                        .build();

        assertThrows(OppgaveMapperException_MoreThanOneActorType.class, () -> OppgaveMapper.map(expectedOppgave));
    }

    @ParameterizedTest
    @ValueSource(chars = {'F', 'T', 'B'})
    void when_aktivFra_or_aktivTil_is_null_then_an_exception_should_be_thrown(final char fieldIndicator) {

        enteringTestHeaderLogger.debug(null);

        final Oppgave expectedOppgave =
                new Oppgave.OppgaveBuilder()
                        .withAktivFra(fieldIndicator == 'F' || fieldIndicator == 'B' ? null : LocalDate.of(1970 + random.nextInt(51), 1 + random.nextInt(12), 1 + random.nextInt(28)))
                        .withAktivTil(fieldIndicator == 'T' || fieldIndicator == 'B' ? null : LocalDate.of(1970 + random.nextInt(52), 1 + random.nextInt(12), 1 + random.nextInt(28)))
                        .build();

        assertThrows(OppgaveMapperException_AktivTilFraNull.class, () -> OppgaveMapper.map(expectedOppgave));
    }
}
