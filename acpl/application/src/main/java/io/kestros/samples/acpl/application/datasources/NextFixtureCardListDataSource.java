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
 * Next-fixture card for a team-detail page. Thin adapter over {@link TeamService#getNextFixtureCard};
 * one {@link SyntheticFixtureCard} rendered by the {@code next-fixture} card layout.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class NextFixtureCardListDataSource extends AbstractLeagueCardListDataSource {

  private static final Logger LOG = LoggerFactory.getLogger(NextFixtureCardListDataSource.class);

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
    final Map<String, String> fixture = teamService.getNextFixtureCard(getTeam(), contextPath());
    if (fixture.isEmpty()) {
      return cards;
    }
    try {
      cards.add(new SyntheticFixtureCard(fixture.get("oppSlug"), fixture.get("oppName"),
          fixture.get("meta"), fixture.get("base"), fixture.get("href"), this, "nextFixture",
          "next-fixture"));
    } catch (final Exception e) {
      LOG.error("element could not be built: {}", e.getMessage(), e);
      // null-safe
    }
    return cards;
  }
}
