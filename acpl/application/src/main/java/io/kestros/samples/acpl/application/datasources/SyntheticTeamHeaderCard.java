package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Header card for a team-detail page. Serializes crest slug, club name, standings-position line and
 * stadium/manager/founded line into the value map for the {@code team-header} card layout.
 */
public class SyntheticTeamHeaderCard extends AbstractLeagueCard {

  private final String slug;
  private final String name;
  private final String place;
  private final String meta;
  private final String base;

  public SyntheticTeamHeaderCard(@Nonnull final String slug, @Nonnull final String name,
      @Nonnull final String place, @Nonnull final String meta, @Nonnull final String base,
      @Nonnull final BaseSlingModelDataSource dataSource, @Nonnull final String resourcePrefix,
      @Nullable final String forcedResourceName) throws ComponentConfigurationException {
    super(dataSource, resourcePrefix, forcedResourceName);
    this.slug = slug;
    this.name = name;
    this.place = place;
    this.meta = meta;
    this.base = base;
  }

  @Override
  public String getLayout() {
    return "team-header";
  }

  public String getSlug() {
    return slug;
  }

  public String getClubName() {
    return name;
  }

  public String getPlace() {
    return place;
  }

  public String getMeta() {
    return meta;
  }

  public String getBase() {
    return base;
  }
}
