package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.table.KestrosTable;
import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.api.table.KestrosTableHeader;
import io.kestros.cms.components.basic.api.table.KestrosTableRow;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.samples.league.api.models.Match;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class TeamFormTableDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public List<KestrosTableHeader> getHeaderElements() {
    List<KestrosTableHeader> headers = new ArrayList<>();
    try {
      headers.add(new SyntheticTableHeader("Date", this, "header", "date"));
      headers.add(new SyntheticTableHeader("Opponent", this, "header", "opp"));
      headers.add(new SyntheticTableHeader("Venue", this, "header", "venue"));
      headers.add(new SyntheticTableHeader("Result", this, "header", "result"));
      headers.add(new SyntheticTableHeader("Score", this, "header", "score"));
    } catch (Exception e) { /* skip */ }
    return headers;
  }

  @Nonnull
  @Override
  public List<KestrosTableRow> getRowElements() {
    List<KestrosTableRow> rows = new ArrayList<>();
    if (leagueDataService == null) return rows;

    String slug = (String) getRequest().getAttribute("team-slug");
    if (slug == null) return rows;

    int i = 0;
    for (Match m : leagueDataService.getMatches()) {
      if (!m.isPlayed()) continue;
      if (!slug.equals(m.getHomeTeamId()) && !slug.equals(m.getAwayTeamId())) continue;

      boolean isHome = slug.equals(m.getHomeTeamId());
      String oppId = isHome ? m.getAwayTeamId() : m.getHomeTeamId();
      Team opp = leagueDataService.getTeam(oppId);
      String oppName = opp != null ? opp.getName() : oppId;
      String venue = isHome ? "H" : "A";

      int teamGoals = isHome ? m.getHomeScore() : m.getAwayScore();
      int oppGoals = isHome ? m.getAwayScore() : m.getHomeScore();
      String result = teamGoals > oppGoals ? "W" : teamGoals < oppGoals ? "L" : "D";
      String score = teamGoals + "-" + oppGoals;

      try {
        List<KestrosTableCell> cells = Arrays.asList(
            new SyntheticTableCell(m.getDate(), this, "cell", "date-" + i),
            new SyntheticTableCell(oppName, this, "cell", "opp-" + i),
            new SyntheticTableCell(venue, this, "cell", "venue-" + i),
            new SyntheticTableCell(result, this, "cell", "result-" + i),
            new SyntheticTableCell(score, this, "cell", "score-" + i)
        );
        rows.add(new SyntheticTableRow(cells, this, "row", "row-" + i));
        i++;
      } catch (Exception e) { /* skip */ }
    }
    return rows;
  }

  @Nonnull
  @Override
  public List<KestrosBasicComponentElement> getChildElements() {
    return new ArrayList<>(getRowElements());
  }
}
