package io.kestros.samples.acpl.services;

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

  List<Map<String, Object>> getStories();

  Map<String, Object> getFeaturedMatch();
}
