package io.kestros.samples.league.api.models;

import javax.annotation.Nonnull;

/**
 * Sling model interface representing a player on a league demo team.
 */
public interface Player {

  /**
   * Unique identifier for the player.
   *
   * @return player identifier.
   */
  @Nonnull
  String getId();

  /**
   * Player's first name.
   *
   * @return first name.
   */
  @Nonnull
  String getFirstName();

  /**
   * Player's last name.
   *
   * @return last name.
   */
  @Nonnull
  String getLastName();

  /**
   * Player's primary position on the team.
   *
   * @return position name.
   */
  @Nonnull
  String getPosition();

  /**
   * Jersey number assigned to the player.
   *
   * @return jersey number.
   */
  int getNumber();

  /**
   * Identifier of the team the player belongs to.
   *
   * @return team identifier.
   */
  @Nonnull
  String getTeamId();

  /**
   * Player's nationality.
   *
   * @return nationality string.
   */
  @Nonnull
  String getNationality();

  /**
   * URL to the player's headshot or profile image.
   *
   * @return image URL.
   */
  @Nonnull
  String getImageUrl();
}
