package io.kestros.samples.acpl.core.services;

import io.kestros.samples.acpl.api.services.LeagueDataService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads the bundled league JSON once on activation and serves it to the datasources.
 */
@Component(service = LeagueDataService.class, immediate = true)
public class LeagueDataServiceImpl implements LeagueDataService {

  private static final Logger LOG = LoggerFactory.getLogger(LeagueDataServiceImpl.class);
  private static final TypeReference<List<Map<String, Object>>> LIST_TYPE =
      new TypeReference<List<Map<String, Object>>>() {};
  private static final TypeReference<Map<String, Object>> MAP_TYPE =
      new TypeReference<Map<String, Object>>() {};

  private final ObjectMapper mapper = new ObjectMapper();

  private List<Map<String, Object>> clubs = Collections.emptyList();
  private final Map<String, Map<String, Object>> clubBySlug = new HashMap<>();
  private List<Map<String, Object>> standings = Collections.emptyList();
  private List<Map<String, Object>> recentResults = Collections.emptyList();
  private List<Map<String, Object>> upcomingFixtures = Collections.emptyList();
  private List<Map<String, Object>> players = Collections.emptyList();
  private final Map<String, Map<String, Object>> playerBySlug = new HashMap<>();
  private List<Map<String, Object>> stories = Collections.emptyList();
  private Map<String, Object> featuredMatch = Collections.emptyMap();
  private List<Map<String, Object>> matches = Collections.emptyList();
  private Map<String, Object> matchesBySeason = Collections.emptyMap();

  @Activate
  protected void activate() {
    clubs = readList("clubs.json");
    for (final Map<String, Object> c : clubs) {
      clubBySlug.put(String.valueOf(c.get("slug")), c);
    }
    standings = readList("standings.json");
    recentResults = readList("recentResults.json");
    upcomingFixtures = readList("upcomingFixtures.json");
    players = readList("players.json");
    for (final Map<String, Object> p : players) {
      playerBySlug.put(String.valueOf(p.get("slug")), p);
    }
    stories = readList("stories.json");
    featuredMatch = readMap("featuredMatch.json");
    matchesBySeason = readMap("matches.json");
    matches = currentSeasonMatches(matchesBySeason);
    LOG.info("LeagueDataService loaded: {} clubs, {} players, {} standings rows, {} stories",
        clubs.size(), players.size(), standings.size(), stories.size());
  }

  /**
   * {@code matches.json} is keyed by season ({@code {"2023-24": [...], "2025-26": [...]}}). Picks the
   * current (highest-keyed) season's match list out of an already-parsed map; empty if that map is
   * empty or malformed. Takes the map rather than reading the file so activation parses the 772KB
   * {@code matches.json} once.
   */
  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> currentSeasonMatches(final Map<String, Object> bySeasons) {
    if (bySeasons.isEmpty()) {
      return Collections.emptyList();
    }
    final String current = bySeasons.keySet().stream().max(String::compareTo).orElse(null);
    final Object seasonMatches = current == null ? null : bySeasons.get(current);
    if (!(seasonMatches instanceof List)) {
      return Collections.emptyList();
    }
    final List<Map<String, Object>> result = new ArrayList<>();
    for (final Object o : (List<Object>) seasonMatches) {
      if (o instanceof Map) {
        result.add((Map<String, Object>) o);
      }
    }
    return result;
  }

  /**
   * Opens a bundled data resource by file name. Package-private, not private, so a same-package test
   * can subclass this service and record which resource each read touched.
   *
   * @param name file name under {@code /data}, e.g. {@code matches.json}
   * @return the resource stream, or {@code null} if there is no such resource
   */
  InputStream open(final String name) {
    return getClass().getClassLoader().getResourceAsStream("data/" + name);
  }

  private List<Map<String, Object>> readList(final String name) {
    try (InputStream in = open(name)) {
      if (in == null) {
        LOG.warn("League data resource not found: {}", name);
        return Collections.emptyList();
      }
      return mapper.readValue(in, LIST_TYPE);
    } catch (final Exception e) {
      LOG.error("Failed reading league data {}: {}", name, e.getMessage());
      return Collections.emptyList();
    }
  }

