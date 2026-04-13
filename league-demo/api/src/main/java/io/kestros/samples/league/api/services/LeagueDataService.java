package io.kestros.samples.league.api.services;

import io.kestros.samples.league.api.models.Match;
import io.kestros.samples.league.api.models.Player;
import io.kestros.samples.league.api.models.Season;
import io.kestros.samples.league.api.models.Team;

import java.util.List;
import javax.annotation.Nullable;

public interface LeagueDataService {

    List<Team> getTeams();

    @Nullable
    Team getTeam(String slug);

    List<Player> getPlayers();

    List<Player> getPlayersByTeam(String teamSlug);

    @Nullable
    Player getPlayer(String slug);

    List<Match> getMatches();

    List<Match> getMatchesByMatchday(int matchday);

    @Nullable
    Match getMatch(String id);

    List<Season> getSeasons();

    @Nullable
    Season getSeason(String id);
}
