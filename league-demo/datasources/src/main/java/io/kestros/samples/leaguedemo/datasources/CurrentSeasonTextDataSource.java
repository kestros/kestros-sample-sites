package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.KestrosText;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.samples.league.api.models.Season;
import io.kestros.samples.league.api.services.LeagueDataService;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Generic text component that emits "{prefix}{seasonName}{suffix}" for the in-progress
 * season. Useful for footer copyright lines, brand straplines, page descriptions, or any
 * other copy that needs the current season name without computed matchweek context.
 *
 * <p>Reads from the resource:
 * <ul>
 *   <li>{@code prefix} — text before the season name. Defaults to "".</li>
 *   <li>{@code suffix} — text after the season name. Defaults to "".</li>
 * </ul>
 *
 * <p>If no season is in progress, returns {@code prefix + suffix} so the surrounding copy
 * still makes sense.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class CurrentSeasonTextDataSource extends BaseSlingModelDataSource
    implements KestrosText {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nullable
  @Override
  public String getText() {
    String prefix = getResource().getValueMap().get("prefix", "");
    String suffix = getResource().getValueMap().get("suffix", "");
    if (leagueDataService == null) {
      return prefix + suffix;
    }
    String seasonName = leagueDataService.getSeasons().stream()
        .filter(s -> "in-progress".equals(s.getStatus()))
        .findFirst()
        .map(Season::getName)
        .orElse("");
    return prefix + seasonName + suffix;
  }
}
