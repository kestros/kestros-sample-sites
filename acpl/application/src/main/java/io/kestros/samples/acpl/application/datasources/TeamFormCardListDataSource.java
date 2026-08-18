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
 * Recent-form badges for a team-detail page. Thin adapter over {@link TeamService#getFormBadges};
 * each result is a {@link SyntheticFormCard} rendered by the {@code form-badge} card layout.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class TeamFormCardListDataSource extends AbstractLeagueCardListDataSource {

  @OSGiService
  @Optional
  private TeamService teamService;

  /**
   * Club to render: if the node names a {@code clubAttr}, resolve that request attribute (set by a
   * dynamic-page filter, e.g. homeTeam/awayTeam on match routes); else the usual team param.
   */
  private String resolveClub() {
    final String clubAttr = getResource().getValueMap().get("clubAttr", "");
    if (!clubAttr.isEmpty() && getRequest() != null
        && getRequest().getAttribute(clubAttr) != null) {
      return String.valueOf(getRequest().getAttribute(clubAttr));
    }
    return getTeam();
  }

  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    final List<KestrosCard> cards = new ArrayList<>();
    if (teamService == null) {
      return cards;
    }
    int i = 0;
    for (final Map<String, String> badge : teamService.getFormBadges(resolveClub())) {
      try {
        cards.add(new SyntheticFormCard(badge.get("result"), badge.get("badgeClass"),
            badge.get("tip"), this, "form", "form-" + i));
        i++;
      } catch (final Exception e) {
        // null-safe
      }
    }
    return cards;
  }
}
