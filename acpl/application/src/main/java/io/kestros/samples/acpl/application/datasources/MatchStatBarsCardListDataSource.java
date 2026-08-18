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
 * Match statistics as mirrored home/away bars. Thin adapter over
 * {@link MatchService#getMatchStatRows} (which supplies precomputed percentages); match id from the
 * {@code match} route param.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class MatchStatBarsCardListDataSource extends AbstractLeagueCardListDataSource {

  private static final Logger LOG = LoggerFactory.getLogger(MatchStatBarsCardListDataSource.class);

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
    if (!header.isEmpty()) {
      try {
        cards.add(new SyntheticStatLegendCard(header.get("homeName"), header.get("awayName"),
            this, "stat", "stat-legend"));
      } catch (final Exception e) {
        LOG.error("element could not be built: {}", e.getMessage(), e);
        // null-safe
      }
    }
    int i = 0;
    for (final Map<String, String> s : matchService.getMatchStatRows(getParam("match", ""))) {
      try {
        cards.add(new SyntheticStatBarCard(s.get("label"), s.get("h"), s.get("a"),
            s.get("hPct"), s.get("aPct"), this, "stat", "stat-" + i)
            .withColors(s.get("homeColor"), s.get("awayColor")));
        i++;
      } catch (final Exception e) {
        LOG.error("row skipped: {}", e.getMessage(), e);
        // null-safe
      }
    }
    return cards;
  }
}
