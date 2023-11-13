package no.nav.okosynk.hentbatchoppgaver.lagoppgave.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BeskrivelseInfoTest {
    @Test
    void osInfo() {
        assertThat(BeskrivelseInfo.sum.apply(
                anOsInfo("2001-01-01", "100", "2001-01-01", "2001-01-31"),
                anOsInfo("2001-01-01", "200", "2001-02-01", "2001-02-28")
        ).lagBeskrivelse())
                .contains("300kr")
                .contains("beregningsdato/id:01.01.01")
                .contains("periode:01.01.01-28.02.01");
    }

    @Test
    void listeMedOsInfo_skal_summere_belop_og_velge_tidligste_fom_og_seneste_tom() {
        List<BeskrivelseInfo> list = asList(
                anOsInfo("2001-01-01", "100", "2001-01-01", "2001-01-31"),
                anOsInfo("2001-01-01", "100", "2001-02-01", "2001-02-28"),
                anOsInfo("2001-01-01", "100", "2001-03-01", "2001-03-31"),
                anOsInfo("2001-01-01", "100", "2001-04-01", "2001-04-30"),
                anOsInfo("2001-01-01", "100", "2001-05-01", "2001-05-31"),
                anOsInfo("2001-01-01", "100", "2001-06-01", "2001-06-30")
        );
        Assertions.assertThat(
                list.stream().reduce(BeskrivelseInfo.sum).orElseThrow().lagBeskrivelse()
        )
                .contains("600kr")
                .contains("beregningsdato/id:01.01.01")
                .contains("periode:01.01.01-30.06.01");
    }

    @Test
    void urInfo() {
        assertThat(BeskrivelseInfo.sum.apply(
                anUrInfo("2001-01-01", "100"),
                anUrInfo("2001-02-01", "200")
        ).lagBeskrivelse())
                .contains("300kr")
                .contains("postert/bilagsnummer:01.01.01");
    }

    @Test
    void listeMedUrInfo_skal_summere_belop_og_velge_tidligste_posteringsdato() {
        List<BeskrivelseInfo> list = asList(
                anUrInfo("2001-01-01", "100"),
                anUrInfo("2001-02-01", "100"),
                anUrInfo("2001-03-01", "100"),
                anUrInfo("2001-04-01", "100"),
                anUrInfo("2001-05-01", "100"),
                anUrInfo("2001-06-01", "100")
        );

        assertThat(
                list.stream().reduce(BeskrivelseInfo.sum).orElseThrow().lagBeskrivelse()
        )
                .contains("600kr")
                .contains("postert/bilagsnummer:01.01.01");
    }

    @Test
    void listeMedUrInfo_og_OsInfo() {
        Stream<BeskrivelseInfo> blandings = Stream.of(
                anUrInfo("2001-01-01", "100"),
                anOsInfo("2001-01-01", "100", "2001-06-01", "2001-06-30")
        );

        assertThatThrownBy(
                () -> blandings.reduce(BeskrivelseInfo.sum)
        )
                .isInstanceOf(IllegalStateException.class);
    }

    private static OsBeskrivelseInfo anOsInfo(String beregningsdato, String belop, String fom, String tom) {
        return new OsBeskrivelseInfo("A", belop, "id", LocalDate.parse(beregningsdato), LocalDate.parse(fom), LocalDate.parse(tom), "", LocalDate.parse("2000-01-01"), "ok", "id", "id");
    }

    private static UrBeskrivelseInfo anUrInfo(String datoPostert, String belop) {
        return new UrBeskrivelseInfo("A", "årsak", LocalDate.parse(datoPostert), "id", belop, LocalDate.parse("2000-01-01"), "id", "id");
    }
}
