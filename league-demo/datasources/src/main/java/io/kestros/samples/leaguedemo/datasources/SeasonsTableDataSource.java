package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.KestrosBasicComponentElement;
import io.kestros.cms.components.basic.api.table.KestrosTable;
import io.kestros.cms.components.basic.api.table.KestrosTableCell;
import io.kestros.cms.components.basic.api.table.KestrosTableHeader;
import io.kestros.cms.components.basic.api.table.KestrosTableRow;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.samples.league.api.models.Player;
import io.kestros.samples.league.api.models.Season;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class SeasonsTableDataSource extends BaseContainerSlingModelDataSource
    implements KestrosTable {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public List<KestrosTableHeader> getHeaderElements() {
    List<KestrosTableHeader> headers = new ArrayList<>();
    try {
      headers.add(new SyntheticTableHeader("Season", this, "header", "season"));
      headers.add(new SyntheticTableHeader("Status", this, "header", "status"));
      headers.add(new SyntheticTableHeader("Champion", this, "header", "champ"));
      headers.add(new SyntheticTableHeader("Runner-up", this, "header", "runnerup"));
      headers.add(new SyntheticTableHeader("Golden Boot", this, "header", "boot"));
    } catch (Exception e) { /* skip */ }
    return headers;
  }

  @Nonnull
  @Override
  public List<KestrosTableRow> getRowElements() {
    List<KestrosTableRow> rows = new ArrayList<>();
    if (leagueDataService == null) return rows;

    int i = 0;
    for (Season s : leagueDataService.getSeasons()) {
      boolean inProgress = "in-progress".equals(s.getStatus());

      String name = s.getName() != null ? s.getName() : s.getStartYear() + "-" + s.getEndYear();
      String status = capitalize(s.getStatus());

      String champ;
      String runnerUp;
      if (inProgress) {
        String[] top = currentTopTwo(s.getId());
        champ = top[0] != null ? top[0] + " (leader)" : "—";
        runnerUp = top[1] != null ? top[1] : "—";
      } else {
        champ = teamName(s.getChampionId());
        runnerUp = teamName(s.getRunnerUpId());
      }

      String boot;
      if (inProgress) {
        boot = currentGoldenBoot();
      } else if (StringUtils.isNotBlank(s.getTopScorerId())) {
        Player p = leagueDataService.getPlayer(s.getTopScorerId());
        String name2 = p != null ? p.getFirstName() + " " + p.getLastName() : s.getTopScorerId();
        boot = s.getTopScorerGoals() > 0
            ? name2 + " (" + s.getTopScorerGoals() + ")"
            : name2;
      } else if (s.getTopScorerGoals() > 0) {
        boot = s.getTopScorerGoals() + " goals";
      } else {
        boot = "—";
      }

      try {
        List<KestrosTableCell> cells = Arrays.asList(
            new SyntheticTableCell(name, this, "cell", "name-" + i),
            new SyntheticTableCell(status, this, "cell", "status-" + i),
            new SyntheticTableCell(champ, this, "cell", "champ-" + i),
            new SyntheticTableCell(runnerUp, this, "cell", "runnerup-" + i),
            new SyntheticTableCell(boot, this, "cell", "boot-" + i)
        );
        rows.add(new SyntheticTableRow(cells, this, "row", "row-" + i));
        i++;
      } catch (Exception e) { /* skip */ }
    }
    return rows;
  }

  private String teamName(String id) {
    if (StringUtils.isBlank(id)) return "—";
    Team t = leagueDataService.getTeam(id);
    return t != null ? t.getName() : id;
  }

  private String[] currentTopTwo(String seasonId) {
    List<StandingsCalculator.Standing> table =
        StandingsCalculator.compute(leagueDataService, seasonId);
    if (table.isEmpty() || table.get(0).played == 0) {
      return new String[]{null, null};
    }
    String first = teamName(table.get(0).teamId);
    String second = table.size() > 1 ? teamName(table.get(1).teamId) : null;
    return new String[]{first, second};
  }

  private String currentGoldenBoot() {
    return leagueDataService.getPlayers().stream()
        .filter(p -> p.getGoals() > 0)
        .max(Comparator.comparingInt(Player::getGoals))
        .map(p -> p.getFirstName() + " " + p.getLastName() + " (" + p.getGoals() + ")")
        .orElse("—");
  }

  private static String capitalize(String s) {
    if (s == null || s.isEmpty()) return "";
    String hyphenated = s.substring(0, 1).toUpperCase() + s.substring(1);
    int dash = hyphenated.indexOf('-');
    if (dash > 0 && dash < hyphenated.length() - 1) {
      return hyphenated.substring(0, dash + 1)
          + Character.toUpperCase(hyphenated.charAt(dash + 1))
          + hyphenated.substring(dash + 2);
    }
    return hyphenated;
  }

  @Nonnull
  @Override
  public List<KestrosBasicComponentElement> getChildElements() {
    return new ArrayList<>(getRowElements());
  }
}
