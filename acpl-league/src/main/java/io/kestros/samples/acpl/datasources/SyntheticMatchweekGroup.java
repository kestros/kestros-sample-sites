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
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * One matchweek's worth of results or fixtures: a "Matchweek N" heading plus a card of match rows,
 * rendered by the {@code results-group} / {@code fixtures-group} card layout. The per-row data is a
 * list of string maps that serializes into the value map, so the layout iterates it as
 * {@code data-sly-list.m="${properties.matches}"} and reads {@code m.homeName} etc. Group-level
 * {@code base} / {@code matchHref} are read as {@code properties.base} / {@code properties.matchHref}.
 */
public class SyntheticMatchweekGroup extends BaseContainerSyntheticResource implements KestrosCard {

  private final String layoutName;
  private final String matchweek;
  private final String base;
  private final String matchHref;
  private final List<Map<String, String>> matches;

  public SyntheticMatchweekGroup(@Nonnull String layoutName, @Nonnull String matchweek,
      @Nonnull String base, @Nonnull String matchHref,
      @Nonnull List<Map<String, String>> matches,
      @Nonnull BaseSlingModelDataSource dataSource,
      @Nonnull String resourcePrefix,
      @Nullable String forcedResourceName) throws ComponentConfigurationException {
    super(dataSource, resourcePrefix, forcedResourceName);
    this.layoutName = layoutName;
    this.matchweek = matchweek;
    this.base = base;
    this.matchHref = matchHref;
    this.matches = matches;
  }

  @Override
  public String getLayout() {
    return layoutName;
  }

  public String getMatchweek() {
    return matchweek;
  }

  public String getBase() {
    return base;
  }

  public String getMatchHref() {
    return matchHref;
  }

  public List<Map<String, String>> getMatches() {
    return matches;
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
    return new ArrayList<>();
  }
}
