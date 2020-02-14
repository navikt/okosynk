package no.nav.okosynk.config;

import no.nav.okosynk.cli.AbstractAlertMetrics;
import no.nav.okosynk.cli.AbstractBatchMetrics;
import no.nav.okosynk.cli.FakeAlertMetrics;
import no.nav.okosynk.cli.FakeBatchMetrics;
import no.nav.okosynk.config.IOkosynkConfiguration;

public class FakeOkosynkConfiguration
    implements IOkosynkConfiguration {

  @Override
  public void setBoolean(final String key, final boolean defaultValue) {

    System.setProperty(
        key,
        Boolean.valueOf(defaultValue).toString());
  }

  @Override
  public boolean getBoolean(final String key, final boolean defaultValue) {

    return Boolean
        .parseBoolean(
            System.getProperty(key,
                Boolean.valueOf(defaultValue).toString())
        );
  }

  @Override
  public int getRequiredInt(final String key) {
    final String value = System.getProperty(key);
    if (value == null)  {
      throw new IllegalStateException(String.format("There is no system property set for the key " + key));
    }
    return Integer.valueOf(value);
  }

  @Override
  public String getRequiredString(final String key) {

    final String value = System.getProperty(key);
    if (value == null)  {
      throw new IllegalStateException(String.format("There is no system property set for the key " + key));
    }
    return value;
  }

  @Override
  public void clearSystemProperty(final String key) {
    System.clearProperty(key);
  }

  @Override
  public void setSystemProperty(final String key, final String value) {

    System.setProperty(key, value);
  }

  @Override
  public String getString(final String key) {

    return System.getProperty(key);
  }

  @Override
  public String getString(final String key, final String defaulValue) {

    return System.getProperty(key, defaulValue);
  }

  @Override
  public AbstractBatchMetrics getBatchMetrics(final Constants.BATCH_TYPE batchType) {
    return new FakeBatchMetrics(this, batchType);
  }

  @Override
  public AbstractAlertMetrics getAlertMetrics(final Constants.BATCH_TYPE batchType) {
    return new FakeAlertMetrics(this, batchType);
  }
}
