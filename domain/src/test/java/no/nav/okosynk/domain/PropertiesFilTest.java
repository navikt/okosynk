package no.nav.okosynk.domain;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.fail;

class PropertiesFilTest {

    private static final Logger enteringTestHeaderLogger =
        LoggerFactory.getLogger("EnteringTestHeader");

    private static final char PARAMETER_SKILLETEGN = ',';
    private static final char NOKKEL_VERDI_SKILLETEGN = '=';
    private static final int NOKKEL_INDEKS = 0;
    private static final List<Character> KOMMENTAR_PREFIKSER = Arrays.asList('#', '!');

    private static final String OS_MAPPING_REGEL_PROPERTIES_FILNAVN = "os_mapping_regler.properties";
    private static final String UR_MAPPING_REGEL_PROPERTIES_FILNAVN = "ur_mapping_regler.properties";

    private String propertiesFilnavn;

    @Test
    void urMappingPropertiesErPaaRettFormat() throws IOException {

        enteringTestHeaderLogger.debug(null);

        propertiesFilErPaaRettFormat(UR_MAPPING_REGEL_PROPERTIES_FILNAVN);
    }

    @Test
    void osMappingPropertiesErPaaRettFormat() throws IOException {

        enteringTestHeaderLogger.debug(null);

        propertiesFilErPaaRettFormat(OS_MAPPING_REGEL_PROPERTIES_FILNAVN);
    }

    @Test
    void osMappingPropertiesInneholderIkkeSammeNokkelToGanger() throws IOException {

        enteringTestHeaderLogger.debug(null);

        propertiesFilInneholderIkkeSammeNokkelToGanger(OS_MAPPING_REGEL_PROPERTIES_FILNAVN);
    }

    @Test
    void urMappingPropertiesInneholderIkkeSammeNokkelToGanger() throws IOException {

        enteringTestHeaderLogger.debug(null);

        propertiesFilInneholderIkkeSammeNokkelToGanger(UR_MAPPING_REGEL_PROPERTIES_FILNAVN);
    }

    private void propertiesFilErPaaRettFormat(String propertiesFilnavn) throws IOException {

        this.propertiesFilnavn = propertiesFilnavn;

        BufferedReader reader = lagBufferedReader(propertiesFilnavn);
        validerPropertiesFil(reader);
    }

    private BufferedReader lagBufferedReader(final String propertiesFilnavn) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(propertiesFilnavn);
        return new BufferedReader(new InputStreamReader(inputStream));
    }

    private void validerPropertiesFil(BufferedReader reader) throws IOException {
        String linje;
        while ((linje = reader.readLine()) != null) {
            sjekkLinjeFormat(linje);
        }
    }

    private void sjekkLinjeFormat(String linje) {
        if (!erKommentar(linje) && !erTomLinje(linje)) {
            validerLinjeFormat(linje);
        }
    }

    private boolean erTomLinje(String linje) {
        return linje.trim().isEmpty();
    }

    private boolean erKommentar(String linje) {
        String trimmetLinje = linje.trim();

        if (trimmetLinje.isEmpty()) {
            return false;
        } else {
            char forsteTegnSomIkkeErWhitespace = trimmetLinje.charAt(0);
            return KOMMENTAR_PREFIKSER.contains(forsteTegnSomIkkeErWhitespace);
        }
    }

    private void validerLinjeFormat(String linje) {
        if (linje.contains(Character.toString(NOKKEL_VERDI_SKILLETEGN))) {
            validerProperty(linje);
        } else {
            fail(String.format("Feil format på '.", propertiesFilnavn));
        }
    }

    private void validerProperty(String linje) {
        String[] properties = linje.split(Character.toString(NOKKEL_VERDI_SKILLETEGN).trim());
        String oppdragsInformasjon = properties[0];
        String underkategoriInformasjon = properties[1];
        if (inneholderSkilletegn(oppdragsInformasjon, underkategoriInformasjon)) {
            sjekkOmKodeEllerEnhetErBlank(oppdragsInformasjon, underkategoriInformasjon);
        } else {
            fail(String.format("Feil format på '.", propertiesFilnavn));
        }
    }

    private boolean inneholderSkilletegn(String oppdragsInformasjon, String underkategoriInformasjon) {
        return oppdragsInformasjon.contains(Character.toString(PARAMETER_SKILLETEGN)) && underkategoriInformasjon.contains(Character.toString(PARAMETER_SKILLETEGN));
    }

    private void sjekkOmKodeEllerEnhetErBlank(String oppdragsInformasjon, String underkategoriInformasjon) {
        String oppdragsKode = oppdragsInformasjon.split(Character.toString(PARAMETER_SKILLETEGN))[0];
        String behandlendeEnhet = oppdragsInformasjon.split(Character.toString(PARAMETER_SKILLETEGN))[1];
        String behandlingstema = underkategoriInformasjon.split(Character.toString(PARAMETER_SKILLETEGN))[0];
        String behandlingstype = underkategoriInformasjon.split(Character.toString(PARAMETER_SKILLETEGN))[1];
        String ansvarligEnhet = underkategoriInformasjon.split(Character.toString(PARAMETER_SKILLETEGN))[2];
        if (StringUtils.isBlank(oppdragsKode) || StringUtils.isBlank(behandlendeEnhet) || StringUtils.isBlank(ansvarligEnhet) || StringUtils.isAllBlank(behandlingstema, behandlingstype)) {
            fail(String.format("Mangler verdier for ett eller flere felter i '.", propertiesFilnavn));
        }
    }

    private void propertiesFilInneholderIkkeSammeNokkelToGanger(final String propertiesFilnavn) throws IOException {
        BufferedReader reader = lagBufferedReader(propertiesFilnavn);

        Set<String> nokler = new HashSet<>();

        String linje;
        while ((linje = reader.readLine()) != null) {
            if (!erKommentar(linje) && !erTomLinje(linje)) {
                String nokkel = linje.split(Character.toString(NOKKEL_VERDI_SKILLETEGN))[NOKKEL_INDEKS].trim();

                if (nokler.contains(nokkel)) {
                    fail(String.format("Nøkkel '%s' forekommer flere ganger i filen '%s'.", nokkel, propertiesFilnavn));
                } else {
                    nokler.add(nokkel);
                }
            }
        }
    }
}
