package io.kestros.samples.acpl.datasources;

import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A standings "club" cell rendered through the {@code club} table-cell layout: a linked crest image
 * plus the club's abbreviation as a link. The slug / short / base values are plain strings so they
 * survive serialization into the synthetic resource's value map (the same path the cell text uses)
 * and can be read by the layout as {@code properties.slug} / {@code properties.shortName} /
 * {@code properties.base}.
 */
public class SyntheticClubCell extends SyntheticTableCell {

  private final String slug;
  private final String shortName;
  private final String base;

  public SyntheticClubCell(@Nonnull String slug, @Nonnull String shortName, @Nonnull String base,
      @Nonnull BaseSlingModelDataSource dataSource,
      @Nonnull String resourcePrefix,
      @Nullable String forcedResourceName) throws ComponentConfigurationException {
    super("", dataSource, resourcePrefix, forcedResourceName);
    this.slug = slug;
    this.shortName = shortName;
    this.base = base;
  }

  public String getSlug() {
    return slug;
  }

  public String getShortName() {
    return shortName;
  }

  public String getBase() {
    return base;
  }
}
