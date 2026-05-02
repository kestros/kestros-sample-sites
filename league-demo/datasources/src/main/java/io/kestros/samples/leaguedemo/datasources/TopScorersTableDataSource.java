package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.table.KestrosTable;
import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.api.table.KestrosTableHeader;
import io.kestros.cms.components.basic.api.table.KestrosTableRow;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.samples.league.api.models.Player;
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
public class TopScorersTableDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public List<KestrosTableHeader> getHeaderElements() {
    List<KestrosTableHeader> headers = new ArrayList<>();
    try {
      headers.add(new SyntheticTableHeader("No.", this, "header", "number"));
      headers.add(new SyntheticTableHeader("Player", this, "header", "player"));
      headers.add(new SyntheticTableHeader("Club", this, "header", "club"));
      headers.add(new SyntheticTableHeader("Position", this, "header", "position"));
    } catch (Exception e) { /* skip */ }
    return headers;
  }

  @Nonnull
  @Override
  public List<KestrosTableRow> getRowElements() {
    List<KestrosTableRow> rows = new ArrayList<>();
    if (leagueDataService == null) return rows;

    // List all players sorted by number (as proxy for prominence)
    List<Player> players = new ArrayList<>(leagueDataService.getPlayers());
    if (players.size() > 15) {
      players = players.subList(0, 15);
    }

    int pos = 0;
    for (Player player : players) {
      pos++;
      Team team = leagueDataService.getTeam(player.getTeamId());
      String teamName = team != null ? team.getName() : player.getTeamId();
      String name = player.getFirstName() + " " + player.getLastName();

      try {
        List<KestrosTableCell> cells = Arrays.asList(
            new SyntheticTableCell(String.valueOf(player.getNumber()), this, "cell", "number-" + pos),
            new SyntheticTableCell(name, this, "cell", "name-" + pos),
            new SyntheticTableCell(teamName, this, "cell", "club-" + pos),
            new SyntheticTableCell(player.getPosition(), this, "cell", "position-" + pos)
        );
        rows.add(new SyntheticTableRow(cells, this, "row", "row-" + pos));
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
