package io.kestros.samples.acpl.application.datasources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.cms.componenttypes.api.services.ComponentUiFrameworkViewRetrievalService;
import io.kestros.cms.componenttypes.api.services.ComponentVariationRetrievalService;
import io.kestros.samples.acpl.api.services.TeamService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 * A club whose card cannot be built must leave a trace in the log and must not take the rest of the
 * directory with it.
 */
public class ClubDirectoryCardListDataSourceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private TeamService teamService;

  private ListAppender<ILoggingEvent> appender;

  private Logger logger;

  @Before
  public void setUp() {
    context.addModelsForPackage("io.kestros.samples.acpl.application.datasources");
    teamService = mock(TeamService.class);
    context.registerService(TeamService.class, teamService);
    // Required by BaseSlingModelDataSource; without it the model does not adapt at all and every
    // assertion below would be about the harness rather than the code.
    context.registerService(ComponentVariationRetrievalService.class,
        mock(ComponentVariationRetrievalService.class));
    context.registerService(ComponentUiFrameworkViewRetrievalService.class,
        mock(ComponentUiFrameworkViewRetrievalService.class));

    logger = (Logger) LoggerFactory.getLogger(ClubDirectoryCardListDataSource.class);
    appender = new ListAppender<>();
    appender.start();
    logger.addAppender(appender);
    logger.setLevel(Level.ERROR);
  }

  @After
  public void tearDown() {
    logger.detachAppender(appender);
  }

  private Map<String, String> goodClub() {
    final Map<String, String> club = new HashMap<>();
    club.put("slug", "harborside");
    club.put("name", "Harborside United");
    club.put("city", "Harborside");
    club.put("stadium", "The Quay");
    club.put("mgr", "A Manager");
    club.put("pos", "1");
    club.put("record", "10-0-0");
    club.put("base", "/content/acpl");
    club.put("href", "/content/acpl/teams/harborside.html");
    return club;
  }

  @SuppressWarnings("unchecked")
  private Map<String, String> throwingClub() {
    final Map<String, String> club = mock(Map.class);
    doThrow(new IllegalStateException("club row is unreadable")).when(club).get(anyString());
    return club;
  }

  @Test
  public void testFailedClubIsLogged() {
    // Build the rows BEFORE stubbing: creating a mock inside a when(...) argument leaves the outer
    // stubbing unfinished and Mockito fails the test rather than the code under test.
    final List<Map<String, String>> rows = new ArrayList<>(Arrays.asList(throwingClub()));
    when(teamService.getClubDirectoryCards(anyString())).thenReturn(rows);

    final ClubDirectoryCardListDataSource dataSource = adaptDataSource();
    dataSource.getCardElements();

    assertEquals("one failure, one log line", 1, appender.list.size());
    assertEquals(Level.ERROR, appender.list.get(0).getLevel());
    assertTrue("the log line names the cause",
        appender.list.get(0).getFormattedMessage().contains("club row is unreadable"));
  }

  @Test
  public void testTheLoopContinuesPastAFailedClub() {
    final List<Map<String, String>> rows = new ArrayList<>(
        Arrays.asList(throwingClub(), throwingClub()));
    when(teamService.getClubDirectoryCards(anyString())).thenReturn(rows);

    final ClubDirectoryCardListDataSource dataSource = adaptDataSource();
    dataSource.getCardElements();

    // Two rows in, two log lines out: the first failure did not abort the loop. Before the fix this
    // assertion read 0 and could not tell the difference between "skipped one row" and "dropped the
    // whole table".
    assertEquals("both failures are logged, so the loop ran twice", 2, appender.list.size());
  }

  private ClubDirectoryCardListDataSource adaptDataSource() {
    final Resource resource = context.create().resource("/content/acpl/jcr:content/clubs");
    final ClubDirectoryCardListDataSource dataSource = resource.adaptTo(
        ClubDirectoryCardListDataSource.class);
    assertNotNull("the datasource model adapts", dataSource);
    return dataSource;
  }
}
