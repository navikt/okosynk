package no.nav.okosynk.batch;

import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.domain.AbstractMelding;
import no.nav.okosynk.domain.AbstractMeldingReader;
import no.nav.okosynk.domain.IMeldingMapper;
import no.nav.okosynk.io.IMeldingLinjeFileReader;
import no.nav.okosynk.io.MeldingLinjeReaderWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractService<MELDINGSTYPE extends AbstractMelding> {

  private static final Logger logger = LoggerFactory.getLogger(AbstractService.class);

  private final Constants.BATCH_TYPE batchType;
  private final IOkosynkConfiguration okosynkConfiguration;
  private boolean shouldRun;

  protected AbstractService(
      final Constants.BATCH_TYPE  batchType,
      final IOkosynkConfiguration okosynkConfiguration) {

    this.batchType = batchType;
    this.okosynkConfiguration = okosynkConfiguration;
    this.shouldRun = true;
  }

  public BatchStatus run() {

    final IOkosynkConfiguration okosynkConfiguration = getOkosynkConfiguration();
    BatchStatus batchStatus = null;
    try {
      final Batch<? extends AbstractMelding> batch = createAndConfigureBatch(okosynkConfiguration);
      batch.run();
      batchStatus = batch.getBatchStatus();
    } catch (Throwable e) {
      logger.error("Exception received when waiting for batchService to finish.", e);
      batchStatus = BatchStatus.ERROR;
    } finally {
      setShouldRun(batchStatus.failedButRerunningMaySucceed());
    }

    return batchStatus;
  }

  public Batch<MELDINGSTYPE> createAndConfigureBatch(
      final IOkosynkConfiguration okosynkConfiguration) {

    final Batch<MELDINGSTYPE> batch = createBatch(okosynkConfiguration);
    batch.setMeldingLinjeReader(createMeldingLinjeReader(okosynkConfiguration));

    return batch;
  }

  public AbstractService setShouldRun(final boolean shouldRun) {
    this.shouldRun = shouldRun;
    return this;
  }

  public boolean shouldRun() {
    return this.shouldRun;
  }

  public Constants.BATCH_TYPE getBatchType() {
    return batchType;
  }

  public IOkosynkConfiguration getOkosynkConfiguration() {
    return okosynkConfiguration;
  }

  protected abstract AbstractMeldingReader<MELDINGSTYPE> createMeldingReader();

  protected abstract IMeldingMapper<MELDINGSTYPE> createMeldingMapper();

  private IMeldingLinjeFileReader createMeldingLinjeReader(
      final IOkosynkConfiguration okosynkConfiguration) {
    return new MeldingLinjeReaderWrapper(okosynkConfiguration, this.getBatchType());
  }

  private Batch<MELDINGSTYPE> createBatch(final IOkosynkConfiguration okosynkConfiguration) {

    final Batch<MELDINGSTYPE> batch =
        new Batch<>(
            okosynkConfiguration,
            getBatchType(),
            createMeldingReader(),
            createMeldingMapper()
        );

    return batch;
  }
}
