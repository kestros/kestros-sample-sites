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
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Player profile key/value table. Renders the per-player metadata not already shown in the
 * summary line (team, nationality, season totals, discipline) and computes derived stats
 * such as minutes per goal so the page demonstrates real CMS data binding rather than
 * placeholder copy.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class PlayerStatsTableDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  private Player getPlayer() {
    if (leagueDataService == null) return null;
    String slug = (String) getRequest().getAttribute("player-slug");
    return slug != null ? leagueDataService.getPlayer(slug) : null;
  }

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
    Player player = getPlayer();
    if (player == null) return rows;

    List<String[]> info = new ArrayList<>();
    if (StringUtils.isNotBlank(player.getPosition())) {
      info.add(new String[]{"Position", player.getPosition()});
    }
    if (player.getNumber() > 0) {
      info.add(new String[]{"Squad number", "#" + player.getNumber()});
    }
    Team team = leagueDataService.getTeam(player.getTeamId());
    if (team != null) {
      info.add(new String[]{"Club", team.getName()});
    }
    if (StringUtils.isNotBlank(player.getNationality())) {
      info.add(new String[]{"Nationality", player.getNationality()});
    }
    info.add(new String[]{"Appearances", String.valueOf(player.getAppearances())});
    info.add(new String[]{"Goals", String.valueOf(player.getGoals())});
    info.add(new String[]{"Assists", String.valueOf(player.getAssists())});
    info.add(new String[]{"Goal contributions",
        String.valueOf(player.getGoals() + player.getAssists())});
    if (player.getAppearances() > 0 && player.getGoals() > 0) {
      info.add(new String[]{"Minutes per goal",
          String.valueOf(Math.round(90.0 * player.getAppearances() / player.getGoals()))});
    }
    info.add(new String[]{"Discipline",
        player.getYellowCards() + " yellow / " + player.getRedCards() + " red"});

    try {
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
