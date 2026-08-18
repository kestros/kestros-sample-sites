package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A single season-statistic tile (value + label), rendered by the {@code stat-tile} card layout.
 */
public class SyntheticStatCard extends AbstractLeagueCard {

  private final String value;
  private final String label;

  public SyntheticStatCard(@Nonnull final String value, @Nonnull final String label,
      @Nonnull final BaseSlingModelDataSource dataSource, @Nonnull final String resourcePrefix,
      @Nullable final String forcedResourceName) throws ComponentConfigurationException {
    super(dataSource, resourcePrefix, forcedResourceName);
    this.value = value;
    this.label = label;
  }

  @Override
  public String getLayout() {
    return "stat-tile";
  }

  public String getValue() {
    return value;
  }

  public String getLabel() {
    return label;
  }
}
