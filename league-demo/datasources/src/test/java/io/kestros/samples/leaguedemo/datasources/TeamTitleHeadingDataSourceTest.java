package io.kestros.samples.leaguedemo.datasources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.lang.reflect.Field;
import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.Before;
import org.junit.Test;

public class TeamTitleHeadingDataSourceTest {

  private TeamTitleHeadingDataSource datasource;
  private LeagueDataService leagueDataService;
  private SlingHttpServletRequest request;

  @Before
  public void setUp() throws Exception {
    datasource = spy(new TeamTitleHeadingDataSource());
    leagueDataService = mock(LeagueDataService.class);
    request = mock(SlingHttpServletRequest.class);

    Field f = TeamTitleHeadingDataSource.class.getDeclaredField("leagueDataService");
    f.setAccessible(true);
    f.set(datasource, leagueDataService);

    doReturn(request).when(datasource).getRequest();
  }

  @Test
  public void getHeadingText_returnsTeamName_whenSlugResolves() {
    Team team = mock(Team.class);
    when(team.getName()).thenReturn("Riverside FC");
    when(leagueDataService.getTeam("riverside-fc")).thenReturn(team);
    when(request.getAttribute("team-slug")).thenReturn("riverside-fc");

    assertEquals("Riverside FC", datasource.getHeadingText());
  }

  @Test
  public void getHeadingText_returnsSlug_whenTeamNotFound() {
    when(leagueDataService.getTeam("missing-team")).thenReturn(null);
    when(request.getAttribute("team-slug")).thenReturn("missing-team");

    assertEquals("missing-team", datasource.getHeadingText());
  }

  @Test
  public void getHeadingText_returnsFallback_whenSlugNull() {
    when(request.getAttribute("team-slug")).thenReturn(null);

    assertEquals("Team", datasource.getHeadingText());
  }

  @Test
  public void getHeadingText_returnsFallback_whenServiceNull() throws Exception {
    Field f = TeamTitleHeadingDataSource.class.getDeclaredField("leagueDataService");
    f.setAccessible(true);
    f.set(datasource, null);

    assertEquals("Team", datasource.getHeadingText());
  }
}
