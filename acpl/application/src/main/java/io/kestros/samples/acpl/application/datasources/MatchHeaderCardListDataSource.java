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
 * Match-detail header, keyed on the {@code match} route param (featured match when absent). Thin
 * adapter over {@link MatchService#getMatchHeader}; one {@link SyntheticMatchHeaderCard} rendered by
 * the {@code match-header} card layout.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class MatchHeaderCardListDataSource extends AbstractLeagueCardListDataSource {

  private static final Logger LOG = LoggerFactory.getLogger(MatchHeaderCardListDataSource.class);

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
    final Map<String, String> header =
        matchService.getMatchHeader(getParam("match", ""), contextPath());
    if (header.isEmpty()) {
      return cards;
    }
    try {
      cards.add(new SyntheticMatchHeaderCard(header.get("homeSlug"), header.get("homeName"),
          header.get("awaySlug"), header.get("awayName"), header.get("score"), header.get("meta"),
          header.get("base"), this, "matchHeader", "match-header")
          .withReportHref(header.get("reportHref")));
    } catch (final Exception e) {
      LOG.error("MatchHeaderCardListDataSource: {}", e.getMessage());
      // null-safe
    }
    return cards;
  }
}
