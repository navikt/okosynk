package no.nav.okosynk;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class PropertiesFilTest {

    private static final char PARAMETER_SKILLETEGN = ',';
    private static final char NOKKEL_VERDI_SKILLETEGN = '=';
    private static final int NOKKEL_INDEKS = 0;
    private static final List<Character> KOMMENTAR_PREFIKSER = Arrays.asList('#', '!');

    private static final String OS_MAPPING_REGEL_PROPERTIES_FILNAVN = "os_mapping_regler.properties";
    private static final String UR_MAPPING_REGEL_PROPERTIES_FILNAVN = "ur_mapping_regler.properties";

    private String propertiesFilnavn;

    @Test
    void urMappingPropertiesErPaaRettFormat() throws IOException {

        propertiesFilErPaaRettFormat(UR_MAPPING_REGEL_PROPERTIES_FILNAVN);
    }

    @Test
    void osMappingPropertiesErPaaRettFormat() throws IOException {

        propertiesFilErPaaRettFormat(OS_MAPPING_REGEL_PROPERTIES_FILNAVN);
    }

    @Test
    void osMappingPropertiesInneholderIkkeSammeNokkelToGanger() throws IOException {

        propertiesFilInneholderIkkeSammeNokkelToGanger(OS_MAPPING_REGEL_PROPERTIES_FILNAVN);
    }

    @Test
    void urMappingPropertiesInneholderIkkeSammeNokkelToGanger() throws IOException {

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
        if (erFaktiskLinje(linje) && harInnhold(linje)) {
            validerLinjeFormat(linje);
        }
    }

    private boolean harInnhold(String linje) {
        return !linje.trim().isEmpty();
    }

    private boolean erFaktiskLinje(String linje) {
        String trimmetLinje = linje.trim();

        if (trimmetLinje.isEmpty()) {
            return true;
        } else {
            char forsteTegnSomIkkeErWhitespace = trimmetLinje.charAt(0);
            return !KOMMENTAR_PREFIKSER.contains(forsteTegnSomIkkeErWhitespace);
        }
    }

    private void validerLinjeFormat(String linje) {
        if (linje.contains(Character.toString(NOKKEL_VERDI_SKILLETEGN))) {
            validerProperty(linje);
        } else {
            Assertions.fail(String.format("Feil format på '%s'.", propertiesFilnavn));
        }
    }

    private void validerProperty(String linje) {
        String[] properties = linje.split(Character.toString(NOKKEL_VERDI_SKILLETEGN).trim());
        String oppdragsInformasjon = properties[0];
        String underkategoriInformasjon = properties[1];
        if (inneholderSkilletegn(oppdragsInformasjon, underkategoriInformasjon)) {
            sjekkOmKodeEllerEnhetErBlank(oppdragsInformasjon, underkategoriInformasjon);
        } else {
            Assertions.fail(String.format("Feil format på '%s'.", propertiesFilnavn));
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
            Assertions.fail(String.format("Mangler verdier for ett eller flere felter i '%s'.", propertiesFilnavn));
        }
    }

    private void propertiesFilInneholderIkkeSammeNokkelToGanger(final String propertiesFilnavn) throws IOException {
        BufferedReader reader = lagBufferedReader(propertiesFilnavn);

        Set<String> nokler = new HashSet<>();

        String linje;
        while ((linje = reader.readLine()) != null) {
            if (erFaktiskLinje(linje) && harInnhold(linje)) {
                String nokkel = linje.split(Character.toString(NOKKEL_VERDI_SKILLETEGN))[NOKKEL_INDEKS].trim();

                if (nokler.contains(nokkel)) {
                    Assertions.fail(String.format("Nøkkel '%s' forekommer flere ganger i filen '%s'.", nokkel, propertiesFilnavn));
                } else {
                    nokler.add(nokkel);
                }
            }
        }
    }
}
