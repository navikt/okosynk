package no.nav.okosynk.batch;

import no.nav.okosynk.domain.AbstractMelding;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * TODO: The whole concept of this class is questioned. I keep it, however, to prevent unforeseen side effects by removing it. It is a reminiscence from the web asynchronous thread web version of this application .
 */
public class BatchRepository {

    private static final ConcurrentMap<Long, Batch<? extends AbstractMelding>> BATCH_ON_EXECUTION_ID =
        new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, Batch<? extends AbstractMelding>> BATCH_ON_NAME =
        new ConcurrentHashMap<>();

    public void leggTil(final Batch<? extends AbstractMelding> batch) {
        BATCH_ON_EXECUTION_ID.put(batch.getExecutionId(), batch);
        BATCH_ON_NAME.put(batch.getBatchName(), batch);
    }

    public Optional<Batch<? extends AbstractMelding>> hentBatch(String batchNavn) {
        return Optional.ofNullable(BATCH_ON_NAME.get(batchNavn));
    }

    public Optional<Batch<? extends AbstractMelding>> hentBatch(long executionId) {
        return Optional.ofNullable(BATCH_ON_EXECUTION_ID.get(executionId));
    }

    public void cleanTestRepository() {
        BATCH_ON_EXECUTION_ID.clear();
        BATCH_ON_NAME.clear();
    }
}
