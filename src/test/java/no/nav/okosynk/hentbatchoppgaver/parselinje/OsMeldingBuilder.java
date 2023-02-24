package no.nav.okosynk.hentbatchoppgaver.parselinje;

import no.nav.okosynk.hentbatchoppgaver.lagoppgave.OsMeldingBatchInputRecordBuilder;
import no.nav.okosynk.hentbatchoppgaver.model.OsMelding;

public class OsMeldingBuilder {

    private OsMeldingBatchInputRecordBuilder meldingBatchInputRecordBuilder;

    public static OsMeldingBuilder newBuilder() {
        return new OsMeldingBuilder();
    }

    public OsMeldingBuilder withMeldingBatchInputRecordBuilder(final OsMeldingBatchInputRecordBuilder meldingBatchInputRecordBuilder) {
        this.meldingBatchInputRecordBuilder = meldingBatchInputRecordBuilder;
        return this;
    }

    public OsMelding build() {
        return new OsMelding(meldingBatchInputRecordBuilder.build());
    }
}
