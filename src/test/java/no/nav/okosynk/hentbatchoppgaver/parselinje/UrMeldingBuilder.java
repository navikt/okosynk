package no.nav.okosynk.hentbatchoppgaver.parselinje;

import no.nav.okosynk.hentbatchoppgaver.model.UrMelding;

public class UrMeldingBuilder {

    private UrMeldingBatchInputRecordBuilder meldingBatchInputRecordBuilder;

    public static UrMeldingBuilder newBuilder() {
        return new UrMeldingBuilder();
    }

    public UrMeldingBuilder withMeldingBatchInputRecordBuilder(final UrMeldingBatchInputRecordBuilder meldingBatchInputRecordBuilder) {
        this.meldingBatchInputRecordBuilder = meldingBatchInputRecordBuilder;
        return this;
    }

    public UrMelding build() {
        return new UrMelding(meldingBatchInputRecordBuilder.build());
    }
}
