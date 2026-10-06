package io.kestros.samples.acpl.application.datasources;

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
 * loses Java-side child elements). On a framework version with no story layout (p3's 0.0.1) the
 * stock card renders it instead, and reads the title, image and button group back as child
 * resources; see {@link #getChildElements()}.
 */
public class SyntheticStoryCard extends BaseContainerSyntheticResource implements KestrosCard {

  private final String title;
  private final String excerpt;
  private final String image;
  private final String href;
  private final String category;
  private final String byline;
  private final BaseSlingModelDataSource cardDataSource;

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
    this.cardDataSource = dataSource;
  }

  private String layoutOverride;

  @Override
  public String getLayout() {
    return layoutOverride != null ? layoutOverride : "story-card";
  }

  /** Composition override (e.g. story-lead / story-row on the front page). */
  public SyntheticStoryCard withLayout(final String layout) {
    this.layoutOverride = layout;
    return this;
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
    return LeagueCardElements.heading(title, cardDataSource);
  }

  @Nullable
  @Override
  public String getDescription() {
    return excerpt;
  }

  @Nullable
  @Override
  public KestrosImage getImageElement() {
    return LeagueCardElements.image(image, title, href, cardDataSource);
  }

  @Nullable
  @Override
  public KestrosButtonGroup getButtonGroupElement() {
    return LeagueCardElements.buttonGroup("Read more", href, cardDataSource);
  }

  /**
   * Title, image and button group, so they become child resources of the synthetic card.
   *
   * <p>Same gap {@link AbstractLeagueCard#getChildElements()} closed for the league cards: an empty
   * list meant the stock card on a framework with no story layout showed only the excerpt and an
   * unlabelled button, with no headline or image (#253).
   */
  @Nonnull
  @Override
  public List<KestrosBasicComponentElement> getChildElements() {
    final List<KestrosBasicComponentElement> children = new ArrayList<>();
    final KestrosHeading heading = getTitleElement();
    if (heading != null) {
      children.add(heading);
    }
    final KestrosImage cardImage = getImageElement();
    if (cardImage != null) {
      children.add(cardImage);
    }
    final KestrosButtonGroup buttonGroup = getButtonGroupElement();
    if (buttonGroup != null) {
      children.add(buttonGroup);
    }
    return children;
  }
}
