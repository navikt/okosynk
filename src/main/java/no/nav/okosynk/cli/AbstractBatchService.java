package no.nav.okosynk.cli;

import no.nav.okosynk.batch.AbstractService;
import no.nav.okosynk.batch.BatchRepository;
import no.nav.okosynk.batch.BatchStatus;
import no.nav.okosynk.config.Constants;
import no.nav.okosynk.config.IOkosynkConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public abstract class AbstractBatchService {

  private static final Logger logger = LoggerFactory.getLogger(AbstractBatchService.class);

  private final IOkosynkConfiguration okosynkConfiguration;
  private final Constants.BATCH_TYPE batchType;
  private final AbstractService service;
  private boolean shouldRun;

  public AbstractBatchService(
      final IOkosynkConfiguration okosynkConfiguration,
      final Constants.BATCH_TYPE batchType) {

    final AbstractService service = createService(okosynkConfiguration);
    this.okosynkConfiguration = okosynkConfiguration;
    this.batchType = batchType;
    this.service = service;
    this.shouldRun = true;
  }

  public BatchStatus startBatchSynchronously() {
    MDC.put("batchnavn", getBatchNavn());
    BatchStatus batchStatus = null;
    try {
      logger
          .info("Mottatt kall til " + this.getClass().getSimpleName() + ".startBatchSynchronously");
      final AbstractService service = getService();
      batchStatus = service.startBatchSynchronously();
    } finally {
      MDC.remove(getBatchNavn());
      setShouldRun(batchStatus.failedButRerunningMaySucceed());
    }

    return batchStatus;
  }

  public IOkosynkConfiguration getOkosynkConfiguration() {
    return okosynkConfiguration;
  }

  public AbstractBatchService setShouldRun(final boolean shouldRun) {
    this.shouldRun = shouldRun;
    return this;
  }
  Constants.BATCH_TYPE getBatchType() {
    return batchType;
  }

  boolean shouldRun() {
    return this.shouldRun;
  }

  protected abstract AbstractService createService(final IOkosynkConfiguration okosynkConfiguration,
      final BatchRepository batchRepository);

  private AbstractService getService() {
    return this.service;
  }

  private AbstractService createService(final IOkosynkConfiguration okosynkConfiguration) {
    final BatchRepository batchRepository = new BatchRepository();
    return createService(okosynkConfiguration, batchRepository);
  }

  private String getBatchNavn() {
    return getBatchType().getName();
  }

  private String getStartBatchSynchronouslyTimerNavn() {
    return getBatchType().getBatchRunSynchronouslyTimerName();
  }
}