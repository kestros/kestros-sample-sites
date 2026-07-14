package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Next-fixture card (opponent crest slug, opponent name, and a Home/Away · date · kickoff meta line),
 * rendered by the {@code next-fixture} card layout.
 */
public class SyntheticFixtureCard extends AbstractLeagueCard {

  private final String oppSlug;
  private final String oppName;
  private final String meta;
  private final String base;
  private final String href;

  public SyntheticFixtureCard(@Nonnull final String oppSlug, @Nonnull final String oppName,
      @Nonnull final String meta, @Nonnull final String base, @Nullable final String href,
      @Nonnull final BaseSlingModelDataSource dataSource, @Nonnull final String resourcePrefix,
      @Nullable final String forcedResourceName) throws ComponentConfigurationException {
    super(dataSource, resourcePrefix, forcedResourceName);
    this.oppSlug = oppSlug;
    this.oppName = oppName;
    this.meta = meta;
    this.base = base;
    this.href = href == null ? "" : href;
  }

  @Override
  public String getLayout() {
    return "next-fixture";
  }

  public String getOppSlug() {
    return oppSlug;
  }

  public String getOppName() {
    return oppName;
  }

  public String getMeta() {
    return meta;
  }

  public String getBase() {
    return base;
  }

  public String getHref() {
    return href;
  }
}
