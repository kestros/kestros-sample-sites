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

  private String layoutName = "goal-row";
  private String portrait = "";
  private String leftPct = "";
  private String color = "";

  @Override
  public String getLayout() {
    return layoutName;
  }

  /** Timeline rendering: switch layout and carry axis position + club color. */
  public SyntheticGoalCard asTimelineDot(@Nullable final String leftPct,
      @Nullable final String color) {
    this.layoutName = "goal-dot";
    this.leftPct = leftPct == null ? "" : leftPct;
    this.color = color == null ? "" : color;
    return this;
  }

  /** Scorer portrait for the goals list. */
  public SyntheticGoalCard withPortrait(final String portrait) {
    this.portrait = portrait == null ? "" : portrait;
    return this;
  }

  public String getPortrait() {
    return portrait;
  }

  public String getLeftPct() {
    return leftPct;
  }

  public String getColor() {
    return color;
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
