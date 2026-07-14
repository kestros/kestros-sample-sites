package io.kestros.samples.acpl.api.services;

import java.util.List;
import java.util.Map;

/**
 * Reads the generated ACPL league JSON (bundled under {@code /data}) and exposes typed data.
 * Datasources tap this service; components resolve to those datasources.
 */
public interface LeagueDataService {

  List<Map<String, Object>> getClubs();

  Map<String, Object> getClub(String slug);

  /** Convenience: club display name for a slug (falls back to the slug). */
  String getClubName(String slug);

  /** Convenience: club short code for a slug. */
  String getClubShort(String slug);

  /** Current-season standings, pre-sorted by position. */
  List<Map<String, Object>> getStandings();

  /** Current-season recent results, newest first. */
  List<Map<String, Object>> getRecentResults();

  /** Current-season upcoming fixtures. */
  List<Map<String, Object>> getUpcomingFixtures();

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

  List<Map<String, Object>> getStories();

  Map<String, Object> getFeaturedMatch();

  /** Played meetings between two clubs across ALL seasons, newest first, up to {@code limit}. */
  List<Map<String, Object>> getMeetings(String clubA, String clubB, int limit);
}
