package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Legend row above the mirrored stat bars: which club is which side. Rendered by the
 * {@code stat-legend} card layout.
 */
public class SyntheticStatLegendCard extends AbstractLeagueCard {

  private final String homeName;
  private final String awayName;

  public SyntheticStatLegendCard(@Nonnull final String homeName, @Nonnull final String awayName,
      @Nonnull final BaseSlingModelDataSource dataSource, @Nonnull final String resourcePrefix,
      @Nullable final String forcedResourceName) throws ComponentConfigurationException {
    super(dataSource, resourcePrefix, forcedResourceName);
    this.homeName = homeName;
    this.awayName = awayName;
  }

  @Override
  public String getLayout() {
    return "stat-legend";
  }

  public String getHomeName() {
    return homeName;
  }

  public String getAwayName() {
    return awayName;
  }
}
