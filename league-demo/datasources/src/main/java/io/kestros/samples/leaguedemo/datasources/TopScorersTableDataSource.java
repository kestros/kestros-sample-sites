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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
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
      headers.add(new SyntheticTableHeader("#", this, "header", "rank"));
      headers.add(new SyntheticTableHeader("Player", this, "header", "player"));
      headers.add(new SyntheticTableHeader("Club", this, "header", "club"));
      headers.add(new SyntheticTableHeader("Pos", this, "header", "position"));
      headers.add(new SyntheticTableHeader("Apps", this, "header", "apps"));
      headers.add(new SyntheticTableHeader("Goals", this, "header", "goals"));
      headers.add(new SyntheticTableHeader("Assists", this, "header", "assists"));
    } catch (Exception e) { /* skip */ }
    return headers;
  }

  @Nonnull
  @Override
  public List<KestrosTableRow> getRowElements() {
    List<KestrosTableRow> rows = new ArrayList<>();
    if (leagueDataService == null) return rows;

    // Sort by goals (top scorers), filter out 0-goal players for cleaner top list
    List<Player> players = leagueDataService.getPlayers().stream()
        .filter(p -> p.getGoals() > 0)
        .sorted(Comparator.comparingInt(Player::getGoals).reversed()
            .thenComparing(Comparator.comparingInt(Player::getAssists).reversed()))
        .limit(15)
        .collect(Collectors.toList());

    int pos = 0;
    for (Player player : players) {
      pos++;
      Team team = leagueDataService.getTeam(player.getTeamId());
      String teamName = team != null ? team.getName() : player.getTeamId();
      String name = player.getFirstName() + " " + player.getLastName();

      try {
        List<KestrosTableCell> cells = Arrays.asList(
            new SyntheticTableCell(String.valueOf(pos), this, "cell", "rank-" + pos),
            new SyntheticTableCell(name, this, "cell", "name-" + pos),
            new SyntheticTableCell(teamName, this, "cell", "club-" + pos),
            new SyntheticTableCell(player.getPosition(), this, "cell", "position-" + pos),
            new SyntheticTableCell(String.valueOf(PlayerAppearances.displayedFor(player, leagueDataService)), this, "cell", "apps-" + pos),
            new SyntheticTableCell(String.valueOf(player.getGoals()), this, "cell", "goals-" + pos),
            new SyntheticTableCell(String.valueOf(player.getAssists()), this, "cell", "assists-" + pos)
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
