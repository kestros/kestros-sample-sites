package io.kestros.samples.acpl.core.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

/**
 * Covers the three defects on card #257: the read of {@code matches.json} per {@code getMeetings()}
 * call, the eight methods that handed back the service's own cached state, and the rows returned by
 * {@code getMeetings()} aliasing the new cache.
 *
 * <p>These run against the real bundled {@code /data} resources, which are on the test classpath
 * from {@code src/main/resources}. {@code matches.json} holds 2023-24, 2024-25 and 2025-26, and
 * harborside v marshall is played twice in each.
 */
public class LeagueDataServiceImplTest {

  private static final String CLUB_A = "harborside";
  private static final String CLUB_B = "marshall";
  private static final int ALL_MEETINGS = 10;

  /**
   * The seam named on card #257 step 4: {@code open(String)} is package-private, so this same-package
   * subclass can record the name of every resource read. Counted per name, because {@code activate()}
   * calls {@code open()} eight times and a bare total cannot say which file a later read touched.
   */
  private static final class ReadRecordingLeagueDataService extends LeagueDataServiceImpl {

    private final Map<String, Integer> readsByName = new LinkedHashMap<>();

    @Override
    InputStream open(final String name) {
      readsByName.merge(name, 1, Integer::sum);
      return super.open(name);
    }

    int readsOf(final String name) {
      return readsByName.getOrDefault(name, 0);
    }

    Map<String, Integer> readsByName() {
      return new LinkedHashMap<>(readsByName);
    }
  }

  private ReadRecordingLeagueDataService service;

  @Before
  public void setUp() {
    service = new ReadRecordingLeagueDataService();
    service.activate();
  }

  /**
   * Baseline it is shown failing against: unmodified code. {@code getMeetings()} called
   * {@code readMap("matches.json")} on every call, so this counted 3 rather than 1.
   */
  @Test
  public void matchesJsonIsReadOnceAcrossActivationAndTwoGetMeetingsCalls() {
    service.getMeetings(CLUB_A, CLUB_B, ALL_MEETINGS);
    service.getMeetings(CLUB_A, CLUB_B, ALL_MEETINGS);

    System.out.println("resource reads after activate() + 2x getMeetings(): " + service.readsByName());

    assertEquals("matches.json must be parsed once per activation and never by getMeetings()",
        1, service.readsOf("matches.json"));
    // Keyed per name, not a bare total: every other resource is also read exactly once.
    assertEquals(1, service.readsOf("clubs.json"));
    assertEquals(1, service.readsOf("standings.json"));
    assertEquals(1, service.readsOf("recentResults.json"));
    assertEquals(1, service.readsOf("upcomingFixtures.json"));
    assertEquals(1, service.readsOf("players.json"));
    assertEquals(1, service.readsOf("stories.json"));
    assertEquals(1, service.readsOf("featuredMatch.json"));
  }

  /**
   * Baseline it is shown failing against: unmodified code, where all eight sites returned the field
   * itself (or a {@code subList} view of it), so clearing what a caller was given emptied the
   * singleton for every later caller.
   */
  @Test
  public void mutatingReturnedCollectionsDoesNotReachTheService() {
    assertUnaffectedByClear("getClubs", service.getClubs().size(), service::getClubs);
    assertUnaffectedByClear("getStandings", service.getStandings().size(), service::getStandings);
    assertUnaffectedByClear("getRecentResults", service.getRecentResults().size(),
        service::getRecentResults);
    assertUnaffectedByClear("getUpcomingFixtures", service.getUpcomingFixtures().size(),
        service::getUpcomingFixtures);
    assertUnaffectedByClear("getPlayers", service.getPlayers().size(), service::getPlayers);
    assertUnaffectedByClear("getStories", service.getStories().size(), service::getStories);

    final int featuredKeys = service.getFeaturedMatch().size();
    assertTrue("featuredMatch.json must have loaded", featuredKeys > 0);
    service.getFeaturedMatch().clear();
    assertEquals("getFeaturedMatch must not hand out the field", featuredKeys,
        service.getFeaturedMatch().size());

    // StandingsServiceImpl:23-27 returned getStandings() and a subList view of it.
    final StandingsServiceImpl standingsService = standingsServiceBackedBy(service);
    final int allRows = standingsService.getStandingsRows(0).size();
    assertTrue("standings.json must have loaded", allRows > 3);
    standingsService.getStandingsRows(0).clear();
    standingsService.getStandingsRows(3).clear();
    assertEquals("getStandingsRows must not hand out the standings list or a view of it", allRows,
        standingsService.getStandingsRows(0).size());
  }

