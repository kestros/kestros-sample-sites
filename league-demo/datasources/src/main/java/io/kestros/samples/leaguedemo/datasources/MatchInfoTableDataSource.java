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
public class MatchInfoTableDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public List<KestrosTableHeader> getHeaderElements() {
    List<KestrosTableHeader> headers = new ArrayList<>();
    try {
      headers.add(new SyntheticTableHeader("", this, "header", "label"));
      headers.add(new SyntheticTableHeader("", this, "header", "value"));
    } catch (Exception e) { /* skip */ }
    return headers;
  }

  @Nonnull
  @Override
  public List<KestrosTableRow> getRowElements() {
    List<KestrosTableRow> rows = new ArrayList<>();
    if (leagueDataService == null) return rows;

    String id = (String) getRequest().getAttribute("match-id");
    if (id == null) return rows;

    Match match = leagueDataService.getMatch(id);
    if (match == null) return rows;

    Team home = leagueDataService.getTeam(match.getHomeTeamId());
    Team away = leagueDataService.getTeam(match.getAwayTeamId());
    String homeName = home != null ? home.getName() : match.getHomeTeamId();
    String awayName = away != null ? away.getName() : match.getAwayTeamId();
    String result = match.isPlayed()
        ? homeName + " " + match.getHomeScore() + " — " + match.getAwayScore() + " " + awayName
        : homeName + " vs " + awayName;
    String status = match.isPlayed() ? "Full Time" : "Upcoming";

    try {
      String[][] info = {
          {"Status", status},
          {"Date", match.getDate() != null ? match.getDate() : "—"},
          {"Matchweek", String.valueOf(match.getMatchday())},
          {"Venue", match.getVenue() != null ? match.getVenue() : "—"},
          {"Result", result},
      };
      int i = 0;
      for (String[] entry : info) {
        List<KestrosTableCell> cells = Arrays.asList(
            new SyntheticTableCell(entry[0], this, "cell", "label-" + i),
            new SyntheticTableCell(entry[1], this, "cell", "value-" + i)
        );
        rows.add(new SyntheticTableRow(cells, this, "row", "row-" + i));
        i++;
      }
    } catch (Exception e) { /* skip */ }
    return rows;
  }

  @Nonnull
  @Override
  public List<KestrosBasicComponentElement> getChildElements() {
    return new ArrayList<>(getRowElements());
  }
}
