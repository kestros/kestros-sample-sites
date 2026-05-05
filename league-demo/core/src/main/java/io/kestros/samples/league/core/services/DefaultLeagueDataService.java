package io.kestros.samples.league.core.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kestros.samples.league.api.models.Article;
import io.kestros.samples.league.api.models.Match;
import io.kestros.samples.league.api.models.MerchItem;
import io.kestros.samples.league.api.models.Sponsor;
import io.kestros.samples.league.api.models.Player;
import io.kestros.samples.league.api.models.Season;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import io.kestros.samples.league.core.models.ArticleData;
import io.kestros.samples.league.core.models.MerchItemData;
import io.kestros.samples.league.core.models.SponsorData;
import io.kestros.samples.league.core.models.MatchData;
import io.kestros.samples.league.core.models.PlayerData;
import io.kestros.samples.league.core.models.SeasonData;
import io.kestros.samples.league.core.models.TeamData;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = LeagueDataService.class, immediate = true)
public class DefaultLeagueDataService implements LeagueDataService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultLeagueDataService.class);
    private static final String DATA_PATH = "/league-data/";

    private List<TeamData> teams = Collections.emptyList();
    private List<PlayerData> players = Collections.emptyList();
    private List<MatchData> matches = Collections.emptyList();
    private List<SeasonData> seasons = Collections.emptyList();
    private List<ArticleData> articles = Collections.emptyList();
    private List<SponsorData> sponsors = Collections.emptyList();
    private List<MerchItemData> merchandise = Collections.emptyList();

    private Map<String, TeamData> teamsById = Collections.emptyMap();
    private Map<String, PlayerData> playersById = Collections.emptyMap();
    private Map<String, MatchData> matchesById = Collections.emptyMap();
    private Map<String, SeasonData> seasonsById = Collections.emptyMap();

    @Activate
    protected void activate() {
        ObjectMapper mapper = new ObjectMapper();
        teams = loadJson(mapper, "teams.json", new TypeReference<List<TeamData>>() {});
        players = loadJson(mapper, "players.json", new TypeReference<List<PlayerData>>() {});
        matches = loadJson(mapper, "matches.json", new TypeReference<List<MatchData>>() {});
        seasons = loadJson(mapper, "seasons.json", new TypeReference<List<SeasonData>>() {});
        articles = loadJson(mapper, "articles.json", new TypeReference<List<ArticleData>>() {});
        sponsors = loadJson(mapper, "sponsors.json", new TypeReference<List<SponsorData>>() {});
        merchandise = loadJson(mapper, "merchandise.json", new TypeReference<List<MerchItemData>>() {});

        teamsById = indexById(teams, TeamData::getId);
        playersById = indexById(players, PlayerData::getId);
        matchesById = indexById(matches, MatchData::getId);
        seasonsById = indexById(seasons, SeasonData::getId);

        LOG.info("Loaded league data: {} teams, {} players, {} matches, {} seasons, {} articles, {} sponsors, {} merch",
                teams.size(), players.size(), matches.size(), seasons.size(), articles.size(), sponsors.size(), merchandise.size());
    }

    private static <T> Map<String, T> indexById(List<T> items, Function<T, String> idFn) {
        Map<String, T> map = new HashMap<>(items.size() * 2);
        for (T item : items) {
            String id = idFn.apply(item);
            if (id != null) {
                map.put(id, item);
            }
        }
        return map;
    }

    private <T> List<T> loadJson(ObjectMapper mapper, String filename, TypeReference<List<T>> type) {
        try (InputStream is = getClass().getResourceAsStream(DATA_PATH + filename)) {
            if (is == null) {
                LOG.warn("League data file not found: {}", filename);
                return Collections.emptyList();
            }
            return mapper.readValue(is, type);
        } catch (IOException e) {
            LOG.error("Failed to load league data: {}", filename, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Team> getTeams() {
        return Collections.unmodifiableList(teams);
    }

    @Nullable
    @Override
    public Team getTeam(String slug) {
        return slug == null ? null : teamsById.get(slug);
    }

    @Override
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    @Override
    public List<Player> getPlayersByTeam(String teamSlug) {
        return players.stream()
                .filter(p -> teamSlug.equals(p.getTeamId()))
                .collect(Collectors.toList());
    }

    @Nullable
    @Override
    public Player getPlayer(String slug) {
        return slug == null ? null : playersById.get(slug);
    }

    @Override
    public List<Match> getMatches() {
        return Collections.unmodifiableList(matches);
    }

    @Override
    public List<Match> getMatchesByMatchday(int matchday) {
        return matches.stream()
                .filter(m -> m.getMatchday() == matchday)
                .collect(Collectors.toList());
    }

    @Nullable
    @Override
    public Match getMatch(String id) {
        return id == null ? null : matchesById.get(id);
    }

    @Override
    public List<Season> getSeasons() {
        return Collections.unmodifiableList(seasons);
    }

    @Nullable
    @Override
    public Season getSeason(String id) {
        return id == null ? null : seasonsById.get(id);
    }

    @Override
    public List<Article> getArticles() {
        return Collections.unmodifiableList(articles);
    }

    @Override
    public List<Article> getArticlesByTag(String tag) {
        return articles.stream()
                .filter(a -> a.getTags() != null && a.getTags().contains(tag))
                .collect(Collectors.toList());
    }

    @Override
    public List<Sponsor> getSponsors() {
        return Collections.unmodifiableList(sponsors);
    }

    @Override
    public List<MerchItem> getMerchandise() {
        return Collections.unmodifiableList(merchandise);
    }
}
