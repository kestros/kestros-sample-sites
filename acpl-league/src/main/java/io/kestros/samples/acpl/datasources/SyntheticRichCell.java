package io.kestros.samples.acpl.datasources;

import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A table cell whose render layout is chosen in Java (via {@link #getLayout()}) rather than through a
 * table-node {@code <prefix>Layout} property, and which carries href / image / label / base strings
 * for the richer table-cell layouts (portrait, playerlink, badge, numbold). All values serialize into
 * the synthetic resource's value map, so the layout reads them as {@code properties.href} /
 * {@code properties.imageSrc} / {@code properties.label} / {@code properties.base} (and the plain
 * {@code tableCell.text} for text-based layouts).
 */
public class SyntheticRichCell extends SyntheticTableCell {

  private final String layoutName;
  private final String href;
  private final String imageSrc;
  private final String label;
  private final String base;

  public SyntheticRichCell(@Nonnull String text, @Nonnull String layoutName,
      @Nullable String href, @Nullable String imageSrc, @Nullable String label,
      @Nullable String base,
      @Nonnull BaseSlingModelDataSource dataSource,
      @Nonnull String resourcePrefix,
      @Nullable String forcedResourceName) throws ComponentConfigurationException {
    super(text, dataSource, resourcePrefix, forcedResourceName);
    this.layoutName = layoutName;
    this.href = href;
    this.imageSrc = imageSrc;
    this.label = label;
    this.base = base;
  }

  @Override
  public String getLayout() {
    return layoutName;
  }

  public String getHref() {
    return href;
  }

  public String getImageSrc() {
    return imageSrc;
  }

  public String getLabel() {
    return label;
  }

  public String getBase() {
    return base;
  }
}
