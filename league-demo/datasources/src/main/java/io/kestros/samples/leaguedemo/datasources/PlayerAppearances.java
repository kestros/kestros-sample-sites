package io.kestros.samples.leaguedemo.datasources;

import io.kestros.samples.league.api.models.Match;
import io.kestros.samples.league.api.models.Player;
import io.kestros.samples.league.api.services.LeagueDataService;

/**
 * Returns the appearance count to display for a player, capped at the number of matches
 * their team has actually played. The seed data carries an aspirational
 * {@link Player#getAppearances() appearances} value (e.g. a striker is recorded as having
 * played 18 matches even if the league has only completed 12). Capping keeps the displayed
 * stats internally consistent — a player can't have more apps than their team has games.
 */
final class PlayerAppearances {

  private PlayerAppearances() {}

  static int displayedFor(Player player, LeagueDataService service) {
    if (player == null) return 0;
    int raw = player.getAppearances();
    if (service == null || player.getTeamId() == null) return raw;
    int teamPlayed = (int) service.getMatches().stream()
        .filter(Match::isPlayed)
        .filter(m -> player.getTeamId().equals(m.getHomeTeamId())
            || player.getTeamId().equals(m.getAwayTeamId()))
        .count();
    return Math.min(raw, teamPlayed);
  }
}
