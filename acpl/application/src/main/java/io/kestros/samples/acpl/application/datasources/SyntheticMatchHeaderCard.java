package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Match-detail header card (home/away crests + names, scoreline, matchweek/date/venue meta), rendered
 * by the {@code match-header} card layout.
 */
public class SyntheticMatchHeaderCard extends AbstractLeagueCard {

  private final String homeSlug;
  private final String homeName;
  private final String awaySlug;
  private final String awayName;
  private final String score;
  private final String meta;
  private final String base;
  private String reportHref = "";

  public SyntheticMatchHeaderCard(@Nonnull final String homeSlug, @Nonnull final String homeName,
      @Nonnull final String awaySlug, @Nonnull final String awayName, @Nonnull final String score,
      @Nonnull final String meta, @Nonnull final String base,
      @Nonnull final BaseSlingModelDataSource dataSource, @Nonnull final String resourcePrefix,
      @Nullable final String forcedResourceName) throws ComponentConfigurationException {
    super(dataSource, resourcePrefix, forcedResourceName);
    this.homeSlug = homeSlug;
    this.homeName = homeName;
    this.awaySlug = awaySlug;
    this.awayName = awayName;
    this.score = score;
    this.meta = meta;
    this.base = base;
  }

  @Override
  public String getLayout() {
    return "match-header";
  }

  public String getHomeSlug() {
    return homeSlug;
  }

  public String getHomeName() {
    return homeName;
  }

  public String getAwaySlug() {
    return awaySlug;
  }

  public String getAwayName() {
    return awayName;
  }

  public String getScore() {
    return score;
  }

  public String getMeta() {
    return meta;
  }

  public String getBase() {
    return base;
  }

  /** Optional link to the editorial match report, when one exists. */
  public SyntheticMatchHeaderCard withReportHref(@Nullable final String reportHref) {
    this.reportHref = reportHref == null ? "" : reportHref;
    return this;
  }

  public String getReportHref() {
    return reportHref;
  }
}