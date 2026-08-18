package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A single recent-form result badge (W/D/L + Bootstrap badge class), rendered by the
 * {@code form-badge} card layout. {@code title} carries the opponent and score for a hover tooltip.
 */
public class SyntheticFormCard extends AbstractLeagueCard {

  private final String result;
  private final String badgeClass;
  private final String title;

  public SyntheticFormCard(@Nonnull final String result, @Nonnull final String badgeClass,
      @Nonnull final String title, @Nonnull final BaseSlingModelDataSource dataSource,
      @Nonnull final String resourcePrefix, @Nullable final String forcedResourceName)
      throws ComponentConfigurationException {
    super(dataSource, resourcePrefix, forcedResourceName);
    this.result = result;
    this.badgeClass = badgeClass;
    this.title = title;
  }

  @Override
  public String getLayout() {
    return "form-badge";
  }

  public String getResult() {
    return result;
  }

  public String getBadgeClass() {
    return badgeClass;
  }

  public String getTip() {
    return title;
  }
}