  private Map<String, Object> readMap(final String name) {
    try (InputStream in = open(name)) {
      if (in == null) {
        return Collections.emptyMap();
      }
      return mapper.readValue(in, MAP_TYPE);
    } catch (final Exception e) {
      LOG.error("Failed reading league data {}: {}", name, e.getMessage());
      return Collections.emptyMap();
    }
  }

  @Override
  public List<Map<String, Object>> getClubs() {
    return new ArrayList<>(clubs);
  }

  @Override
  public Map<String, Object> getClub(final String slug) {
    return clubBySlug.getOrDefault(slug, Collections.emptyMap());
  }

  @Override
  public String getClubName(final String slug) {
    final Object n = getClub(slug).get("name");
    return n != null ? String.valueOf(n) : slug;
  }

  @Override
  public String getClubShort(final String slug) {
    final Object s = getClub(slug).get("short");
    return s != null ? String.valueOf(s) : slug;
  }

  @Override
  public List<Map<String, Object>> getStandings() {
    return new ArrayList<>(standings);
  }

  @Override
  public List<Map<String, Object>> getRecentResults() {
    return new ArrayList<>(recentResults);
  }

  @Override
  public List<Map<String, Object>> getUpcomingFixtures() {
    return new ArrayList<>(upcomingFixtures);
  }

  @Override
  public List<Map<String, Object>> getPlayers() {
    return new ArrayList<>(players);
  }

  @Override
  public Map<String, Object> getPlayer(final String slug) {
    return playerBySlug.getOrDefault(slug, Collections.emptyMap());
  }

  @Override
  public List<Map<String, Object>> getSquad(final String clubSlug) {
    final List<Map<String, Object>> squad = players.stream()
        .filter(p -> clubSlug.equals(p.get("club")))
        .collect(Collectors.toList());
    squad.sort((a, b) -> asInt(a.get("num")) - asInt(b.get("num")));
    return squad;
  }

  @Override
  public Map<String, Object> getStanding(final String clubSlug) {
    return standings.stream()
        .filter(r -> clubSlug.equals(String.valueOf(r.get("club"))))
        .findFirst()
        .orElse(Collections.emptyMap());
  }

  @Override
  public List<Map<String, Object>> getTopScorers(final String clubSlug, final int limit) {
    final List<Map<String, Object>> scorers = players.stream()
        .filter(p -> clubSlug.equals(String.valueOf(p.get("club"))))
        .filter(p -> seasonStat(p, "goals") > 0)
        .sorted((a, b) -> seasonStat(b, "goals") - seasonStat(a, "goals"))
        .collect(Collectors.toList());
    return limit > 0 && scorers.size() > limit ? scorers.subList(0, limit) : scorers;
  }

  @Override
  public List<Map<String, Object>> getClubForm(final String clubSlug, final int limit) {
    final List<Map<String, Object>> played = matches.stream()
        .filter(m -> Boolean.TRUE.equals(m.get("played")))
        .filter(m -> clubSlug.equals(String.valueOf(m.get("home")))
            || clubSlug.equals(String.valueOf(m.get("away"))))
        .sorted((a, b) -> String.valueOf(a.get("date")).compareTo(String.valueOf(b.get("date"))))
        .collect(Collectors.toList());
    final List<Map<String, Object>> recent =
        limit > 0 && played.size() > limit ? played.subList(played.size() - limit, played.size())
            : played;
    final List<Map<String, Object>> form = new ArrayList<>();
    for (final Map<String, Object> m : recent) {
      final boolean home = clubSlug.equals(String.valueOf(m.get("home")));
      final int gf = asInt(home ? m.get("hg") : m.get("ag"));
      final int ga = asInt(home ? m.get("ag") : m.get("hg"));
      final String result = gf > ga ? "W" : gf < ga ? "L" : "D";
      final Map<String, Object> row = new HashMap<>();
      row.put("result", result);
      row.put("opp", String.valueOf(home ? m.get("away") : m.get("home")));
      row.put("gf", gf);
      row.put("ga", ga);
      form.add(row);
    }
    return form;
  }

