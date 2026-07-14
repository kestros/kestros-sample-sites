package io.kestros.samples.acpl.api.services;

import java.util.List;
import java.util.Map;

/** Display-ready player data for the player-detail page datasources. */
public interface PlayerService {

  /** Header block: name, club (full name), number (#N), pos, meta line, photo (portrait path). */
  Map<String, String> getPlayerHeader(String slug, String contextPath);

  /** Six season-stat tiles: value + label each. */
  List<Map<String, String>> getPlayerStatTiles(String slug);

  /**
   * Last-5 rows, newest first: mw, opp (full club name), oppSlug, venue, res (e.g. "W 3–1"),
   * g, a, rating, href (match page), base.
   */
  List<Map<String, String>> getLast5Rows(String slug, String contextPath);

  /**
   * League leaders for a stat ("goals" or "assists"), up to {@code limit}: name, club (full name),
   * clubSlug, value, href (player page), clubHref, base.
   */
  List<Map<String, String>> getLeagueLeaders(String stat, int limit, String contextPath);
}
