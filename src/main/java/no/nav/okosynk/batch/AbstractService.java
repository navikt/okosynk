package no.nav.okosynk.batch;

import no.nav.okosynk.cli.AbstractAlertMetrics;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import no.nav.okosynk.consumer.aktoer.AktoerRestClient;
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
  private BatchStatus lastBatchStatus;
  private AktoerRestClient aktoerRestClient;
  private Batch<? extends AbstractMelding> batch;
  private IMeldingLinjeFileReader meldingLinjeFileReader;

  protected AbstractService(
      final Constants.BATCH_TYPE  batchType,
      final IOkosynkConfiguration okosynkConfiguration) {

    this.batchType = batchType;
    this.okosynkConfiguration = okosynkConfiguration;
    this.shouldRun = true;
  }

  /**
   * Never throws. The outcome can be seen solely from the return code.
   * @return The outcome of the run.
   */
  public BatchStatus run() {

    final Batch<? extends AbstractMelding> batch = getBatch();
    batch.run();
    setLastBatchStatus(batch.getBatchStatus());
    setShouldRun(batch.getBatchStatus().failedButRerunningMaySucceed());
    setBatch(null);

    return batch.getBatchStatus();
  }

  public BatchStatus getLastBatchStatus() {
    return this.lastBatchStatus;
  }

  public AbstractService<MELDINGSTYPE> setShouldRun(final boolean shouldRun) {
    this.shouldRun = shouldRun;
    return this;
  }

  public boolean shouldRun() {
    return this.shouldRun;
  }

  public AbstractAlertMetrics getAlertMetrics() {
    return getOkosynkConfiguration().getAlertMetrics(getBatchType());
  }

  public Constants.BATCH_TYPE getBatchType() {
    return this.batchType;
  }

  public Batch<MELDINGSTYPE> createAndConfigureBatch(
      final IOkosynkConfiguration okosynkConfiguration) {

    final Batch<MELDINGSTYPE> batch = createBatch(okosynkConfiguration);
    batch.setUspesifikkMeldingLinjeReader(getMeldingLinjeReader(okosynkConfiguration));

    return batch;
  }

  protected AktoerRestClient createAktoerRestClient() {
    return new AktoerRestClient(getOkosynkConfiguration(), getBatchType());
  }

  protected abstract AbstractMeldingReader<MELDINGSTYPE> createMeldingReader();

  protected abstract IMeldingMapper<MELDINGSTYPE> createMeldingMapper(final AktoerRestClient aktoerRestClient);

  void setBatch(final Batch<? extends AbstractMelding> batch) {
    this.batch = batch;
  }

  void setAktoerRestClient(final AktoerRestClient aktoerRestClient) {
    this.aktoerRestClient = aktoerRestClient;
  }

  private IOkosynkConfiguration getOkosynkConfiguration() {
    return this.okosynkConfiguration;
  }

  private void setLastBatchStatus(final BatchStatus batchStatus) {
    this.lastBatchStatus = batchStatus;
  }

  private void setMeldingLinjeReader(final IMeldingLinjeFileReader meldingLinjeFileReader) {
    this.meldingLinjeFileReader = meldingLinjeFileReader;
  }

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
            createMeldingMapper(getAktoerRestClient())
        );

    return batch;
  }

  private IMeldingLinjeFileReader getMeldingLinjeReader(final IOkosynkConfiguration okosynkConfiguration) {

    if (this.meldingLinjeFileReader == null) {
      setMeldingLinjeReader(createMeldingLinjeReader(okosynkConfiguration));
    }
    return this.meldingLinjeFileReader;
  }

  private Batch<? extends AbstractMelding> getBatch() {
    if (this.batch == null) {
      setBatch(createAndConfigureBatch(getOkosynkConfiguration()));
    }
    return this.batch;
  }

  private AktoerRestClient getAktoerRestClient() {

    if (this.aktoerRestClient == null) {
      setAktoerRestClient(createAktoerRestClient());
    }
    return this.aktoerRestClient;
  }
}