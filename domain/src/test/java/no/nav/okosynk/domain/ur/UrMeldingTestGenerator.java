package no.nav.okosynk.domain.ur;

import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public class UrMeldingTestGenerator {

    public static class EksempelMelding {
        public static final String personGjelderId = "10108000398";
        public static final String datoForStatus = "2011-01-28";
        public static final String nyesteVentestatus = "25";
        public static final String brukerId = "";
        public static final String totaltNettoBelop = "1940.0";
        public static final String behandlendeEnhet = "4819";
        public static final String gjelderIdType = "PERSON";
        public static final String oppdragsKode = "PEN";
        public static final String kilde = "UR230";
        public static final String datoPostert = "2011-01-21";
        public static final String bilagsId = "123456789";
        public static final String arsaksTekst = "Mottakers konto er oppgjort";
        public static final String mottakerId = "05073500186";

        public static final String getMelding() {
            return getUrMelding(personGjelderId);
        }

        public static final String getMelding(String gjelderId) {
            return getUrMelding(gjelderId);
        }

        private static String getUrMelding(String gjelderId) {
            return gjelderId +
                    gjelderIdType + "      " +
                    datoForStatus +
                    "T18:25:5825          00000000019400æ" +
                    behandlendeEnhet +
                    oppdragsKode + "    " +
                    kilde +
                    datoPostert +
                    bilagsId +
                    arsaksTekst + "                       " +
                    mottakerId;
        }

        public static final String withBehandlendeEnhet(String behandlendeEnhet){
            return personGjelderId +
                    gjelderIdType + "      " +
                    datoForStatus +
                    "T18:25:5825          00000000019400æ" +
                    behandlendeEnhet +
                    oppdragsKode + "    " +
                    kilde +
                    datoPostert +
                    bilagsId +
                    arsaksTekst + "                       " +
                    mottakerId;
        }
    }

    public static Stream<Arguments> urMeldingAndExpectedProvider() {
        return Stream.of(
                Arguments.of("10108000398", "PERSON", "10108000398PERSON      2011-01-28T18:25:5825          00000000019400æ8020INNT   UR2302011-01-21342552558Mottakers konto er oppgjort                       10108000398"),
                Arguments.of("80123240327", "SAMHANDLER", "80123240327ORGANISASJON2011-01-28T18:25:5825          00000000019400æ8020INNT   UR2302011-01-21342552558Mottakers konto er oppgjort                       10108000398"),
                Arguments.of("90123240327", "SAMHANDLER", "90123240327ORGANISASJON2011-01-28T18:25:5825          00000000019400æ8020INNT   UR2302011-01-21342552558Mottakers konto er oppgjort                       10108000398"),
                Arguments.of("123456789", "ORGANISASJON", "00123456789ORGANISASJON2011-01-28T18:25:5825          00000000019400æ8020INNT   UR2302011-01-21342552558Mottakers konto er oppgjort                       10108000398")
        );
    }
}
