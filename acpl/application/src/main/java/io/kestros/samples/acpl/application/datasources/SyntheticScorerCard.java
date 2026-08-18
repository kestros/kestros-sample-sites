package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A single top-scorer row (player name, season goals, and a link to the player page), rendered by the
 * {@code scorer-row} card layout.
 */
public class SyntheticScorerCard extends AbstractLeagueCard {

  private final String name;
  private final String goals;
  private final String href;

  public SyntheticScorerCard(@Nonnull final String name, @Nonnull final String goals,
      @Nonnull final String href, @Nonnull final BaseSlingModelDataSource dataSource,
      @Nonnull final String resourcePrefix, @Nullable final String forcedResourceName)
      throws ComponentConfigurationException {
    super(dataSource, resourcePrefix, forcedResourceName);
    this.name = name;
    this.goals = goals;
    this.href = href;
  }

  @Override
  public String getLayout() {
    return "scorer-row";
  }

  public String getPlayerName() {
    return name;
  }

  public String getGoals() {
    return goals;
  }

  public String getHref() {
    return href;
  }
}
