package io.kestros.samples.acpl.api.services;

import java.util.List;
import java.util.Map;

/**
 * Display-ready match data for the match-detail page datasources. An empty {@code matchId} falls back
 * to the featured match.
 */
public interface MatchService {

  /** Header: homeSlug, homeName, awaySlug, awayName, score, meta (matchweek · date · venue). */
  Map<String, String> getMatchHeader(String matchId, String contextPath);

  /** Goal rows: minute, scorer, href (player page or empty), teamSlug, assist. */
  List<Map<String, String>> getGoalRows(String matchId, String contextPath);

  /** Stat rows: h, label, a. */
  List<Map<String, String>> getMatchStatRows(String matchId);

  /**
   * Fixture-preview info for an unplayed match: date, kickoff, venue, referee, homeName/awayName,
   * homePos/awayPos (ordinal standings), homeForm/awayForm ("W W D W L"), and previewHref when an
   * editorial preview story exists. Empty if the match is unknown.
   */
  Map<String, String> getPreviewInfo(String matchId, String contextPath);

  /**
   * Past meetings between the two clubs of a match (all seasons, newest first, max 5) in
   * result-row shape: homeSlug/homeShort/homeName, awaySlug/…, mid (score), href, base, plus
   * "season" for context.
   */
  List<Map<String, String>> getHeadToHeadRows(String matchId, String contextPath);
}
