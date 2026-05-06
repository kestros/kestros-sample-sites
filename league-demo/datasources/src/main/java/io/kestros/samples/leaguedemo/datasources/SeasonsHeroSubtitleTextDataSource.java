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
 * Seasons archive hero subtitle: "{N} seasons of Meridian Premier League football.
 * {M} different champions." Both numbers come from season data so the strapline keeps
 * pace with new seasons without copy edits. Small counts render as words for readability.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class SeasonsHeroSubtitleTextDataSource extends BaseSlingModelDataSource
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
    int total = leagueDataService.getSeasons().size();
    Set<String> champions = new HashSet<>();
    for (Season s : leagueDataService.getSeasons()) {
      if (StringUtils.isNotBlank(s.getChampionId())) {
        champions.add(s.getChampionId());
      }
    }
    return NumberWords.words(total) + " seasons of Meridian Premier League football. "
        + NumberWords.words(champions.size()) + " different champions.";
  }
}
