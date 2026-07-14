package io.kestros.samples.acpl.api.services;

import java.util.List;
import java.util.Map;

/** Display-ready results/fixtures data for the schedule + results datasources and the match filter. */
public interface ScheduleService {

  /**
   * Played matches grouped by matchweek, ascending. Each group: {@code matchweek} ("Matchweek N") +
   * {@code matches} (list of row maps: homeSlug/homeName/homeRec, awaySlug/awayName/awayRec, href,
   * score, date).
   */
  List<Map<String, Object>> getResultsByMatchweek(String contextPath);

  /** Upcoming fixtures grouped by matchweek; rows carry {@code time} instead of score/date. */
  List<Map<String, Object>> getFixturesByMatchweek(String contextPath);

  /** Recent-result rows for the home widget: home/homeShort/homeName, away/…, mid (score), href. */
  List<Map<String, String>> getRecentResultRows(String contextPath, int limit);

  /** Upcoming-fixture rows for the home widget; mid = "day kickoff". */
  List<Map<String, String>> getUpcomingFixtureRows(String contextPath, int limit);

  /** Matchweek dropdown options for a filter: played weeks for "results", upcoming for "fixtures". */
  List<String> getMatchweekOptions(String mode);

  /** Club dropdown options: slug + name. */
  List<Map<String, String>> getClubOptions();
}
