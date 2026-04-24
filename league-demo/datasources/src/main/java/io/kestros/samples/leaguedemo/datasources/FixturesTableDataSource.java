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
public class FixturesTableDataSource extends BaseContainerSlingModelDataSource
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
      headers.add(new SyntheticTableHeader("Home", this, "header", "home"));
      headers.add(new SyntheticTableHeader("Score", this, "header", "score"));
      headers.add(new SyntheticTableHeader("Away", this, "header", "away"));
    } catch (Exception e) { /* skip */ }
    return headers;
  }

  @Nonnull
  @Override
  public List<KestrosTableRow> getRowElements() {
    List<KestrosTableRow> rows = new ArrayList<>();
    if (leagueDataService == null) return rows;

    int i = 0;
    for (Match m : leagueDataService.getMatches()) {
      Team home = leagueDataService.getTeam(m.getHomeTeamId());
      Team away = leagueDataService.getTeam(m.getAwayTeamId());
      String homeName = home != null ? home.getName() : m.getHomeTeamId();
      String awayName = away != null ? away.getName() : m.getAwayTeamId();
      String score = m.isPlayed()
          ? m.getHomeScore() + " - " + m.getAwayScore()
          : m.getDate();

      try {
        List<KestrosTableCell> cells = Arrays.asList(
            new SyntheticTableCell(m.getDate(), this, "cell", "date-" + i),
            new SyntheticTableCell(homeName, this, "cell", "home-" + i),
            new SyntheticTableCell(score, this, "cell", "score-" + i),
            new SyntheticTableCell(awayName, this, "cell", "away-" + i)
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
