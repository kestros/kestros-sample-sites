package io.kestros.samples.league.api.models;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Sling model interface representing a single league season.
 */
public interface Season {

  /**
   * Unique identifier for the season.
   *
   * @return season identifier.
   */
  @Nonnull
  String getId();

  /**
   * Display name of the season.
   *
   * @return season name.
   */
  @Nonnull
  String getName();

  /**
   * Calendar year the season begins.
   *
   * @return start year.
   */
  int getStartYear();

  /**
   * Calendar year the season ends.
   *
   * @return end year.
   */
  int getEndYear();

  /**
   * Identifiers of teams participating in the season.
   *
   * @return list of team identifiers.
   */
  @Nonnull
  List<String> getTeamIds();

  /**
   * Identifiers of matches scheduled for the season.
   *
   * @return list of match identifiers.
   */
  @Nonnull
  List<String> getMatchIds();
}
