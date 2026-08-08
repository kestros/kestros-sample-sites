package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.samples.acpl.api.services.MatchService;
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
 * Head-to-head recent meetings for a match page. Thin adapter over
 * {@link MatchService#getHeadToHeadRows}; match id from the {@code match} route param. Rows render
 * through the {@code result-row} card layout.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class MatchH2hCardListDataSource extends AbstractLeagueCardListDataSource {

  private static final Logger LOG = LoggerFactory.getLogger(MatchH2hCardListDataSource.class);

  @OSGiService
  @Optional
  private MatchService matchService;

  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    final List<KestrosCard> cards = new ArrayList<>();
    if (matchService == null) {
      return cards;
    }
    int i = 0;
    for (final Map<String, String> m
        : matchService.getHeadToHeadRows(getParam("match", ""), contextPath())) {
      try {
        cards.add(new SyntheticMatchCard("result-row", m.get("base"), m.get("href"),
            m.get("homeSlug"), m.get("homeShort"), m.get("homeName"), m.get("awaySlug"),
            m.get("awayShort"), m.get("awayName"), m.get("mid"), this, "h2h", "h2h-" + i)
            .withSubText(m.get("subText")));
        i++;
      } catch (final Exception e) {
        LOG.error("MatchH2hCardListDataSource: {}", e.getMessage());
        // null-safe
      }
    }
    return cards;
  }
}
