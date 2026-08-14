package io.kestros.samples.acpl.api.services;

import java.util.List;
import java.util.Map;

/**
 * Reads the generated ACPL league JSON (bundled under {@code /data}) and exposes typed data.
 * Datasources tap this service; components resolve to those datasources.
 */
public interface LeagueDataService {

  /**
   * All clubs.
   *
   * @return a copy of the club list; mutating it does not affect the service. The club maps inside
   *     it are still the service's own — do not modify them.
   */
  List<Map<String, Object>> getClubs();

  Map<String, Object> getClub(String slug);

  /** Convenience: club display name for a slug (falls back to the slug). */
  String getClubName(String slug);

  /** Convenience: club short code for a slug. */
  String getClubShort(String slug);

  /**
   * Current-season standings, pre-sorted by position.
   *
   * @return a copy of the standings list; mutating it does not affect the service. The rows inside
   *     it are still the service's own — do not modify them.
   */
  List<Map<String, Object>> getStandings();

  /**
   * Current-season recent results, newest first.
   *
   * @return a copy of the results list; mutating it does not affect the service. The rows inside it
   *     are still the service's own — do not modify them.
   */
  List<Map<String, Object>> getRecentResults();

  /**
   * Current-season upcoming fixtures.
   *
   * @return a copy of the fixtures list; mutating it does not affect the service. The rows inside it
   *     are still the service's own — do not modify them.
   */
  List<Map<String, Object>> getUpcomingFixtures();

  /**
   * All players.
   *
   * @return a copy of the player list; mutating it does not affect the service. The player maps
   *     inside it are still the service's own — do not modify them.
   */
  List<Map<String, Object>> getPlayers();

  Map<String, Object> getPlayer(String slug);

  /** Squad for a club, ordered by shirt number. */
  List<Map<String, Object>> getSquad(String clubSlug);

  /** Standings row for a single club (empty map if not found). */
  Map<String, Object> getStanding(String clubSlug);

  /** Top scorers for a club, ordered by season goals desc, capped at {@code limit}. */
  List<Map<String, Object>> getTopScorers(String clubSlug, int limit);

  /** Last-N results for a club, oldest→newest, each with a {@code result} of W/D/L. */
  List<Map<String, Object>> getClubForm(String clubSlug, int limit);

  /** The club's next unplayed fixture (empty map if none). */
  Map<String, Object> getNextFixture(String clubSlug);

  /** Current-season match by its numeric {@code id} (empty map if not found). */
  Map<String, Object> getMatch(String matchId);

  /** All current-season played matches, ordered by matchweek then date (ascending, matchweek 1 first). */
  List<Map<String, Object>> getPlayedMatches();

  /**
   * All stories.
   *
   * @return a copy of the story list; mutating it does not affect the service. The story maps inside
   *     it are still the service's own — do not modify them.
   */
  List<Map<String, Object>> getStories();

  /**
   * The featured match.
   *
   * @return a copy of the featured-match map; mutating it does not affect the service. Its nested
   *     values are still the service's own — do not modify them.
   */
  Map<String, Object> getFeaturedMatch();

  /**
   * Played meetings between two clubs across ALL seasons, newest first, up to {@code limit}.
   *
   * @return fully independent rows, each stamped with its {@code season}. Neither the list nor
   *     anything nested inside a row is shared with the service.
   */
  List<Map<String, Object>> getMeetings(String clubA, String clubB, int limit);
}
