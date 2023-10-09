package no.nav.okosynk.hentbatchoppgaver.model;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class OsMeldingTestGenerator {

    private static final String OS_MELDING = "2009-07-042009-09-26RETUK231B3502009-05-012009-07-31000000012300æ 8020         INNT    06025800174            ";

    public static String withGjelderIdOrganiasjon() {
        String organisasjonGjelderId = OsMelding.ORGANISASJON_PREFIKS + "125240327022828640";
        return organisasjonGjelderId + " " + OS_MELDING;
    }

    public static String withGjelderIdPerson() {
        String personGjelderId = "10108000398022828640";
        return personGjelderId + " " + OS_MELDING;
    }

    public static Stream<Arguments> osMeldingAndExpectedProvider() {
        return Stream.of(
                Arguments.of("10108000398", "AKTORID", /*...*/ "10108000398024544313 2009-08-072009-09-26RETUK231B3502009-08-012009-08-31000000004100æ 8020         KORTTID 10108000398            "),
                Arguments.of("80123240327", "SAMHANDLER", /**/ "80123240327024544313 2009-08-072009-09-26RETUK231B3502009-08-012009-08-31000000004100æ 8020         KORTTID 10108000398            "),
                Arguments.of("90123240327", "SAMHANDLER", /**/ "90123240327024544313 2009-08-072009-09-26RETUK231B3502009-08-012009-08-31000000004100æ 8020         KORTTID 10108000398            "),
                Arguments.of("123456789", "ORGANISASJON", /**/ "00123456789055209429 2010-12-222010-12-30RETUK231B3502010-12-012010-12-31000001456070H 8020         REFARBG 80000510102            ")
        );
    }

    public static class OsMeldingForPerson {
        public static final String beregningsId = "022838640";
        public static final String beregningsdato = "2009-07-04";
        public static final String forsteFomIPeriode = "2009-05-01";
        public static final String sisteTomIPeriode = "2009-07-31";
        public static final String flaggFeilkonto = " ";
        public static final String faggruppe = "INNT";
        public static final String utbetalesTilId = "06025800174";
        public static final String etteroppgjor = "";
        public static final String personGjelderId = "10108000398";
        public static final String datoForStatus = "2009-07-04";
        public static final String nyesteVentestatus = "RETU";
        public static final String brukerId = "X1234567";
        public static final String totaltNettoBelop = "1230.0";
        public static final String behandleneEnhet = "8020";

        private static final String OS_MELDING = beregningsId + " " + beregningsdato + datoForStatus +
                nyesteVentestatus + brukerId + forsteFomIPeriode + sisteTomIPeriode + "000000012300æ " +
                behandleneEnhet + "         " + faggruppe + "    " + utbetalesTilId + "            ";

        public static String getMelding() {
            return personGjelderId + OS_MELDING;
        }
    }
}
