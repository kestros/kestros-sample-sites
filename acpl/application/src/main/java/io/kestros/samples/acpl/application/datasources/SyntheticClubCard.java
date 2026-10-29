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
 * A club-directory card for the /teams index, rendered by the {@code club-card} card layout. All
 * values are display-ready strings serialized into the synthetic resource value map
 * ({@code properties.clubName} etc.). The title and button-group elements are for frameworks with
 * no club-card layout, which fall through to the stock card layout.
 */
public class SyntheticClubCard extends BaseContainerSyntheticResource implements KestrosCard {

  private final String slug;
  private final String clubName;
  private final String city;
  private final String stadium;
  private final String mgr;
  private final String pos;
  private final String record;
  private final String base;
  private final String href;
  private final BaseSlingModelDataSource cardDataSource;

  public SyntheticClubCard(@Nonnull final String slug, @Nonnull final String clubName,
      @Nonnull final String city, @Nonnull final String stadium, @Nonnull final String mgr,
      @Nonnull final String pos, @Nonnull final String record, @Nonnull final String base,
      @Nonnull final String href, @Nonnull final BaseSlingModelDataSource dataSource,
      @Nonnull final String resourcePrefix, @Nullable final String forcedResourceName)
      throws ComponentConfigurationException {
    super(dataSource, resourcePrefix, forcedResourceName);
    this.slug = slug;
    this.clubName = clubName;
    this.city = city;
    this.stadium = stadium;
    this.mgr = mgr;
    this.pos = pos;
    this.record = record;
    this.base = base;
    this.href = href;
    this.cardDataSource = dataSource;
  }

  @Override
  public String getLayout() {
    return "club-card";
  }

  public String getSlug() {
    return slug;
  }

  public String getClubName() {
    return clubName;
  }

  public String getCity() {
    return city;
  }

  public String getStadium() {
    return stadium;
  }

  public String getMgr() {
    return mgr;
  }

  public String getPos() {
    return pos;
  }

  public String getRecord() {
    return record;
  }

  public String getBase() {
    return base;
  }

  public String getHref() {
    return href;
  }

  @Nullable
  @Override
  public KestrosHeading getTitleElement() {
    return LeagueCardElements.heading(clubName, cardDataSource);
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
    return LeagueCardElements.buttonGroup("Club page", href, cardDataSource);
  }

  @Nonnull
  @Override
  public List<KestrosBasicComponentElement> getChildElements() {
    // The stock card layout - all a framework without a club-card layout has, such as ACPL
    // Framework 0.0.1 on p3 - reads the title and button group back as child resources. With no
    // children every club rendered as an empty card-body (#1262).
    final List<KestrosBasicComponentElement> children = new ArrayList<>();
    final KestrosHeading title = getTitleElement();
    if (title != null) {
      children.add(title);
    }
    final KestrosButtonGroup buttonGroup = getButtonGroupElement();
    if (buttonGroup != null) {
      children.add(buttonGroup);
    }
    return children;
  }
}
