package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Player-detail header card (portrait, name, club, shirt-number and position badges, meta line),
 * rendered by the {@code player-header} card layout.
 */
public class SyntheticPlayerHeaderCard extends AbstractLeagueCard {

  private final String name;
  private final String club;
  private final String number;
  private final String pos;
  private final String meta;
  private final String photo;
  private final String base;
  private String clubColor = "";

  private final String clubHref;

  public SyntheticPlayerHeaderCard(@Nonnull final String name, @Nonnull final String club,
      @Nullable final String clubHref, @Nonnull final String number, @Nonnull final String pos,
      @Nonnull final String meta, @Nonnull final String photo, @Nullable final String base,
      @Nonnull final BaseSlingModelDataSource dataSource,
      @Nonnull final String resourcePrefix, @Nullable final String forcedResourceName)
      throws ComponentConfigurationException {
    super(dataSource, resourcePrefix, forcedResourceName);
    this.name = name;
    this.club = club;
    this.clubHref = clubHref == null ? "" : clubHref;
    this.base = base == null ? "" : base;
    this.number = number;
    this.pos = pos;
    this.meta = meta;
    this.photo = photo;
  }

  @Override
  public String getLayout() {
    return "player-header";
  }

  public String getPlayerName() {
    return name;
  }

  public String getClubHref() {
    return clubHref;
  }

  public String getClubName() {
    return club;
  }

  public String getNumber() {
    return number;
  }

  public String getPos() {
    return pos;
  }

  public String getMeta() {
    return meta;
  }

  public String getBase() {
    return base;
  }

  public String getPhoto() {
    return photo;
  }

  /** Contrast-gated club brand color for the hero keyline. */
  public SyntheticPlayerHeaderCard withClubColor(final String clubColor) {
    this.clubColor = clubColor == null ? "" : clubColor;
    return this;
  }

  public String getClubColor() {
    return clubColor;
  }
}