  /**
   * Baseline it is shown failing against: the intermediate state where the new cache holds only the
   * current season, which truncates head-to-head to 2025-26. Passes on unmodified code, because
   * {@code getMeetings()} re-parsed the whole file on every call.
   */
  @Test
  public void getMeetingsStillReturnsMeetingsFromTheEarliestSeason() {
    final List<Map<String, Object>> meetings = service.getMeetings(CLUB_A, CLUB_B, ALL_MEETINGS);

    final List<Object> seasons = new ArrayList<>();
    for (final Map<String, Object> meeting : meetings) {
      seasons.add(meeting.get("season"));
    }
    assertTrue("head-to-head must span every season, not just the current one, but got " + seasons,
        seasons.contains("2023-24"));
    assertTrue(seasons.contains("2024-25"));
    assertTrue(seasons.contains("2025-26"));
  }

  /**
   * Baseline it is shown failing against: the intermediate state with the cache in place but the
   * shallow {@code new LinkedHashMap<>(m)} left as it was, so every returned row's nested goals,
   * cards and stats were the cache's own lists. Passes on unmodified code, because each call
   * re-parsed the file into a fresh object graph.
   */
  @Test
  public void getMeetingsRowsShareNoNestedValuesWithTheCache() {
    final Map<String, Object> firstCall = service.getMeetings(CLUB_A, CLUB_B, ALL_MEETINGS).get(0);
    final Map<String, Object> secondCall = service.getMeetings(CLUB_A, CLUB_B, ALL_MEETINGS).get(0);

    for (final String nested : new String[] {"goals", "cards", "stats"}) {
      assertNotSame("rows from two calls must not share their " + nested + " list",
          firstCall.get(nested), secondCall.get(nested));
    }

    // The same property stated as behaviour: emptying a row's nested lists cannot reach the cache.
    asList(firstCall.get("goals")).clear();
    asList(firstCall.get("cards")).clear();
    asList(firstCall.get("stats")).clear();

    final Map<String, Object> afterMutation =
        service.getMeetings(CLUB_A, CLUB_B, ALL_MEETINGS).get(0);
    assertFalse(asList(afterMutation.get("goals")).isEmpty());
    assertFalse(asList(afterMutation.get("cards")).isEmpty());
    assertFalse(asList(afterMutation.get("stats")).isEmpty());
  }

  private void assertUnaffectedByClear(final String method, final int expected,
      final java.util.function.Supplier<List<Map<String, Object>>> getter) {
    assertTrue(method + " must have loaded data to be a meaningful check", expected > 0);
    getter.get().clear();
    assertEquals(method + " must not hand out the field itself", expected, getter.get().size());
  }

  @SuppressWarnings("unchecked")
  private static List<Object> asList(final Object value) {
    return (List<Object>) value;
  }

  /**
   * {@code StandingsServiceImpl} takes its {@code LeagueDataService} through an OSGi
   * {@code @Reference} on a private field. Set directly rather than standing up an OSGi container,
   * which is all this assertion needs.
   */
  private static StandingsServiceImpl standingsServiceBackedBy(final LeagueDataServiceImpl backing) {
    try {
      final StandingsServiceImpl standingsService = new StandingsServiceImpl();
      final Field reference = StandingsServiceImpl.class.getDeclaredField("leagueDataService");
      reference.setAccessible(true);
      reference.set(standingsService, backing);
      return standingsService;
    } catch (final ReflectiveOperationException e) {
      throw new IllegalStateException("could not wire StandingsServiceImpl for the test", e);
    }
  }
}
