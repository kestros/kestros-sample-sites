package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A single match goal (minute, scorer link, scoring-team crest, assist), rendered by the
 * {@code goal-row} card layout.
 */
public class SyntheticGoalCard extends AbstractLeagueCard {

  private final String minute;
  private final String scorer;
  private final String href;
  private final String teamSlug;
  private final String assist;
  private final String base;
  private final String side;

  public SyntheticGoalCard(@Nonnull final String minute, @Nonnull final String scorer,
      @Nonnull final String href, @Nonnull final String teamSlug, @Nonnull final String assist,
      @Nonnull final String base, @Nonnull final String side,
      @Nonnull final BaseSlingModelDataSource dataSource,
      @Nonnull final String resourcePrefix, @Nullable final String forcedResourceName)
      throws ComponentConfigurationException {
    super(dataSource, resourcePrefix, forcedResourceName);
    this.minute = minute;
    this.scorer = scorer;
    this.href = href;
    this.teamSlug = teamSlug;
    this.assist = assist;
    this.base = base;
    this.side = side;
  }

  @Override
  public String getLayout() {
    return "goal-row";
  }

  public String getMinute() {
    return minute;
  }

  public String getScorer() {
    return scorer;
  }

  public String getHref() {
    return href;
  }

  public String getTeamSlug() {
    return teamSlug;
  }

  public String getAssist() {
    return assist;
  }

  public String getBase() {
    return base;
  }

  public String getSide() {
    return side;
  }
}
