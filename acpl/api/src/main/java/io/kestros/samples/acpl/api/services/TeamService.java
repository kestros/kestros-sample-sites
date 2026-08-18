package io.kestros.samples.acpl.api.services;

import java.util.List;
import java.util.Map;

/**
 * Display-ready team data for the team-detail page datasources. All values are pre-formatted strings —
 * datasources only wrap them into synthetic resources. {@code contextPath} is the calling component's
 * resource path; implementations derive the site root from it for asset/link paths.
 */
public interface TeamService {

  /** Header block: {@code slug}, {@code name}, {@code place} (ordinal line), {@code meta}. Empty if unknown. */
  Map<String, String> getTeamHeader(String club, String contextPath);

  /** Eight season-stat tiles: {@code value} + {@code label} each. */
  List<Map<String, String>> getTeamStatTiles(String club);

  /** Squad rows: num, name, pos, age, apps, goals, assists, portrait (path), href (player page). */
  List<Map<String, String>> getSquadRows(String club, String contextPath);

  /** Last-5 form badges: {@code result} (W/D/L), {@code badgeClass}, {@code tip}. */
  List<Map<String, String>> getFormBadges(String club);

  /** Next fixture card: oppSlug, oppName, meta (Home/Away · date · kickoff). Empty map if none. */
  Map<String, String> getNextFixtureCard(String club, String contextPath);

  /** Top scorers (max 5): name, goals, href. */
  List<Map<String, String>> getTopScorerRows(String club, String contextPath);

  /**
   * Directory card per club (all clubs, table order): slug, name, city, stadium, mgr, pos (ordinal),
   * record (W-D-L), base, href — for the /teams index grid.
   */
  List<Map<String, String>> getClubDirectoryCards(String contextPath);
}
