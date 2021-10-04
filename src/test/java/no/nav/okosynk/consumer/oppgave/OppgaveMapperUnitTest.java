package no.nav.okosynk.consumer.oppgave;

import no.nav.okosynk.consumer.oppgave.json.FinnOppgaveResponseJson;
import no.nav.okosynk.consumer.oppgave.json.IdentGruppeV2;
import no.nav.okosynk.consumer.oppgave.json.IdentJson;
import no.nav.okosynk.consumer.oppgave.json.PostOppgaveRequestJson;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    private static String generateAktivDatoAstring() {
        final LocalDate aktivDato = generateAktivDato();
        return aktivDato.format(OppgaveMapperUnitTest.dateFormatter);
    }

    private static LocalDate generateAktivDato() {
        return LocalDate.of(1970 + random.nextInt(51), 1 + random.nextInt(12), 1 + random.nextInt(28));
    }

    private static String generateRandomOppgavetype() {
        return RandUt.constructRandomAlphaNumString(random.nextInt(79), OppgaveMapperUnitTest.random);
    }

    private static String generateRandomFolkeregisterIdent() {
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

    private static Stream<Arguments> provideDifferentCombinationsOfAktoerIdAndIdenter() {

        final String expectedAktoerId1 = null;
        final String expectedAktoerId2 = generateRandomAktoerId();
        final Collection<IdentJson> identer1 = null;
        final Collection<IdentJson> identer2;
        {
            final Collection<IdentJson> identer = new ArrayList<>();
            final IdentJson identJson1 = new IdentJson(IdentGruppeV2.FOLKEREGISTERIDENT, expectedAktoerId2 + "xxx");
            identer.add(identJson1);
            final IdentJson identJson2 = new IdentJson(IdentGruppeV2.AKTOERID, expectedAktoerId2 + "kjnbkjnbjkbjk");
            identer.add(identJson2);
            identer2 = identer;
        }
        final Collection<IdentJson> identer3;
        {
            final Collection<IdentJson> identer = new ArrayList<>();
            final IdentJson identJson1 = new IdentJson(IdentGruppeV2.FOLKEREGISTERIDENT, expectedAktoerId2 + "xxx");
            identer.add(identJson1);
            identer3 = identer;
        }
        final Collection<IdentJson> identer4;
        {
            final Collection<IdentJson> identer = new ArrayList<>();
            final IdentJson identJson2 = new IdentJson(IdentGruppeV2.AKTOERID, expectedAktoerId2 + "kjnbkjnbjkbjk");
            identer.add(identJson2);
            identer4 = identer;
        }
        final Collection<IdentJson> identer5;
        {
            final Collection<IdentJson> identer = new ArrayList<>();
            final IdentJson identJson1 = new IdentJson(IdentGruppeV2.FOLKEREGISTERIDENT, null);
            identer.add(identJson1);
            final IdentJson identJson2 = new IdentJson(IdentGruppeV2.AKTOERID, null);
            identer.add(identJson2);
            identer5 = identer;
        }
        final Collection<IdentJson> identer6;
        {
            final Collection<IdentJson> identer = new ArrayList<>();
            final IdentJson identJson1 = new IdentJson(IdentGruppeV2.FOLKEREGISTERIDENT, null);
            identer.add(identJson1);
            identer6 = identer;
        }
        final Collection<IdentJson> identer7;
        {
            final Collection<IdentJson> identer = new ArrayList<>();
            final IdentJson identJson2 = new IdentJson(IdentGruppeV2.AKTOERID, null);
            identer.add(identJson2);
            identer7 = identer;
        }
        final Collection<IdentJson> identer8;
        {
            final Collection<IdentJson> identer = new ArrayList<>();
            identer8 = identer;
        }
        final Collection<IdentJson> identer9;
        {
            final Collection<IdentJson> identer = new ArrayList<>();
            final IdentJson identJson1 = new IdentJson(IdentGruppeV2.FOLKEREGISTERIDENT, null);
            identer.add(identJson1);
            final IdentJson identJson2 = null;
            identer.add(identJson2);
            identer9 = identer;
        }
        final Collection<IdentJson> identer10;
        {
            final Collection<IdentJson> identer = new ArrayList<>();
            final IdentJson identJson1 = null;
            identer.add(identJson1);
            final IdentJson identJson2 = new IdentJson(IdentGruppeV2.AKTOERID, null);
            identer.add(identJson2);
            identer10 = identer;
        }
        final Collection<IdentJson> identer11;
        {
            final Collection<IdentJson> identer = new ArrayList<>();
            final IdentJson identJson1 = new IdentJson(IdentGruppeV2.FOLKEREGISTERIDENT, null);
            identer.add(identJson1);
            final IdentJson identJson2 = new IdentJson(IdentGruppeV2.AKTOERID, expectedAktoerId2 + "kjnbkjnbjkbjk");
            identer.add(identJson2);
            identer11 = identer;
        }
        final Collection<IdentJson> identer12;
        {
            final Collection<IdentJson> identer = new ArrayList<>();
            final IdentJson identJson1 = new IdentJson(IdentGruppeV2.FOLKEREGISTERIDENT, expectedAktoerId2 + "xyz");
            identer.add(identJson1);
            final IdentJson identJson2 = new IdentJson(IdentGruppeV2.AKTOERID, null);
            identer.add(identJson2);
            identer12 = identer;
        }

        return Stream.of(
                Arguments.of(expectedAktoerId1, identer1, expectedAktoerId1, identer1),
                Arguments.of(expectedAktoerId2, identer1, expectedAktoerId2, identer1),
                Arguments.of(expectedAktoerId2, identer2, expectedAktoerId2, expectedAktoerId2 + "xxx"),
                Arguments.of(expectedAktoerId2, identer3, expectedAktoerId2, expectedAktoerId2 + "xxx"),
                Arguments.of(expectedAktoerId2, identer4, expectedAktoerId2, null),
                Arguments.of(expectedAktoerId2, identer5, expectedAktoerId2, null),
                Arguments.of(expectedAktoerId2, identer6, expectedAktoerId2, null),
                Arguments.of(expectedAktoerId2, identer7, expectedAktoerId2, null),
                Arguments.of(expectedAktoerId2, identer8, expectedAktoerId2, null),
                Arguments.of(expectedAktoerId2, identer9, expectedAktoerId2, null),
                Arguments.of(expectedAktoerId2, identer10, expectedAktoerId2, null),
                Arguments.of(expectedAktoerId2, identer11, expectedAktoerId2, null),
                Arguments.of(expectedAktoerId2, identer12, expectedAktoerId2, expectedAktoerId2 + "xyz")
        );
    }

    @ParameterizedTest
    @ValueSource(chars = {'A', 'B', 'N', 'O', 'S'})
    void when_mapping_from_oppgave_to_PostOppgaveRequestJson_then_all_fields_should_have_expected_values(final char aktorType) throws OppgaveMapperException_MoreThanOneActorType, OppgaveMapperException_AktivTilFraNull {

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
                        .withFolkeregisterIdent(aktorType == 'N' ? OppgaveMapperUnitTest.generateRandomFolkeregisterIdent() : null)
                        .withOppgaveId(OppgaveMapperUnitTest.generateRandomId())
                        .withOppgavetypeKode(generateRandomOppgavetype())
                        .withOrgnr(aktorType == 'O' ? OppgaveMapperUnitTest.generateRandomOrgnr() : null)
                        .withPrioritetKode(OppgaveMapperUnitTest.generateRandomPrioritet())
                        .withSamhandlernr(aktorType == 'S' ? OppgaveMapperUnitTest.generateRandomSamhandlernr() : null)
                        .withVersjon(OppgaveMapperUnitTest.generateRandomVersjon())
                        .build();

        final PostOppgaveRequestJson actualPostOppgaveRequestJson = OppgaveMapper.mapFromFinnOppgaveResponseJsonToOppgave(expectedOppgave);

        assertEquals(OppgaveMapper.ENHET_ID_FOR_ANDRE_EKSTERNE, actualPostOppgaveRequestJson.getOpprettetAvEnhetsnr(), "opprettetAvEnhetsnr");
        assertEquals(expectedOppgave.aktivFra.format(OppgaveMapperUnitTest.dateFormatter), actualPostOppgaveRequestJson.getAktivDato(), "aktivFra");
        assertEquals(expectedOppgave.aktivTil.format(OppgaveMapperUnitTest.dateFormatter), actualPostOppgaveRequestJson.getFristFerdigstillelse(), "fristFerdigstillelse");
        assertEquals(expectedOppgave.aktoerId, actualPostOppgaveRequestJson.getAktoerId(), "aktoerId");
        assertEquals(expectedOppgave.ansvarligEnhetId, actualPostOppgaveRequestJson.getTildeltEnhetsnr(), "tildeltEnhetsnr");
        assertEquals(expectedOppgave.behandlingstema, actualPostOppgaveRequestJson.getBehandlingstema(), "behandlingstema");
        assertEquals(expectedOppgave.behandlingstype, actualPostOppgaveRequestJson.getBehandlingstype(), "behandlingstype");
        assertEquals(expectedOppgave.beskrivelse, actualPostOppgaveRequestJson.getBeskrivelse(), "beskrivelse");
        assertEquals(expectedOppgave.bnr, actualPostOppgaveRequestJson.getBnr(), "bnr");
        assertEquals(expectedOppgave.folkeregisterIdent, actualPostOppgaveRequestJson.getFolkeregisterIdent(), "folkeregisterIdent");
        assertEquals(expectedOppgave.oppgavetypeKode, actualPostOppgaveRequestJson.getOppgavetype(), "oppgavetypeKode");
        assertEquals(expectedOppgave.orgnr, actualPostOppgaveRequestJson.getOrgnr(), "orgnr");
        assertEquals(expectedOppgave.prioritetKode, actualPostOppgaveRequestJson.getPrioritet(), "prioritetKode");
        assertEquals(expectedOppgave.samhandlernr, actualPostOppgaveRequestJson.getSamhandlernr(), "samhandlernr");

        assertEquals(null, actualPostOppgaveRequestJson.getBehandlesAvApplikasjon(), "behandlesAvApplikasjon");
        assertEquals(null, actualPostOppgaveRequestJson.getEndretAv(), "endretAv");
        assertEquals(null, actualPostOppgaveRequestJson.getEndretAvEnhetsnr(), "endretAvEnhetsnr");
        assertEquals(null, actualPostOppgaveRequestJson.getEndretTidspunkt(), "endretTidspunkt");
        assertEquals(null, actualPostOppgaveRequestJson.getFerdigstiltTidspunkt(), "ferdigstiltTidspunkt");
        assertEquals(null, actualPostOppgaveRequestJson.getId(), "id");
        assertEquals(null, actualPostOppgaveRequestJson.getJournalpostId(), "journalpostId");
        assertEquals(null, actualPostOppgaveRequestJson.getJournalpostkilde(), "journalpostkilde");
        assertEquals(null, actualPostOppgaveRequestJson.getMappeId(), "mappeId");
        assertEquals(null, actualPostOppgaveRequestJson.getMetadata(), "metadata");
        assertEquals(null, actualPostOppgaveRequestJson.getOpprettetAv(), "opprettetAv");
        assertEquals(null, actualPostOppgaveRequestJson.getOpprettetTidspunkt(), "opprettetTidspunkt");
        assertEquals(null, actualPostOppgaveRequestJson.getSaksreferanse(), "saksreferanse");
        assertEquals(null, actualPostOppgaveRequestJson.getStatus(), "status");
        assertEquals(null, actualPostOppgaveRequestJson.getStatus(), "status");
        assertEquals(null, actualPostOppgaveRequestJson.getTilordnetRessurs(), "tilordnetRessurs");
        assertEquals(null, actualPostOppgaveRequestJson.getVersjon(), "versjon");
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
                        .withFolkeregisterIdent(actorTypes.contains('N') ? RandUt.constructRandomAlphaNumString(random.nextInt(87), OppgaveMapperUnitTest.random) : null)
                        .withOrgnr(actorTypes.contains('O') ? RandUt.constructRandomAlphaNumString(random.nextInt(73), OppgaveMapperUnitTest.random) : null)
                        .withSamhandlernr(actorTypes.contains('S') ? RandUt.constructRandomAlphaNumString(random.nextInt(61), OppgaveMapperUnitTest.random) : null)
                        .build();

        assertThrows(OppgaveMapperException_MoreThanOneActorType.class, () -> OppgaveMapper.mapFromFinnOppgaveResponseJsonToOppgave(expectedOppgave));
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

        assertThrows(OppgaveMapperException_AktivTilFraNull.class, () -> OppgaveMapper.mapFromFinnOppgaveResponseJsonToOppgave(expectedOppgave));
    }

    @ParameterizedTest
    @MethodSource("provideOppgaveDtoToOppgaveParms")
    void when_mapping_from_FinnOppgaveResponseJson_to_oppgave_then_all_fields_should_have_expected_values(
            final OppgaveStatus oppgaveStatus,
            final LocalDateTime opprettetTidspunkt,
            final LocalDateTime endretTidspunkt
    ) {

        enteringTestHeaderLogger.debug(null);

        final FinnOppgaveResponseJson expectedFinnOppgaveResponseJson = new FinnOppgaveResponseJson();

        expectedFinnOppgaveResponseJson.setAktivDato(OppgaveMapperUnitTest.generateAktivDatoAstring());
        expectedFinnOppgaveResponseJson.setBehandlingstema(OppgaveMapperUnitTest.generateRandomBehandlingstema());
        expectedFinnOppgaveResponseJson.setBehandlingstype(OppgaveMapperUnitTest.generateRandomBehandlingstype());
        expectedFinnOppgaveResponseJson.setBeskrivelse(OppgaveMapperUnitTest.generateRandomBeskrivelse());
        expectedFinnOppgaveResponseJson.setEndretTidspunkt(OppgaveMapperUnitTest.convertLocalDateTimeToZonedDateTime(endretTidspunkt));
        expectedFinnOppgaveResponseJson.setFristFerdigstillelse(OppgaveMapperUnitTest.generateAktivTilDatoAstring());
        expectedFinnOppgaveResponseJson.setId(OppgaveMapperUnitTest.generateRandomId());
        expectedFinnOppgaveResponseJson.setMappeId(OppgaveMapperUnitTest.generateRandomMappeId());
        expectedFinnOppgaveResponseJson.setOppgavetype(OppgaveMapperUnitTest.generateRandomOppgavetype());
        expectedFinnOppgaveResponseJson.setOpprettetTidspunkt(OppgaveMapperUnitTest.convertLocalDateTimeToZonedDateTime(opprettetTidspunkt));
        expectedFinnOppgaveResponseJson.setPrioritet(OppgaveMapperUnitTest.generateRandomPrioritet());
        ;
        expectedFinnOppgaveResponseJson.setStatus(oppgaveStatus);
        expectedFinnOppgaveResponseJson.setTema(OppgaveMapperUnitTest.generateRandomTema());
        expectedFinnOppgaveResponseJson.setTildeltEnhetsnr(OppgaveMapperUnitTest.generateRandomTildeltEnhetsnr());
        expectedFinnOppgaveResponseJson.setTilordnetRessurs(OppgaveMapperUnitTest.generateRandomTilordnetRessurs());
        expectedFinnOppgaveResponseJson.setVersjon(OppgaveMapperUnitTest.generateRandomVersjon());

        expectedFinnOppgaveResponseJson.setIdenter(null);
        expectedFinnOppgaveResponseJson.setAktoerId(OppgaveMapperUnitTest.generateRandomAktoerId());
        expectedFinnOppgaveResponseJson.setBnr(OppgaveMapperUnitTest.generateRandomBnr());
        expectedFinnOppgaveResponseJson.setOrgnr(OppgaveMapperUnitTest.generateRandomOrgnr());
        expectedFinnOppgaveResponseJson.setSamhandlernr(OppgaveMapperUnitTest.generateRandomSamhandlernr());

        final Oppgave actualOppgave = OppgaveMapper.mapFromFinnOppgaveResponseJsonToOppgave(expectedFinnOppgaveResponseJson);

        assertEquals(expectedFinnOppgaveResponseJson.getAktivDato(), actualOppgave.aktivFra.format(OppgaveMapperUnitTest.dateFormatter), "aktivDato");
        assertEquals(expectedFinnOppgaveResponseJson.getBehandlingstema(), actualOppgave.behandlingstema, "behandlingstema");
        assertEquals(expectedFinnOppgaveResponseJson.getBehandlingstype(), actualOppgave.behandlingstype, "behandlingstype");
        assertEquals(expectedFinnOppgaveResponseJson.getBeskrivelse(), actualOppgave.beskrivelse, "beskrivelse");
        assertEquals(expectedFinnOppgaveResponseJson.getEndretTidspunkt() == null ? expectedFinnOppgaveResponseJson.getOpprettetTidspunkt() : expectedFinnOppgaveResponseJson.getEndretTidspunkt(), actualOppgave.sistEndret, "sistEndret - endretTidspunkt");
        assertEquals(expectedFinnOppgaveResponseJson.getFristFerdigstillelse(), actualOppgave.aktivTil.format(OppgaveMapperUnitTest.dateFormatter), "fristFerdigstillelse");
        assertEquals(expectedFinnOppgaveResponseJson.getId(), actualOppgave.oppgaveId, "oppgaveId");
        assertEquals(expectedFinnOppgaveResponseJson.getMappeId(), actualOppgave.mappeId, "mappeId");
        assertEquals(expectedFinnOppgaveResponseJson.getOppgavetype(), actualOppgave.oppgavetypeKode, "oppgavetype");
        assertEquals(expectedFinnOppgaveResponseJson.getPrioritet(), actualOppgave.prioritetKode, "prioritetKode");
        assertEquals(expectedFinnOppgaveResponseJson.getStatus() != OppgaveStatus.OPPRETTET, actualOppgave.lest, "lest");
        assertEquals(expectedFinnOppgaveResponseJson.getTema(), actualOppgave.fagomradeKode, "tema - fagomradeKode");
        assertEquals(expectedFinnOppgaveResponseJson.getTildeltEnhetsnr(), actualOppgave.ansvarligEnhetId, "ansvarligEnhetId");
        assertEquals(expectedFinnOppgaveResponseJson.getTilordnetRessurs(), actualOppgave.ansvarligSaksbehandlerIdent, "ansvarligSaksbehandlerIdent");
        assertEquals(expectedFinnOppgaveResponseJson.getVersjon(), actualOppgave.versjon, "versjon");

        assertNull(actualOppgave.folkeregisterIdent, "folkeregisterIdent");
        assertEquals(expectedFinnOppgaveResponseJson.getAktoerId(), actualOppgave.aktoerId, "aktoerId");
        assertEquals(expectedFinnOppgaveResponseJson.getBnr(), actualOppgave.bnr, "bnr");
        assertEquals(expectedFinnOppgaveResponseJson.getOrgnr(), actualOppgave.orgnr, "orgnr");
        assertEquals(expectedFinnOppgaveResponseJson.getSamhandlernr(), actualOppgave.samhandlernr, "samhandlernr");
    }

    @ParameterizedTest
    @MethodSource("provideDifferentCombinationsOfAktoerIdAndIdenter")
    void when_FinnOppgaveResponseJson_contains_different_values_for_aktoerId_and_identer_then_the_mapping_should_set_or_not_set_the_folkeregisterIdent_accordingly(
            final String aktoerId,
            final Collection<IdentJson> identer,
            final String expectedAktoerId,
            final String expectedFolkeregisterIdent
    ) {
        final FinnOppgaveResponseJson expectedFinnOppgaveResponseJson = new FinnOppgaveResponseJson();
        expectedFinnOppgaveResponseJson.setVersjon(131); // Integer/int type of problem, although it is irrelevant for this test

        expectedFinnOppgaveResponseJson.setAktoerId(aktoerId);
        expectedFinnOppgaveResponseJson.setIdenter(identer);

        final Oppgave actualOppgave = OppgaveMapper.mapFromFinnOppgaveResponseJsonToOppgave(expectedFinnOppgaveResponseJson);

        assertEquals(expectedAktoerId, actualOppgave.aktoerId, "aktoerId");
        assertEquals(expectedFolkeregisterIdent, actualOppgave.folkeregisterIdent, "folkeregisterIdent");
    }
}