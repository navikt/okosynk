package no.nav.okosynk.consumer;

import lombok.AccessLevel;
import lombok.Getter;
import no.nav.okosynk.config.IOkosynkConfiguration;

public class AbstractConsumerV3ServiceImpl {

    @Getter(AccessLevel.PROTECTED)
    private final IOkosynkConfiguration okosynkConfiguration;

    protected AbstractConsumerV3ServiceImpl(final IOkosynkConfiguration okosynkConfiguration) {
        this.okosynkConfiguration = okosynkConfiguration;
    }
}
