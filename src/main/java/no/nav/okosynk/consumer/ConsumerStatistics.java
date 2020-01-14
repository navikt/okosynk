package no.nav.okosynk.consumer;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import no.nav.okosynk.config.Constants.BATCH_TYPE;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ConsumerStatistics {

  private final String name;
  private final int antallOppgaverSomErHentetFraDatabasen;
  private final int antallOppgaverSomMedSikkerhetErOpprettet;
  private final int antallOppgaverSomKanVaereOpprettet;
  private final int antallOppgaverSomMedSikkerhetIkkeErOpprettet;
  private final int antallOppgaverSomMedSikkerhetErOppdatert;
  private final int antallOppgaverSomMedSikkerhetIkkeErOppdatert;
  private final int antallOppgaverSomKanVaereOppdatert;
  private final int antallOppgaverSomMedSikkerhetErFerdigstilt;
  private final int antallOppgaverSomMedSikkerhetIkkeErFerdigstilt;
  private final int antallOppgaverSomKanVaereFerdigstilt;
  private final int numberOfExceptionsReceivedDuringRun;

  private ConsumerStatistics(final Builder builder) {

    Validate.notBlank(builder.name);

    this.name = builder.name;
    this.antallOppgaverSomErHentetFraDatabasen =
        builder.antallOppgaverSomErHentetFraDatabasen;
    this.antallOppgaverSomMedSikkerhetErOpprettet =
        builder.antallOppgaverSomMedSikkerhetErOpprettet;
    this.antallOppgaverSomKanVaereOpprettet =
        builder.antallOppgaverSomKanVaereOpprettet;
    this.antallOppgaverSomMedSikkerhetIkkeErOpprettet =
        builder.antallOppgaverSomMedSikkerhetIkkeErOpprettet;
    this.antallOppgaverSomMedSikkerhetErOppdatert =
        builder.antallOppgaverSomMedSikkerhetErOppdatert;
    this.antallOppgaverSomMedSikkerhetIkkeErOppdatert =
        builder.antallOppgaverSomMedSikkerhetIkkeErOppdatert;
    this.antallOppgaverSomKanVaereOppdatert =
        builder.antallOppgaverSomKanVaereOppdatert;
    this.antallOppgaverSomMedSikkerhetErFerdigstilt =
        builder.antallOppgaverSomMedSikkerhetErFerdigstilt;
    this.antallOppgaverSomMedSikkerhetIkkeErFerdigstilt =
        builder.antallOppgaverSomMedSikkerhetIkkeErFerdigstilt;
    this.antallOppgaverSomKanVaereFerdigstilt =
        builder.antallOppgaverSomKanVaereFerdigstilt;
    this.numberOfExceptionsReceivedDuringRun =
        builder.numberOfExceptionsReceivedDuringRun;
  }

  public static ConsumerStatistics zero(final BATCH_TYPE batchType) {
    return ConsumerStatistics.builder(batchType).build();
  }

  public static ConsumerStatistics zero(final String name) {
    return ConsumerStatistics.builder(name).build();
  }

  /**
   * Add corresponding metrics through all given immutable elements, and keep the name, that needs
   * to be the same for all of them. Create and return a new immutable element that is an
   * accumulation of all the given ones.
   *
   * @param consumerStatisticsArray The individual immutable elements that need to be accumulated.
   * @return a new immutable instance as accumulated by the given input elements
   */
  public static ConsumerStatistics addAll(final ConsumerStatistics... consumerStatisticsArray) {

    Validate.isTrue(consumerStatisticsArray.length > 0,
        "Cannot add an empty collection of ConsumerStatistics");
    Validate.noNullElements(consumerStatisticsArray, "At least one of the elements to sum is null");

    // Initiating the first one by picking
    // the name from a random element, choosing the first one.
    // Illegal to add elements with different names.
    ConsumerStatistics accumulatedConsumerStatistics =
        ConsumerStatistics.zero(consumerStatisticsArray[0].getName());
    for (int i = 0; i < consumerStatisticsArray.length; i++) {
      accumulatedConsumerStatistics = accumulatedConsumerStatistics.add(consumerStatisticsArray[i]);
    }

    return accumulatedConsumerStatistics;
  }

  /**
   * Add corresponding metrics from the given immutable element, and keep the name, that needs to be
   * the same for this and the other. Create and return a new immutable element that is an
   * accumulation of this and the other given element.
   *
   * @param other The element to accumulate with this one.
   * @return a new immutable instance as accumulated by the given input element and this one.
   */
  public ConsumerStatistics add(final ConsumerStatistics other) {

    Validate.notNull(other);
    Validate.isTrue(this.getName().equals(other.getName()),
        "Cannot add two instances of ConsumerStatistics with different names. this: %s, other: %s",
        this.getName(), other.getName());

    return ConsumerStatistics
        .builder()
        .name(this.name)
        .antallOppgaverSomErHentetFraDatabasen(other.antallOppgaverSomErHentetFraDatabasen
            + this.antallOppgaverSomErHentetFraDatabasen)
        .antallOppgaverSomMedSikkerhetErFerdigstilt(other.antallOppgaverSomMedSikkerhetErFerdigstilt
            + this.antallOppgaverSomMedSikkerhetErFerdigstilt)
        .antallOppgaverSomMedSikkerhetErOppdatert(other.antallOppgaverSomMedSikkerhetErOppdatert
            + this.antallOppgaverSomMedSikkerhetErOppdatert)
        .antallOppgaverSomMedSikkerhetErOpprettet(other.antallOppgaverSomMedSikkerhetErOpprettet
            + this.antallOppgaverSomMedSikkerhetErOpprettet)
        .antallOppgaverSomKanVaereFerdigstilt(
            other.antallOppgaverSomKanVaereFerdigstilt + this.antallOppgaverSomKanVaereFerdigstilt)
        .antallOppgaverSomKanVaereOppdatert(
            other.antallOppgaverSomKanVaereOppdatert + this.antallOppgaverSomKanVaereOppdatert)
        .antallOppgaverSomKanVaereOpprettet(
            other.antallOppgaverSomKanVaereOpprettet + this.antallOppgaverSomKanVaereOpprettet)
        .antallOppgaverSomMedSikkerhetIkkeErOpprettet(
            other.antallOppgaverSomMedSikkerhetIkkeErOpprettet
                + this.antallOppgaverSomMedSikkerhetIkkeErOpprettet)
        .antallOppgaverSomMedSikkerhetIkkeErFerdigstilt(
            other.antallOppgaverSomMedSikkerhetIkkeErFerdigstilt
                + this.antallOppgaverSomMedSikkerhetIkkeErFerdigstilt)
        .antallOppgaverSomMedSikkerhetIkkeErOppdatert(
            other.antallOppgaverSomMedSikkerhetIkkeErOppdatert
                + this.antallOppgaverSomMedSikkerhetIkkeErOppdatert)
        .numberOfExceptionsReceivedDuringRun(
            other.numberOfExceptionsReceivedDuringRun + this.numberOfExceptionsReceivedDuringRun)
        .build();
  }

  public static ConsumerStatistics.Builder builder() {
    return new ConsumerStatistics.Builder();
  }

  public static ConsumerStatistics.Builder builder(final BATCH_TYPE batchType) {
    return new ConsumerStatistics.Builder(batchType.getConsumerStatisticsName());
  }

  public static ConsumerStatistics.Builder builder(final String name) {
    return new ConsumerStatistics.Builder(name);
  }

  public static final class Builder {

    private String name;
    private int antallOppgaverSomErHentetFraDatabasen;
    private int antallOppgaverSomMedSikkerhetErOpprettet;
    private int antallOppgaverSomKanVaereOpprettet;
    private int antallOppgaverSomMedSikkerhetIkkeErOpprettet;
    private int antallOppgaverSomMedSikkerhetErOppdatert;
    private int antallOppgaverSomMedSikkerhetIkkeErOppdatert;
    private int antallOppgaverSomKanVaereOppdatert;
    private int antallOppgaverSomMedSikkerhetErFerdigstilt;
    private int antallOppgaverSomMedSikkerhetIkkeErFerdigstilt;
    private int antallOppgaverSomKanVaereFerdigstilt;
    private int numberOfExceptionsReceivedDuringRun;

    private Builder() {
    }

    private Builder(final String name) {
      this.name = name;
    }

    public ConsumerStatistics build() {
      return new ConsumerStatistics(this);
    }

    public Builder name(final String value) {
      this.name = value;
      return this;
    }

    public Builder antallOppgaverSomErHentetFraDatabasen(final int value) {
      this.antallOppgaverSomErHentetFraDatabasen = value;
      return this;
    }

    public Builder antallOppgaverSomMedSikkerhetErOpprettet(final int value) {
      this.antallOppgaverSomMedSikkerhetErOpprettet = value;
      return this;
    }

    public Builder antallOppgaverSomMedSikkerhetIkkeErOpprettet(final int value) {
      this.antallOppgaverSomMedSikkerhetIkkeErOpprettet = value;
      return this;
    }

    Builder antallOppgaverSomKanVaereOpprettet(final int value) {
      this.antallOppgaverSomKanVaereOpprettet = value;
      return this;
    }

    public Builder antallOppgaverSomMedSikkerhetErOppdatert(final int value) {
      this.antallOppgaverSomMedSikkerhetErOppdatert = value;
      return this;
    }

    public Builder antallOppgaverSomMedSikkerhetIkkeErOppdatert(final int value) {
      this.antallOppgaverSomMedSikkerhetIkkeErOppdatert = value;
      return this;
    }

    Builder antallOppgaverSomKanVaereOppdatert(final int value) {
      this.antallOppgaverSomKanVaereOppdatert = value;
      return this;
    }

    public Builder antallOppgaverSomMedSikkerhetErFerdigstilt(final int value) {
      this.antallOppgaverSomMedSikkerhetErFerdigstilt = value;
      return this;
    }

    public Builder antallOppgaverSomMedSikkerhetIkkeErFerdigstilt(final int value) {
      this.antallOppgaverSomMedSikkerhetIkkeErFerdigstilt = value;
      return this;
    }

    Builder antallOppgaverSomKanVaereFerdigstilt(final int value) {
      this.antallOppgaverSomKanVaereFerdigstilt = value;
      return this;
    }

    Builder numberOfExceptionsReceivedDuringRun(final int value) {
      this.numberOfExceptionsReceivedDuringRun = value;
      return this;
    }
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
        .append("name", "\"" + this.name + "\"")
        .append("antallOppgaverSomErHentetFraDatabasen", this.antallOppgaverSomErHentetFraDatabasen)
        .append("antallOppgaverSomMedSikkerhetErOpprettet",
            this.antallOppgaverSomMedSikkerhetErOpprettet)
        .append("antallOppgaverSomMedSikkerhetIkkeErOpprettet", this.antallOppgaverSomMedSikkerhetIkkeErOpprettet)
        .append("antallOppgaverSomKanVaereOpprettet", this.antallOppgaverSomKanVaereOpprettet)
        .append("antallOppgaverSomMedSikkerhetErOppdatert",
            this.antallOppgaverSomMedSikkerhetErOppdatert)
        .append("antallOppgaverSomMedSikkerhetIkkeErOppdatert",
            this.antallOppgaverSomMedSikkerhetIkkeErOppdatert)
        .append("antallOppgaverSomKanVaereOppdatert", this.antallOppgaverSomKanVaereOppdatert)
        .append("antallOppgaverSomMedSikkerhetErFerdigstilt",
            this.antallOppgaverSomMedSikkerhetErFerdigstilt)
        .append("antallOppgaverSomMedSikkerhetIkkeErFerdigstilt",
            this.antallOppgaverSomMedSikkerhetIkkeErFerdigstilt)
        .append("antallOppgaverSomKanVaereFerdigstilt", this.antallOppgaverSomKanVaereFerdigstilt)
        .append("numberOfExceptionsReceivedDuringRun", this.numberOfExceptionsReceivedDuringRun)
        .toString();
  }

  @Override
  public boolean equals(final Object other) {

    final boolean equals;
    if (
        other == null
            ||
            !(other instanceof ConsumerStatistics)
    ) {
      equals = false;
    } else {
      final ConsumerStatistics otherConsumerStatistics = (ConsumerStatistics) other;
      equals =
          (
              otherConsumerStatistics == this
          )
              ||
              (
                  this.name.equals(otherConsumerStatistics.getName())
                      &&
                      this.antallOppgaverSomErHentetFraDatabasen == otherConsumerStatistics
                          .getAntallOppgaverSomErHentetFraDatabasen()
                      &&
                      this.antallOppgaverSomMedSikkerhetErOpprettet == otherConsumerStatistics
                          .getAntallOppgaverSomMedSikkerhetErOpprettet()
                      &&
                      this.antallOppgaverSomKanVaereOpprettet == otherConsumerStatistics
                          .getAntallOppgaverSomKanVaereOpprettet()
                      &&
                      this.antallOppgaverSomMedSikkerhetErOppdatert == otherConsumerStatistics
                          .getAntallOppgaverSomMedSikkerhetErOppdatert()
                      &&
                      this.antallOppgaverSomMedSikkerhetIkkeErOppdatert == otherConsumerStatistics
                          .getAntallOppgaverSomMedSikkerhetIkkeErOppdatert()
                      &&
                      this.antallOppgaverSomKanVaereOppdatert == otherConsumerStatistics
                          .getAntallOppgaverSomKanVaereOppdatert()
                      &&
                      this.antallOppgaverSomMedSikkerhetErFerdigstilt == otherConsumerStatistics
                          .getAntallOppgaverSomMedSikkerhetErFerdigstilt()
                      &&
                      this.antallOppgaverSomMedSikkerhetIkkeErFerdigstilt == otherConsumerStatistics
                          .getAntallOppgaverSomMedSikkerhetIkkeErFerdigstilt()
                      &&
                      this.antallOppgaverSomKanVaereFerdigstilt == otherConsumerStatistics
                          .getAntallOppgaverSomKanVaereFerdigstilt()
                      &&
                      this.numberOfExceptionsReceivedDuringRun == otherConsumerStatistics
                          .getNumberOfExceptionsReceivedDuringRun()
              )
      ;
    }
    ;

    return equals;
  }

  public String getName() {
    return this.name;
  }

  public int getAntallOppgaverSomErHentetFraDatabasen() {
    return this.antallOppgaverSomErHentetFraDatabasen;
  }

  public int getAntallOppgaverSomMedSikkerhetErOpprettet() {
    return this.antallOppgaverSomMedSikkerhetErOpprettet;
  }

  public int getAntallOppgaverSomMedSikkerhetIkkeErOpprettet() {
    return this.antallOppgaverSomMedSikkerhetIkkeErOpprettet;
  }

  public int getAntallOppgaverSomKanVaereOpprettet() {
    return this.antallOppgaverSomKanVaereOpprettet;
  }

  public int getAntallOppgaverSomMedSikkerhetErOppdatert() {
    return this.antallOppgaverSomMedSikkerhetErOppdatert;
  }

  public int getAntallOppgaverSomMedSikkerhetIkkeErOppdatert() {
    return this.antallOppgaverSomMedSikkerhetIkkeErOppdatert;
  }

  public int getAntallOppgaverSomKanVaereOppdatert() {
    return this.antallOppgaverSomKanVaereOppdatert;
  }

  public int getAntallOppgaverSomMedSikkerhetErFerdigstilt() {
    return this.antallOppgaverSomMedSikkerhetErFerdigstilt;
  }

  public int getAntallOppgaverSomMedSikkerhetIkkeErFerdigstilt() {
    return this.antallOppgaverSomMedSikkerhetIkkeErFerdigstilt;
  }

  public int getAntallOppgaverSomKanVaereFerdigstilt() {
    return this.antallOppgaverSomKanVaereFerdigstilt;
  }

  public int getNumberOfExceptionsReceivedDuringRun() {
    return this.numberOfExceptionsReceivedDuringRun;
  }
}