  @Override
  public Map<String, Object> getNextFixture(final String clubSlug) {
    return upcomingFixtures.stream()
        .filter(m -> clubSlug.equals(String.valueOf(m.get("home")))
            || clubSlug.equals(String.valueOf(m.get("away"))))
        .findFirst()
        .orElse(Collections.emptyMap());
  }

  @Override
  public Map<String, Object> getMatch(final String matchId) {
    return matches.stream()
        .filter(m -> matchId.equals(String.valueOf(m.get("id"))))
        .findFirst()
        .orElse(Collections.emptyMap());
  }

  @Override
  public List<Map<String, Object>> getPlayedMatches() {
    return matches.stream()
        .filter(m -> Boolean.TRUE.equals(m.get("played")))
        .sorted((a, b) -> {
          final int byWeek = asInt(a.get("mw")) - asInt(b.get("mw"));
          return byWeek != 0 ? byWeek
              : String.valueOf(a.get("date")).compareTo(String.valueOf(b.get("date")));
        })
        .collect(Collectors.toList());
  }

  private static int seasonStat(final Map<String, Object> player, final String key) {
    final Object season = player.get("season");
    if (season instanceof Map) {
      return asInt(((Map<?, ?>) season).get(key));
    }
    return 0;
  }

  @Override
  public List<Map<String, Object>> getStories() {
    return new ArrayList<>(stories);
  }

  @Override
  public Map<String, Object> getFeaturedMatch() {
    return new LinkedHashMap<>(featuredMatch);
  }

  private static int asInt(final Object o) {
    if (o instanceof Number) {
      return ((Number) o).intValue();
    }
    try {
      return Integer.parseInt(String.valueOf(o));
    } catch (final NumberFormatException e) {
      return 0;
    }
  }

  /**
   * Recursively copies the maps and lists inside a parsed JSON value, so that nothing handed to a
   * caller shares a nested container with the cache. Strings, numbers and booleans are immutable and
   * are passed through.
   *
   * @param value a parsed JSON value
   * @return a value that shares no mutable container with {@code value}
   */
  @SuppressWarnings("unchecked")
  private static Object deepCopy(final Object value) {
    if (value instanceof Map) {
      final Map<String, Object> copy = new LinkedHashMap<>();
      for (final Map.Entry<String, Object> e : ((Map<String, Object>) value).entrySet()) {
        copy.put(e.getKey(), deepCopy(e.getValue()));
      }
      return copy;
    }
    if (value instanceof List) {
      final List<Object> source = (List<Object>) value;
      final List<Object> copy = new ArrayList<>(source.size());
      for (final Object o : source) {
        copy.add(deepCopy(o));
      }
      return copy;
    }
    return value;
  }
  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public List<Map<String, Object>> getMeetings(final String clubA, final String clubB,
      final int limit) {
    final List<Map<String, Object>> meetings = new ArrayList<>();
    final Map<String, Object> bySeasons = matchesBySeason;
    final List<String> seasons = new ArrayList<>(bySeasons.keySet());
    seasons.sort(Collections.reverseOrder());
    for (final String season : seasons) {
      if (!(bySeasons.get(season) instanceof List)) {
        continue;
      }
      final List<Map<String, Object>> seasonMeetings = new ArrayList<>();
      for (final Object o : (List<Object>) bySeasons.get(season)) {
        if (!(o instanceof Map)) {
          continue;
        }
        final Map<String, Object> m = (Map<String, Object>) o;
        final boolean pair = (clubA.equals(m.get("home")) && clubB.equals(m.get("away")))
            || (clubB.equals(m.get("home")) && clubA.equals(m.get("away")));
        if (pair && Boolean.TRUE.equals(m.get("played"))) {
          final Map<String, Object> withSeason = (Map<String, Object>) deepCopy(m);
          withSeason.put("season", season);
          seasonMeetings.add(withSeason);
        }
      }
      // newest first within the season
      Collections.reverse(seasonMeetings);
      for (final Map<String, Object> m : seasonMeetings) {
        if (meetings.size() >= limit) {
          return meetings;
        }
        meetings.add(m);
      }
    }
    return meetings;
  }
}