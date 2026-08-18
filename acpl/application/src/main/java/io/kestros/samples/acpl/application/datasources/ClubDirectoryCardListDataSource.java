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
 * Club directory for the /teams index — one card per club in table order. Thin adapter over
 * {@link TeamService#getClubDirectoryCards}.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class ClubDirectoryCardListDataSource extends AbstractLeagueCardListDataSource {

  private static final Logger LOG = LoggerFactory.getLogger(ClubDirectoryCardListDataSource.class);

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
    for (final Map<String, String> c : teamService.getClubDirectoryCards(contextPath())) {
      try {
        cards.add(new SyntheticClubCard(c.get("slug"), c.get("name"), c.get("city"),
            c.get("stadium"), c.get("mgr"), c.get("pos"), c.get("record"), c.get("base"),
            c.get("href"), this, "club", "club-" + c.get("slug")));
      } catch (final Exception e) {
        LOG.error("row skipped: {}", e.getMessage(), e);
        // null-safe: skip a club that fails to build
      }
    }
    return cards;
  }
}
