package no.nav.okosynk.hentbatchoppgaver.lagoppgave.model;

import java.util.function.BinaryOperator;

public interface BeskrivelseInfo {
    BinaryOperator<BeskrivelseInfo> sum = (a, b) ->
            (a instanceof OsBeskrivelseInfo osa && b instanceof OsBeskrivelseInfo osb) ? osa.pluss(osb)
                    : (a instanceof UrBeskrivelseInfo ura && b instanceof UrBeskrivelseInfo urb) ? ura.pluss(urb)
                    : kanIkkeBehandleToForskjelligeInfo();

    String lagBeskrivelse();

    static BeskrivelseInfo kanIkkeBehandleToForskjelligeInfo() {
        throw new IllegalStateException("Kan ikke behandle en blanding av UR-meldinger og OS-meldinger");
    }

}
