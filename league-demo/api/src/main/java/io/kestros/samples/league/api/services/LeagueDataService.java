package io.kestros.samples.league.api.services;

import io.kestros.samples.league.api.models.Match;
import io.kestros.samples.league.api.models.Player;
import io.kestros.samples.league.api.models.Season;
import io.kestros.samples.league.api.models.Team;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Service that exposes league demo content (teams, players, matches, seasons) to the site.
 */
public interface LeagueDataService {

  /**
   * Retrieves all teams in the league.
   *
   * @return list of teams.
   */
  @Nonnull
  List<Team> getTeams();

  /**
   * Retrieves a single team by slug.
   *
   * @param slug team slug.
   * @return the matching team, or {@code null} if not found.
   */
  @Nullable
  Team getTeam(@Nonnull String slug);

  /**
   * Retrieves all players across all teams.
   *
   * @return list of players.
   */
  @Nonnull
  List<Player> getPlayers();

  /**
   * Retrieves all players belonging to a specific team.
   *
   * @param teamSlug team slug.
   * @return list of players on the team.
   */
  @Nonnull
  List<Player> getPlayersByTeam(@Nonnull String teamSlug);

  /**
   * Retrieves a single player by slug.
   *
   * @param slug player slug.
   * @return the matching player, or {@code null} if not found.
   */
  @Nullable
  Player getPlayer(@Nonnull String slug);

  /**
   * Retrieves all matches in the league.
   *
   * @return list of matches.
   */
  @Nonnull
  List<Match> getMatches();

  /**
   * Retrieves matches scheduled for a given matchday.
   *
   * @param matchday matchday number.
   * @return list of matches on that matchday.
   */
  @Nonnull
  List<Match> getMatchesByMatchday(int matchday);

  /**
   * Retrieves a single match by identifier.
   *
   * @param id match identifier.
   * @return the matching match, or {@code null} if not found.
   */
  @Nullable
  Match getMatch(@Nonnull String id);

  /**
   * Retrieves all seasons in the league.
   *
   * @return list of seasons.
   */
  @Nonnull
  List<Season> getSeasons();

  /**
   * Retrieves a single season by identifier.
   *
   * @param id season identifier.
   * @return the matching season, or {@code null} if not found.
   */
  @Nullable
  Season getSeason(@Nonnull String id);
}
