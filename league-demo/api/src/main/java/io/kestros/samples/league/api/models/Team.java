package io.kestros.samples.league.api.models;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Sling model interface representing a sports team in the league demo.
 */
public interface Team {

  /**
   * Unique identifier for the team.
   *
   * @return team identifier.
   */
  @Nonnull
  String getId();

  /**
   * Full display name of the team.
   *
   * @return team name.
   */
  @Nonnull
  String getName();

  /**
   * Short form name (typically used for compact displays).
   *
   * @return short team name.
   */
  @Nonnull
  String getShortName();

  /**
   * URL to the team logo image.
   *
   * @return logo URL.
   */
  @Nonnull
  String getLogoUrl();

  /**
   * City the team is based in.
   *
   * @return city name.
   */
  @Nonnull
  String getCity();

  /**
   * Stadium or home venue for the team.
   *
   * @return stadium name.
   */
  @Nonnull
  String getStadium();

  /**
   * Year the team was founded.
   *
   * @return founding year.
   */
  int getFounded();

  /**
   * Identifiers for all players associated with the team.
   *
   * @return list of player identifiers.
   */
  @Nonnull
  List<String> getPlayerIds();
}
