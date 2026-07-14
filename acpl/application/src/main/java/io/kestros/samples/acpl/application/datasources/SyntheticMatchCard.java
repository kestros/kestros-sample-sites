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
 * A single match row (result or fixture), rendered through the {@code result-row} / {@code fixture-row}
 * card layout. Home/away slug, short code, full name, the middle box (score or kickoff) and links are
 * plain strings serialized into the synthetic resource value map, read by the layout as
 * {@code properties.homeSlug} etc. {@link KestrosCard} element getters are unused and return null.
 */
public class SyntheticMatchCard extends BaseContainerSyntheticResource implements KestrosCard {

  private final String layoutName;
  private final String base;
  private final String matchHref;
  private final String homeSlug;
  private final String homeShort;
  private final String homeName;
  private final String awaySlug;
  private final String awayShort;
  private final String awayName;
  private final String mid;
  private String subText = "";
  private String homeGoals = "";
  private String awayGoals = "";

  public SyntheticMatchCard(@Nonnull String layoutName, @Nonnull String base,
      @Nonnull String matchHref, @Nonnull String homeSlug, @Nonnull String homeShort,
      @Nonnull String homeName, @Nonnull String awaySlug, @Nonnull String awayShort,
      @Nonnull String awayName, @Nonnull String mid,
      @Nonnull BaseSlingModelDataSource dataSource,
      @Nonnull String resourcePrefix,
      @Nullable String forcedResourceName) throws ComponentConfigurationException {
    super(dataSource, resourcePrefix, forcedResourceName);
    this.layoutName = layoutName;
    this.base = base;
    this.matchHref = matchHref;
    this.homeSlug = homeSlug;
    this.homeShort = homeShort;
    this.homeName = homeName;
    this.awaySlug = awaySlug;
    this.awayShort = awayShort;
    this.awayName = awayName;
    this.mid = mid;
  }

  @Override
  public String getLayout() {
    return layoutName;
  }

  public String getBase() {
    return base;
  }

  public String getMatchHref() {
    return matchHref;
  }

  public String getHomeSlug() {
    return homeSlug;
  }

  public String getHomeShort() {
    return homeShort;
  }

  public String getHomeName() {
    return homeName;
  }

  public String getAwaySlug() {
    return awaySlug;
  }

  public String getAwayShort() {
    return awayShort;
  }

  public String getAwayName() {
    return awayName;
  }

  public String getMid() {
    return mid;
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
  /** Optional context line under the score box (e.g. "MW8 · 2025-26" on head-to-head rows). */
  public SyntheticMatchCard withSubText(@Nullable final String subText) {
    this.subText = subText == null ? "" : subText;
    return this;
  }

  public String getSubText() {
    return subText;
  }

  /** Per-side goals for chip-style rendering. */
  public SyntheticMatchCard withGoals(final String hg, final String ag) {
    this.homeGoals = hg == null ? "" : hg;
    this.awayGoals = ag == null ? "" : ag;
    return this;
  }

  public String getHomeGoals() {
    return homeGoals;
  }

  public String getAwayGoals() {
    return awayGoals;
  }
}