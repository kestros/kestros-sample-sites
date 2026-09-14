package io.kestros.samples.league.api.models;

import javax.annotation.Nonnull;

/**
 * Sling model interface representing a scheduled or played match between two teams.
 */
public interface Match {

  /**
   * Unique identifier for the match.
   *
   * @return match identifier.
   */
  @Nonnull
  String getId();

  /**
   * Identifier of the home team.
   *
   * @return home team identifier.
   */
  @Nonnull
  String getHomeTeamId();

  /**
   * Identifier of the away team.
   *
   * @return away team identifier.
   */
  @Nonnull
  String getAwayTeamId();

  /**
   * Score for the home team.
   *
   * @return home score.
   */
  int getHomeScore();

  /**
   * Score for the away team.
   *
   * @return away score.
   */
  int getAwayScore();

  /**
   * Matchday (round) number within the season.
   *
   * @return matchday number.
   */
  int getMatchday();

  /**
   * Identifier of the season the match belongs to.
   *
   * @return season identifier.
   */
  @Nonnull
  String getSeasonId();

  /**
   * Date the match is scheduled for or was played on.
   *
   * @return date string.
   */
  @Nonnull
  String getDate();

  /**
   * Venue where the match is held.
   *
   * @return venue name.
   */
  @Nonnull
  String getVenue();

  /**
   * Whether the match has been played.
   *
   * @return {@code true} if the match has been played.
   */
  boolean isPlayed();
}
