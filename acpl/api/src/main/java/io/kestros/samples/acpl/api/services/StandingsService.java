package io.kestros.samples.acpl.api.services;

import java.util.List;
import java.util.Map;

/** Display-ready standings data for the standings table datasource. */
public interface StandingsService {

  /**
   * Standings rows (raw column values), position-ordered, optionally trimmed to the top
   * {@code limit}.
   *
   * @return a copy, never a view onto the underlying standings; mutating it does not affect the
   *     service. The rows inside it are still the service's own — do not modify them.
   */
  List<Map<String, Object>> getStandingsRows(int limit);

  /** Standings rows for the given club slugs only (position order) — mini-table slices. */
  List<Map<String, Object>> getStandingsRowsForClubs(List<String> clubs);

  /** Zone for a position: "qualify" (top 3), "relegation" (bottom 2), or "" — for row styling. */
  String getZone(int pos);

  /**
   * Club cell data: slug, label, linkClass, base (site root from contextPath).
   *
   * <p>The label is the full club name for "name" and "rich-name", and the abbreviation for
   * anything else. A "rich-*" mode additionally tells the datasource to build a cell for the
   * {@code club} table-cell layout, which exists from ACPL Framework 0.0.2 on; a page on 0.0.1 asks
   * for plain "name" so the label renders as ordinary cell text.
   */
  Map<String, String> getClubCell(String slug, String displayMode, String contextPath);
}
