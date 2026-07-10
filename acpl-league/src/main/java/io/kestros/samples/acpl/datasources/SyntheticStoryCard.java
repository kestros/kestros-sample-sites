package io.kestros.samples.acpl.datasources;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.content.KestrosButtonGroup;
import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.cms.components.basic.api.content.KestrosHeading;
import io.kestros.cms.components.basic.api.content.KestrosImage;
import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.core.BaseContainerSyntheticResource;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A news "story" card rendered through the {@code story-card} card layout. Title / excerpt / image /
 * href / category are plain strings that serialize into the synthetic resource value map, so the
 * layout reads them as {@code properties.title} etc. (avoiding the data-source re-adaptation that
 * loses Java-side child elements). The {@link KestrosCard} element getters are unused by that layout
 * and return {@code null}.
 */
public class SyntheticStoryCard extends BaseContainerSyntheticResource implements KestrosCard {

  private final String title;
  private final String excerpt;
  private final String image;
  private final String href;
  private final String category;
  private final String byline;

  public SyntheticStoryCard(@Nonnull String title, @Nonnull String excerpt, @Nonnull String image,
      @Nonnull String href, @Nonnull String category, @Nonnull String byline,
      @Nonnull BaseSlingModelDataSource dataSource,
      @Nonnull String resourcePrefix,
      @Nullable String forcedResourceName) throws ComponentConfigurationException {
    super(dataSource, resourcePrefix, forcedResourceName);
    this.title = title;
    this.excerpt = excerpt;
    this.image = image;
    this.href = href;
    this.category = category;
    this.byline = byline;
  }

  @Override
  public String getLayout() {
    return "story-card";
  }

  public String getHeadline() {
    return title;
  }

  public String getExcerpt() {
    return excerpt;
  }

  public String getImageSrc() {
    return image;
  }

  public String getHref() {
    return href;
  }

  public String getCategory() {
    return category;
  }

  public String getByline() {
    return byline;
  }

  @Nullable
  @Override
  public KestrosHeading getTitleElement() {
    return null;
  }

  @Nullable
  @Override
  public String getDescription() {
    return excerpt;
  }

  @Nullable
  @Override
  public KestrosImage getImageElement() {
    return null;
  }

  @Nullable
  @Override
  public KestrosButtonGroup getButtonGroupElement() {
    return null;
  }

  @Nonnull
  @Override
  public List<KestrosBasicComponentElement> getChildElements() {
    return new ArrayList<>();
  }
}
