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
 * Base for the ACPL team-detail synthetic cards. Concrete subclasses supply a {@link #getLayout()}
 * name and plain-string getters that serialize into the synthetic resource value map (read as
 * {@code properties.*} by the matching card layout).
 *
 * <p>A UI framework that ships no card layout of its own falls through to the stock card layout,
 * which renders the {@link KestrosCard} element getters rather than {@code properties.*}. Subclasses
 * therefore say what their title, image and link are through {@link #getCardTitleText()},
 * {@link #getCardImageSrc()} and {@link #getCardHref()}, and those elements are built here.
 */
public abstract class AbstractLeagueCard extends BaseContainerSyntheticResource
    implements KestrosCard {

  private final BaseSlingModelDataSource cardDataSource;

  protected AbstractLeagueCard(@Nonnull final BaseSlingModelDataSource dataSource,
      @Nonnull final String resourcePrefix, @Nullable final String forcedResourceName)
      throws ComponentConfigurationException {
    super(dataSource, resourcePrefix, forcedResourceName);
    this.cardDataSource = dataSource;
  }

  /**
   * Title the stock card layout renders. Null means the card has no heading.
   *
   * @return Title text or null.
   */
  @Nullable
  protected String getCardTitleText() {
    return null;
  }

  /**
   * Image path the stock card layout renders. Null means the card has no image.
   *
   * @return Image path or null.
   */
  @Nullable
  protected String getCardImageSrc() {
    return null;
  }

  /**
   * Target the card's button points at. Null means the card has no button.
   *
   * @return Href or null.
   */
  @Nullable
  protected String getCardHref() {
    return null;
  }

  /**
   * Text of the card's button.
   *
   * @return Button text.
   */
  @Nullable
  protected String getCardLinkText() {
    return "Read more";
  }

  @Nullable
  @Override
  public KestrosHeading getTitleElement() {
    return LeagueCardElements.heading(getCardTitleText(), cardDataSource);
  }

  @Nullable
  @Override
  public String getDescription() {
    return null;
  }

  @Nullable
  @Override
  public KestrosImage getImageElement() {
    return LeagueCardElements.image(getCardImageSrc(), getCardTitleText(), getCardHref(),
        cardDataSource);
  }

  @Nullable
  @Override
  public KestrosButtonGroup getButtonGroupElement() {
    return LeagueCardElements.buttonGroup(getCardLinkText(), getCardHref(), cardDataSource);
  }

  @Nonnull
  @Override
  public List<KestrosBasicComponentElement> getChildElements() {
    return new ArrayList<>();
  }
}
