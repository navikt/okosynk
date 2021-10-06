package no.nav.okosynk.consumer.aktoer;

import com.fasterxml.jackson.core.JsonProcessingException;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.apache.commons.lang3.NotImplementedException;

public class PdlRestClient implements IAktoerClient {

    private final IOkosynkConfiguration okosynkConfiguration;
    private final Constants.BATCH_TYPE batchType;
    private final boolean shouldAlwaysThrow;

    public PdlRestClient(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType) {
        this(okosynkConfiguration, batchType, true);
    }

    PdlRestClient(
            final IOkosynkConfiguration okosynkConfiguration,
            final Constants.BATCH_TYPE batchType,
            final boolean shouldAlwaysThrow) {
        this.okosynkConfiguration = okosynkConfiguration;
        this.batchType = batchType;
        this.shouldAlwaysThrow = shouldAlwaysThrow;
    }

    private static PdlPersonIdentCollection hentAktivePdlIdenter(
            final String folkeregisterIdent,
            final IOkosynkConfiguration okosynkConfiguration,
            final String selfAuthenticationToken
    ) {
        return PdlPersonIdentCollection
                .builder()
                .withPdlPersonIdent(
                        PdlPersonIdent
                                .builder()
                                .withIdent("RUBBISH")
                                .build()
                )
                .build();
    }

    @Override
    public AktoerRespons hentGjeldendeAktoerId(final String folkeregisterIdent) {

        if (this.shouldAlwaysThrow) {
            throw new NotImplementedException();
        }

        final PdlPersonIdentCollection pdlPersonIdentCollection =
                PdlRestClient.hentAktivePdlIdenter(
                        folkeregisterIdent,
                        this.okosynkConfiguration,
                        getSelfAuthenticationToken()
                );

        final String aktorId = pdlPersonIdentCollection
                .extractGjeldendeAktorIdPdlPersonIdent()
                .orElseGet(() -> PdlPersonIdent.builder().withGruppe(null).withIdent(null).build())
                .getIdent();

        if (true) {
            //throw new NotImplementedException("Exception received when trying to parse the response");
        }

        return aktorId == null ?
                AktoerRespons.feil("Finnes ikke")
                :
                AktoerRespons.ok(aktorId);
    }

    private String getSelfAuthenticationToken() {
        return "DummyPlaceholderDuringDevelopment";
    }
}