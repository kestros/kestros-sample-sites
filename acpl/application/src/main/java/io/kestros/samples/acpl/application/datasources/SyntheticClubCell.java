package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A standings "club" cell rendered through the {@code club} table-cell layout: a linked crest image
 * plus a club link. The label (abbreviation for the compact widget, full name for the full table)
 * and its link class are plain strings so they survive serialization into the synthetic resource's
 * value map (the same path the cell text uses) and can be read by the layout as
 * {@code properties.label} / {@code properties.linkClass} / {@code properties.slug} /
 * {@code properties.base}.
 */
public class SyntheticClubCell extends SyntheticTableCell {

  private final String slug;
  private final String label;
  private final String linkClass;
  private final String base;

  public SyntheticClubCell(@Nonnull String slug, @Nonnull String label, @Nonnull String linkClass,
      @Nonnull String base,
      @Nonnull BaseSlingModelDataSource dataSource,
      @Nonnull String resourcePrefix,
      @Nullable String forcedResourceName) throws ComponentConfigurationException {
    // The label doubles as the cell text: a framework with no club layout (ACPL Framework 0.0.1 on
    // p3) renders the stock cell, which prints only the text - an empty club column before #1262.
    super(label == null ? "" : label, dataSource, resourcePrefix, forcedResourceName);
    this.slug = slug;
    this.label = label;
    this.linkClass = linkClass;
    this.base = base;
  }

  public String getSlug() {
    return slug;
  }

  public String getLabel() {
    return label;
  }

  public String getLinkClass() {
    return linkClass;
  }

  public String getBase() {
    return base;
  }
}
