package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.KestrosText;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.samples.league.api.models.Season;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Records-section intro paragraph on the seasons page. Counts completed seasons and
 * distinct champions from data so the lead-in copy stays in sync with the trophy
 * cabinet without manual edits each year.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class SeasonRecordsIntroTextDataSource extends BaseSlingModelDataSource
    implements KestrosText {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nullable
  @Override
  public String getText() {
    if (leagueDataService == null) {
      return "";
    }
    long completed = leagueDataService.getSeasons().stream()
        .filter(s -> "completed".equals(s.getStatus()))
        .count();
    Set<String> champions = new HashSet<>();
    for (Season s : leagueDataService.getSeasons()) {
      if (StringUtils.isNotBlank(s.getChampionId())) {
        champions.add(s.getChampionId());
      }
    }
    return NumberWords.words((int) completed) + " completed seasons. "
        + NumberWords.words(champions.size()) + " different champions. The MPL has earned "
        + "its reputation for unpredictability the hard way -- here are the milestones "
        + "that have defined the league so far.";
  }
}
