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
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class TeamInfoTableDataSource extends BaseContainerSlingModelDataSource
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

    String slug = (String) getRequest().getAttribute("team-slug");
    if (slug == null) return rows;

    Team team = leagueDataService.getTeam(slug);
    if (team == null) return rows;

    List<String[]> info = new ArrayList<>();
    if (StringUtils.isNotBlank(team.getNickname())) {
      info.add(new String[]{"Nickname", team.getNickname()});
    }
    info.add(new String[]{"City", team.getCity()});
    if (StringUtils.isNotBlank(team.getManager())) {
      info.add(new String[]{"Manager", team.getManager()});
    }
    if (team.getFounded() > 0) {
      info.add(new String[]{"Founded", String.valueOf(team.getFounded())});
    }
    String stadium = team.getStadium();
    if (team.getVenueOpened() > 0) {
      stadium = stadium + " (opened " + team.getVenueOpened() + ")";
    }
    info.add(new String[]{"Stadium", stadium});
    if (team.getStadiumCapacity() > 0) {
      info.add(new String[]{"Capacity", String.format("%,d", team.getStadiumCapacity())});
    }
    if (StringUtils.isNotBlank(team.getSurface())) {
      info.add(new String[]{"Surface", team.getSurface()});
    }
    if (StringUtils.isNotBlank(team.getVenueAddress())) {
      info.add(new String[]{"Address", team.getVenueAddress()});
    }

    info.add(new String[]{"Squad size",
        String.valueOf(leagueDataService.getPlayersByTeam(slug).size())});

    String position = leaguePosition(slug);
    if (position != null) {
      info.add(new String[]{"League position", position});
    }

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

  /**
   * Looks up the team's current league position from the shared standings calculator
   * and renders it as "Nth of M" so the profile makes sense in any league size.
   */
  private String leaguePosition(String slug) {
    List<StandingsCalculator.Standing> table = StandingsCalculator.compute(leagueDataService);
    for (int i = 0; i < table.size(); i++) {
      if (slug.equals(table.get(i).teamId)) {
        int rank = i + 1;
        return rank + ordinalSuffix(rank) + " of " + table.size();
      }
    }
    return null;
  }

  private static String ordinalSuffix(int rank) {
    if (rank % 100 >= 11 && rank % 100 <= 13) return "th";
    switch (rank % 10) {
      case 1: return "st";
      case 2: return "nd";
      case 3: return "rd";
      default: return "th";
    }
  }

  @Nonnull
  @Override
  public List<KestrosBasicComponentElement> getChildElements() {
    return new ArrayList<>(getRowElements());
  }
}
