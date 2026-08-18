package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.content.KestrosButtonGroup;
import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.cms.components.basic.api.content.KestrosHeading;
import io.kestros.cms.components.basic.api.content.KestrosImage;
import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.core.BaseContainerSyntheticResource;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Pre-match info block for an unplayed match, rendered by the {@code match-preview-info} card
 * layout. Backed directly by the display-ready map from
 * {@code MatchService.getPreviewInfo} — every map entry is exposed as a value-map property.
 */
public class SyntheticMatchPreviewCard extends BaseContainerSyntheticResource implements KestrosCard {

  private final Map<String, String> info;

  public SyntheticMatchPreviewCard(@Nonnull final Map<String, String> info,
      @Nonnull final BaseSlingModelDataSource dataSource, @Nonnull final String resourcePrefix,
      @Nullable final String forcedResourceName) throws ComponentConfigurationException {
    super(dataSource, resourcePrefix, forcedResourceName);
    this.info = info;
  }

  @Override
  public String getLayout() {
    return "match-preview-info";
  }

  public String getBase() {
    return info.getOrDefault("base", "");
  }

  public String getHomeSlug() {
    return info.getOrDefault("homeSlug", "");
  }

  public String getAwaySlug() {
    return info.getOrDefault("awaySlug", "");
  }

  public String getDate() {
    return info.getOrDefault("date", "");
  }

  public String getKickoff() {
    return info.getOrDefault("kickoff", "");
  }

  public String getVenue() {
    return info.getOrDefault("venue", "");
  }

  public String getReferee() {
    return info.getOrDefault("referee", "");
  }

  public String getHomeName() {
    return info.getOrDefault("homeName", "");
  }

  public String getAwayName() {
    return info.getOrDefault("awayName", "");
  }

  public String getHomePos() {
    return info.getOrDefault("homePos", "");
  }

  public String getAwayPos() {
    return info.getOrDefault("awayPos", "");
  }

  public String getHomeForm() {
    return info.getOrDefault("homeForm", "");
  }

  public String getAwayForm() {
    return info.getOrDefault("awayForm", "");
  }

  public String getPreviewHref() {
    return info.getOrDefault("previewHref", "");
  }

  @Nullable
  @Override
  public KestrosHeading getTitleElement() {
    return null;
  }

  @Nullable
  @Override
  public String getDescription() {
    return null;
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
    return Collections.emptyList();
  }
}
