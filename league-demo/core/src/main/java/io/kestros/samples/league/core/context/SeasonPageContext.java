package io.kestros.samples.league.core.context;

import io.kestros.samples.league.api.models.Match;
import io.kestros.samples.league.api.models.Season;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = SlingHttpServletRequest.class)
public class SeasonPageContext {

  private final SlingHttpServletRequest request;

  @OSGiService
  private LeagueDataService leagueDataService;

  private Season season;

  public SeasonPageContext(SlingHttpServletRequest request) {
    this.request = request;
  }

  @PostConstruct
  protected void init() {
    String id = (String) request.getAttribute("season-id");
    if (id != null && leagueDataService != null) {
      season = leagueDataService.getSeason(id);
    }
  }

  @Nullable
  public Season getSeason() {
    return season;
  }

  public List<Team> getTeams() {
    if (leagueDataService == null) {
      return Collections.emptyList();
    }
    return leagueDataService.getTeams();
  }

  public List<Match> getMatches() {
    if (leagueDataService == null) {
      return Collections.emptyList();
    }
    return leagueDataService.getMatches();
  }
}
