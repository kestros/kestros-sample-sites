package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.samples.acpl.api.services.TeamService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * Top-scorers list for a team-detail page. Thin adapter over {@link TeamService#getTopScorerRows};
 * each scorer is a {@link SyntheticScorerCard} rendered by the {@code scorer-row} card layout.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class TopScorersCardListDataSource extends AbstractLeagueCardListDataSource {

  @OSGiService
  @Optional
  private TeamService teamService;

  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    final List<KestrosCard> cards = new ArrayList<>();
    if (teamService == null) {
      return cards;
    }
    int i = 0;
    for (final Map<String, String> row : teamService.getTopScorerRows(getTeam(), contextPath())) {
      try {
        cards.add(new SyntheticScorerCard(row.get("name"), row.get("goals"), row.get("href"),
            this, "scorer", "scorer-" + i));
        i++;
      } catch (final Exception e) {
        // null-safe
      }
    }
    return cards;
  }
}
