package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * One match statistic rendered as a mirrored home/away bar by the {@code stat-bar} card layout:
 * raw values plus precomputed percentage widths.
 */
public class SyntheticStatBarCard extends AbstractLeagueCard {

  private final String label;
  private final String homeValue;
  private final String awayValue;
  private final String homePct;
  private final String awayPct;
  private String homeColor = "";
  private String awayColor = "";

  public SyntheticStatBarCard(@Nonnull final String label, @Nonnull final String homeValue,
      @Nonnull final String awayValue, @Nonnull final String homePct,
      @Nonnull final String awayPct, @Nonnull final BaseSlingModelDataSource dataSource,
      @Nonnull final String resourcePrefix, @Nullable final String forcedResourceName)
      throws ComponentConfigurationException {
    super(dataSource, resourcePrefix, forcedResourceName);
    this.label = label;
    this.homeValue = homeValue;
    this.awayValue = awayValue;
    this.homePct = homePct;
    this.awayPct = awayPct;
  }

  @Override
  public String getLayout() {
    return "stat-bar";
  }

  public String getLabel() {
    return label;
  }

  public String getHomeValue() {
    return homeValue;
  }

  public String getAwayValue() {
    return awayValue;
  }

  public String getHomePct() {
    return homePct;
  }

  public String getAwayPct() {
    return awayPct;
  }

  /** Club colors for the bar fills (contrast-gated; away empty = league navy fallback). */
  public SyntheticStatBarCard withColors(@Nullable final String home, @Nullable final String away) {
    this.homeColor = home == null ? "" : home;
    this.awayColor = away == null ? "" : away;
    return this;
  }

  public String getHomeColor() {
    return homeColor;
  }

  public String getAwayColor() {
    return awayColor;
  }
}