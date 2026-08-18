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

/**
 * Pre-match info for the match-preview page (unplayed matches). Thin adapter over
 * {@link MatchService#getPreviewInfo}; match id from the {@code match} route param.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class MatchPreviewCardListDataSource extends AbstractLeagueCardListDataSource {

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
    final Map<String, String> info =
        matchService.getPreviewInfo(getParam("match", ""), contextPath());
    if (info.isEmpty()) {
      return cards;
    }
    try {
      cards.add(new SyntheticMatchPreviewCard(info, this, "preview", "match-preview"));
    } catch (final Exception e) {
      // null-safe
    }
    return cards;
  }
}
