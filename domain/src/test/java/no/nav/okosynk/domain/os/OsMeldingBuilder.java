package no.nav.okosynk.domain.os;

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
