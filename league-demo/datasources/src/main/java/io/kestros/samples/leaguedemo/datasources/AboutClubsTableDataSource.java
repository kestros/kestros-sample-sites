package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.table.KestrosTable;
import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.api.table.KestrosTableHeader;
import io.kestros.cms.components.basic.api.table.KestrosTableRow;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * About page club list. Replaces the hand-authored ten-row grid with a single dynamic
 * table sourced from team data, sorted by founding year so the league's history is
 * presented chronologically without any author maintenance as clubs are added or edited.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class AboutClubsTableDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public List<KestrosTableHeader> getHeaderElements() {
    List<KestrosTableHeader> headers = new ArrayList<>();
    try {
      headers.add(new SyntheticTableHeader("Founded", this, "header", "founded"));
      headers.add(new SyntheticTableHeader("Club", this, "header", "club"));
      headers.add(new SyntheticTableHeader("City", this, "header", "city"));
      headers.add(new SyntheticTableHeader("Stadium", this, "header", "stadium"));
    } catch (Exception e) { /* skip */ }
    return headers;
  }

  @Nonnull
  @Override
  public List<KestrosTableRow> getRowElements() {
    List<KestrosTableRow> rows = new ArrayList<>();
    if (leagueDataService == null) return rows;

    List<Team> teams = leagueDataService.getTeams().stream()
        .sorted(Comparator.comparingInt(Team::getFounded))
        .collect(Collectors.toList());

    int i = 0;
    for (Team team : teams) {
      try {
        List<KestrosTableCell> cells = Arrays.asList(
            new SyntheticTableCell(team.getFounded() > 0
                ? String.valueOf(team.getFounded()) : "—",
                this, "cell", "founded-" + i),
            new SyntheticTableCell(team.getName(), this, "cell", "club-" + i),
            new SyntheticTableCell(team.getCity() != null ? team.getCity() : "—",
                this, "cell", "city-" + i),
            new SyntheticTableCell(team.getStadium() != null ? team.getStadium() : "—",
                this, "cell", "stadium-" + i)
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
