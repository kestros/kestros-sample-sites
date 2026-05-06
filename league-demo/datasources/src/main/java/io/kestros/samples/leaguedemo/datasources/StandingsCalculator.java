package io.kestros.samples.leaguedemo.datasources;

import io.kestros.samples.league.api.models.Match;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shared helper for computing the league table from played matches. Used by both the
 * full-table and top-five standings datasources so they always rank the same way and
 * can be enriched once.
 *
 * <p>Tiebreakers follow the order the league publishes on /standings.html: points,
 * then goal difference, then goals scored, then wins.
 */
final class StandingsCalculator {

  private StandingsCalculator() {}

  /** Per-team standings entry. Mutable while accumulating from match results. */
  static final class Standing {
    final String teamId;
    int played;
    int won;
    int drawn;
    int lost;
    int goalsFor;
    int goalsAgainst;

    Standing(String teamId) {
      this.teamId = teamId;
    }

    int points() {
      return won * 3 + drawn;
    }

    int goalDifference() {
      return goalsFor - goalsAgainst;
    }
  }

  static final Comparator<Standing> RANK = Comparator
      .comparingInt(Standing::points).reversed()
      .thenComparing(Comparator.comparingInt(Standing::goalDifference).reversed())
      .thenComparing(Comparator.comparingInt((Standing s) -> s.goalsFor).reversed())
      .thenComparing(Comparator.comparingInt((Standing s) -> s.won).reversed());

  /** Returns the league table across all played matches, sorted by RANK. */
  static List<Standing> compute(LeagueDataService service) {
    return compute(service, null);
  }

  /**
   * Returns the league table sorted by RANK, considering only matches whose
   * {@link Match#getSeasonId() season id} equals {@code seasonId}. Pass {@code null}
   * to include all matches.
   */
  static List<Standing> compute(LeagueDataService service, String seasonId) {
    if (service == null) return new ArrayList<>();
    Map<String, Standing> map = new HashMap<>();
    for (Team t : service.getTeams()) {
      map.put(t.getId(), new Standing(t.getId()));
    }
    for (Match m : service.getMatches()) {
      if (!m.isPlayed()) continue;
      if (seasonId != null && !seasonId.equals(m.getSeasonId())) continue;
      Standing home = map.get(m.getHomeTeamId());
      Standing away = map.get(m.getAwayTeamId());
      if (home == null || away == null) continue;

      home.played++; away.played++;
      home.goalsFor += m.getHomeScore();
      home.goalsAgainst += m.getAwayScore();
      away.goalsFor += m.getAwayScore();
      away.goalsAgainst += m.getHomeScore();

      if (m.getHomeScore() > m.getAwayScore()) {
        home.won++;
        away.lost++;
      } else if (m.getHomeScore() < m.getAwayScore()) {
        away.won++;
        home.lost++;
      } else {
        home.drawn++;
        away.drawn++;
      }
    }
    List<Standing> standings = new ArrayList<>(map.values());
    standings.sort(RANK);
    return standings;
  }
}
