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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Team-detail header, keyed on the {@code team} route param. Thin adapter over
 * {@link TeamService#getTeamHeader}; one {@link SyntheticTeamHeaderCard} rendered by the
 * {@code team-header} card layout.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class TeamHeaderCardListDataSource extends AbstractLeagueCardListDataSource {

  private static final Logger LOG = LoggerFactory.getLogger(TeamHeaderCardListDataSource.class);

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
    final Map<String, String> header = teamService.getTeamHeader(getTeam(), contextPath());
    if (header.isEmpty()) {
      return cards;
    }
    try {
      cards.add(new SyntheticTeamHeaderCard(header.get("slug"), header.get("name"),
          header.get("place"), header.get("meta"), header.get("base"),
          this, "teamHeader", "team-header").withColor(header.get("color")));
    } catch (final Exception e) {
      LOG.error("TeamHeaderCardListDataSource: {}", e.getMessage());
      // null-safe: skip on build failure
    }
    return cards;
  }
}
