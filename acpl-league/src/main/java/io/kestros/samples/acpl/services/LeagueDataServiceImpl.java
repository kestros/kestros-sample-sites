package io.kestros.samples.acpl.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
    LOG.info("LeagueDataService loaded: {} clubs, {} players, {} standings rows, {} stories",
        clubs.size(), players.size(), standings.size(), stories.size());
  }

  private InputStream open(final String name) {
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
    return clubs;
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
    return standings;
  }

  @Override
  public List<Map<String, Object>> getRecentResults() {
    return recentResults;
  }

  @Override
  public List<Map<String, Object>> getUpcomingFixtures() {
    return upcomingFixtures;
  }

  @Override
  public List<Map<String, Object>> getPlayers() {
    return players;
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
  public List<Map<String, Object>> getStories() {
    return stories;
  }

  @Override
  public Map<String, Object> getFeaturedMatch() {
    return featuredMatch;
  }

  private static int asInt(final Object o) {
    if (o instanceof Number) {
      return ((Number) o).intValue();
    }
    try {
      return Integer.parseInt(String.valueOf(o));
    } catch (final NumberFormatException e) {
      return new ArrayList<>().size();
    }
  }
}
