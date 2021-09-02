package no.nav.okosynk.consumer.oppgave;

import no.nav.okosynk.domain.Oppgave;
import no.nav.okosynk.testutils.RandUt;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static Stream<Arguments> provideActorTypeCombinations() {

        return Stream.of(
                Arguments.of(new HashSet<Character>() {{
                    add('A');
                    add('B');
                }}),
                Arguments.of(new HashSet<Character>() {{
                    add('A');
                    add('N');
                }}),
                Arguments.of(new HashSet<Character>() {{
                    add('A');
                    add('O');
                }}),
                Arguments.of(new HashSet<Character>() {{
                    add('A');
                    add('S');
                }}),
                Arguments.of(new HashSet<Character>() {{
                    add('B');
                    add('N');
                }}),
                Arguments.of(new HashSet<Character>() {{
                    add('B');
                    add('O');
                }}),
                Arguments.of(new HashSet<Character>() {{
                    add('B');
                    add('S');
                }}),
                Arguments.of(new HashSet<Character>() {{
                    add('N');
                    add('O');
                }}),
                Arguments.of(new HashSet<Character>() {{
                    add('N');
                    add('S');
                }}),
                Arguments.of(new HashSet<Character>() {{
                    add('O');
                    add('S');
                }})
        );
    }

    private static String generateRandomTilordnetRessurs() {
        return RandUt.constructRandomAlphaNumString(OppgaveMapperUnitTest.random.nextInt(99), OppgaveMapperUnitTest.random);
    }

    private static String convertLocalDateTimeToZonedDateTime(final LocalDateTime localDateTime) {
        final String zonedDateTimeString;
        if (localDateTime == null) {
            zonedDateTimeString = null;
        } else {
            final ZonedDateTime zonedDateTime =
                    ZonedDateTime.of(localDateTime, ZoneId.ofOffset("", ZoneOffset.ofHours(2)));
            zonedDateTimeString = zonedDateTime.toString();
        }
        return zonedDateTimeString;
    }

    private static LocalDateTime generateRandomOpprettetTidspunkt() {
        return LocalDateTime.of(1999, 3, 5, 23, 19, 13, 17);
    }

    private static LocalDateTime generateRandomEndretTidspunkt() {
        return LocalDateTime.of(2000, 3, 5, 23, 19, 13, 17);
    }

    private static String generateRandomMappeId() {
        return RandUt.constructRandomAlphaNumString(random.nextInt(89), OppgaveMapperUnitTest.random);
    }

    private static String generateRandomTildeltEnhetsnr() {
        return RandUt.constructRandomAlphaNumString(random.nextInt(99), OppgaveMapperUnitTest.random);
    }

    private static String generateRandomPrioritet() {
        return RandUt.constructRandomAlphaNumString(random.nextInt(71), OppgaveMapperUnitTest.random);
    }

    private static String generateRandomId() {
        return RandUt.constructRandomAlphaNumString(random.nextInt(83), OppgaveMapperUnitTest.random);
    }

    private static String generateRandomTema() {
        return RandUt.constructRandomAlphaNumString(random.nextInt(91), OppgaveMapperUnitTest.random);
    }

    private static String generateRandomSamhandlernr() {
        return RandUt.constructRandomAlphaNumString(random.nextInt(61), OppgaveMapperUnitTest.random);
    }

    private static String generateRandomOrgnr() {
        return RandUt.constructRandomAlphaNumString(random.nextInt(73), OppgaveMapperUnitTest.random);
    }

    private static String generateRandomBnr() {
        return RandUt.constructRandomAlphaNumString(random.nextInt(97), OppgaveMapperUnitTest.random);
    }

    private static String generateRandomBehandlingstype() {
        return RandUt.constructRandomAlphaNumString(random.nextInt(99), OppgaveMapperUnitTest.random);
    }

    private static String generateRandomBeskrivelse() {
        return RandUt.constructRandomAlphaNumString(103 + random.nextInt(99), OppgaveMapperUnitTest.random);
    }

    private static String generateRandomBehandlingstema() {
        return RandUt.constructRandomAlphaNumString(random.nextInt(99), OppgaveMapperUnitTest.random);
    }

    private static String generateRandomAktoerId() {
        return RandUt.constructRandomAlphaNumString(random.nextInt(99), OppgaveMapperUnitTest.random);
    }

    private static String generateAktivTilDatoAstring() {
        final LocalDate aktivTilDato = generateAktivTilDato();
        return aktivTilDato.format(OppgaveMapperUnitTest.dateFormatter);
    }

    private static LocalDate generateAktivTilDato() {
        return LocalDate.of(1970 + random.nextInt(52), 1 + random.nextInt(12), 1 + random.nextInt(28));
    }

    private static String generateAktivDatoSAstring() {
        final LocalDate aktivDato = generateAktivDato();
        return aktivDato.format(OppgaveMapperUnitTest.dateFormatter);
    }

    private static LocalDate generateAktivDato() {
        return LocalDate.of(1970 + random.nextInt(51), 1 + random.nextInt(12), 1 + random.nextInt(28));
    }

    private static String generateRandomOppgavetype() {
        return RandUt.constructRandomAlphaNumString(random.nextInt(79), OppgaveMapperUnitTest.random);
    }

    private static String generateRandomNavPersonIdent() {
        return RandUt.constructRandomAlphaNumString(OppgaveMapperUnitTest.random.nextInt(87), OppgaveMapperUnitTest.random);
    }

    private static int generateRandomVersjon() {
        return Math.abs(OppgaveMapperUnitTest.random.nextInt());
    }

    private static Stream<Arguments> provideOppgaveDtoToOppgaveParms() {
        return Stream.of(
                Arguments.of(OppgaveStatus.AAPNET, null, null),
                Arguments.of(OppgaveStatus.FEILREGISTRERT, null, null),
                Arguments.of(OppgaveStatus.FERDIGSTILT, null, null),
                Arguments.of(OppgaveStatus.OPPRETTET, generateRandomOpprettetTidspunkt(), generateRandomEndretTidspunkt()),
                Arguments.of(OppgaveStatus.OPPRETTET, generateRandomOpprettetTidspunkt(), null),
                Arguments.of(OppgaveStatus.OPPRETTET, null, generateRandomEndretTidspunkt()),
                Arguments.of(OppgaveStatus.OPPRETTET, null, null),
                Arguments.of(OppgaveStatus.UNDER_BEHANDLING, null, null)
        );
    }

    @ParameterizedTest
    @ValueSource(chars = {'A', 'B', 'N', 'O', 'S'})
    void when_mapping_from_oppgave_to_oppgaveDto_then_all_fields_should_have_expected_values(final char aktorType) throws OppgaveMapperException_MoreThanOneActorType, OppgaveMapperException_AktivTilFraNull {

        enteringTestHeaderLogger.debug(null);

        final Oppgave expectedOppgave =
                new Oppgave.OppgaveBuilder()
                        .withAktivFra(OppgaveMapperUnitTest.generateAktivDato())
                        .withAktivTil(OppgaveMapperUnitTest.generateAktivTilDato())
                        .withAntallMeldinger(31)
                        .withSistEndret(OppgaveMapperUnitTest.generateRandomEndretTidspunkt())
                        .withAktoerId(aktorType == 'A' ? OppgaveMapperUnitTest.generateRandomAktoerId() : null)
                        .withAnsvarligEnhetId(OppgaveMapperUnitTest.generateRandomTildeltEnhetsnr())
                        .withAnsvarligSaksbehandlerIdent(OppgaveMapperUnitTest.generateRandomTilordnetRessurs())
                        .withBehandlingstema(OppgaveMapperUnitTest.generateRandomBehandlingstema())
                        .withBehandlingstype(OppgaveMapperUnitTest.generateRandomBehandlingstype())
                        .withBeskrivelse(OppgaveMapperUnitTest.generateRandomBeskrivelse())
                        .withBnr(aktorType == 'B' ? OppgaveMapperUnitTest.generateRandomBnr() : null)
                        .withFagomradeKode(OppgaveMapperUnitTest.generateRandomTema())
                        .withLest(RandUt.generateRandomBoolean(OppgaveMapperUnitTest.random))
                        .withMappeId(OppgaveMapperUnitTest.generateRandomMappeId())
                        .withNavPersonIdent(aktorType == 'N' ? OppgaveMapperUnitTest.generateRandomNavPersonIdent() : null)
                        .withOppgaveId(OppgaveMapperUnitTest.generateRandomId())
                        .withOppgavetypeKode(generateRandomOppgavetype())
                        .withOrgnr(aktorType == 'O' ? OppgaveMapperUnitTest.generateRandomOrgnr() : null)
                        .withPrioritetKode(OppgaveMapperUnitTest.generateRandomPrioritet())
                        .withSamhandlernr(aktorType == 'S' ? OppgaveMapperUnitTest.generateRandomSamhandlernr() : null)
                        .withVersjon(OppgaveMapperUnitTest.generateRandomVersjon())
                        .build();

        final OppgaveDto actualOppgaveDto = OppgaveMapper.map(expectedOppgave);

        assertEquals(OppgaveMapper.ENHET_ID_FOR_ANDRE_EKSTERNE, actualOppgaveDto.getOpprettetAvEnhetsnr(), "opprettetAvEnhetsnr");
        assertEquals(expectedOppgave.aktivFra.format(OppgaveMapperUnitTest.dateFormatter), actualOppgaveDto.getAktivDato(), "aktivFra");
        assertEquals(expectedOppgave.aktivTil.format(OppgaveMapperUnitTest.dateFormatter), actualOppgaveDto.getFristFerdigstillelse(), "fristFerdigstillelse");
        assertEquals(expectedOppgave.aktoerId, actualOppgaveDto.getAktoerId(), "aktoerId");
        assertEquals(expectedOppgave.ansvarligEnhetId, actualOppgaveDto.getTildeltEnhetsnr(), "tildeltEnhetsnr");
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
                        .withAktoerId(actorTypes.contains('A') ? RandUt.constructRandomAlphaNumString(random.nextInt(99), OppgaveMapperUnitTest.random) : null)
                        .withBnr(actorTypes.contains('B') ? RandUt.constructRandomAlphaNumString(random.nextInt(97), OppgaveMapperUnitTest.random) : null)
                        .withNavPersonIdent(actorTypes.contains('N') ? RandUt.constructRandomAlphaNumString(random.nextInt(87), OppgaveMapperUnitTest.random) : null)
                        .withOrgnr(actorTypes.contains('O') ? RandUt.constructRandomAlphaNumString(random.nextInt(73), OppgaveMapperUnitTest.random) : null)
                        .withSamhandlernr(actorTypes.contains('S') ? RandUt.constructRandomAlphaNumString(random.nextInt(61), OppgaveMapperUnitTest.random) : null)
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

    @ParameterizedTest
    @MethodSource("provideOppgaveDtoToOppgaveParms")
    void when_mapping_from_oppgaveDto_to_oppgave_then_all_fields_should_have_expected_values(
            final OppgaveStatus oppgaveStatus,
            final LocalDateTime opprettetTidspunkt,
            final LocalDateTime endretTidspunkt
    ) {

        enteringTestHeaderLogger.debug(null);

        final OppgaveDto expectedOppgaveDto = new OppgaveDto();

        expectedOppgaveDto.setAktivDato(OppgaveMapperUnitTest.generateAktivDatoSAstring());
        expectedOppgaveDto.setAktoerId(OppgaveMapperUnitTest.generateRandomAktoerId());
        expectedOppgaveDto.setBehandlingstema(OppgaveMapperUnitTest.generateRandomBehandlingstema());
        expectedOppgaveDto.setBehandlingstype(OppgaveMapperUnitTest.generateRandomBehandlingstype());
        expectedOppgaveDto.setBeskrivelse(OppgaveMapperUnitTest.generateRandomBeskrivelse());
        expectedOppgaveDto.setBnr(OppgaveMapperUnitTest.generateRandomBnr());
        expectedOppgaveDto.setEndretTidspunkt(OppgaveMapperUnitTest.convertLocalDateTimeToZonedDateTime(endretTidspunkt));
        expectedOppgaveDto.setFristFerdigstillelse(OppgaveMapperUnitTest.generateAktivTilDatoAstring());
        expectedOppgaveDto.setId(OppgaveMapperUnitTest.generateRandomId());
        expectedOppgaveDto.setMappeId(OppgaveMapperUnitTest.generateRandomMappeId());
        expectedOppgaveDto.setOppgavetype(OppgaveMapperUnitTest.generateRandomOppgavetype());
        expectedOppgaveDto.setOpprettetTidspunkt(OppgaveMapperUnitTest.convertLocalDateTimeToZonedDateTime(opprettetTidspunkt));
        expectedOppgaveDto.setOrgnr(OppgaveMapperUnitTest.generateRandomOrgnr());
        expectedOppgaveDto.setPrioritet(OppgaveMapperUnitTest.generateRandomPrioritet());
        expectedOppgaveDto.setSamhandlernr(OppgaveMapperUnitTest.generateRandomSamhandlernr());
        expectedOppgaveDto.setStatus(oppgaveStatus);
        expectedOppgaveDto.setTema(OppgaveMapperUnitTest.generateRandomTema());
        expectedOppgaveDto.setTildeltEnhetsnr(OppgaveMapperUnitTest.generateRandomTildeltEnhetsnr());
        expectedOppgaveDto.setTilordnetRessurs(OppgaveMapperUnitTest.generateRandomTilordnetRessurs());
        expectedOppgaveDto.setVersjon(OppgaveMapperUnitTest.generateRandomVersjon());

        final Oppgave actualOppgave = OppgaveMapper.map(expectedOppgaveDto);

        assertEquals(expectedOppgaveDto.getAktivDato(), actualOppgave.aktivFra.format(OppgaveMapperUnitTest.dateFormatter), "aktivDato");
        assertEquals(expectedOppgaveDto.getAktoerId(), actualOppgave.aktoerId, "aktoerId");
        assertEquals(expectedOppgaveDto.getBehandlingstema(), actualOppgave.behandlingstema, "behandlingstema");
        assertEquals(expectedOppgaveDto.getBehandlingstype(), actualOppgave.behandlingstype, "behandlingstype");
        assertEquals(expectedOppgaveDto.getBeskrivelse(), actualOppgave.beskrivelse, "beskrivelse");
        assertEquals(expectedOppgaveDto.getBnr(), actualOppgave.bnr, "bnr");
        assertEquals(expectedOppgaveDto.getEndretTidspunkt() == null ? expectedOppgaveDto.getOpprettetTidspunkt() : expectedOppgaveDto.getEndretTidspunkt(), actualOppgave.sistEndret, "sistEndret - endretTidspunkt");
        assertEquals(expectedOppgaveDto.getFristFerdigstillelse(), actualOppgave.aktivTil.format(OppgaveMapperUnitTest.dateFormatter), "fristFerdigstillelse");
        assertEquals(expectedOppgaveDto.getId(), actualOppgave.oppgaveId, "oppgaveId");
        assertEquals(expectedOppgaveDto.getMappeId(), actualOppgave.mappeId, "mappeId");
        assertEquals(expectedOppgaveDto.getOppgavetype(), actualOppgave.oppgavetypeKode, "oppgavetype");
        assertEquals(expectedOppgaveDto.getOrgnr(), actualOppgave.orgnr, "orgnr");
        assertEquals(expectedOppgaveDto.getPrioritet(), actualOppgave.prioritetKode, "prioritetKode");
        assertEquals(expectedOppgaveDto.getSamhandlernr(), actualOppgave.samhandlernr, "samhandlernr");
        assertEquals(expectedOppgaveDto.getStatus() != OppgaveStatus.OPPRETTET, actualOppgave.lest, "lest");
        assertEquals(expectedOppgaveDto.getTema(), actualOppgave.fagomradeKode, "tema - fagomradeKode");
        assertEquals(expectedOppgaveDto.getTildeltEnhetsnr(), actualOppgave.ansvarligEnhetId, "ansvarligEnhetId");
        assertEquals(expectedOppgaveDto.getTilordnetRessurs(), actualOppgave.ansvarligSaksbehandlerIdent, "ansvarligSaksbehandlerIdent");
        assertEquals(expectedOppgaveDto.getVersjon(), actualOppgave.versjon, "versjon");
    }
}