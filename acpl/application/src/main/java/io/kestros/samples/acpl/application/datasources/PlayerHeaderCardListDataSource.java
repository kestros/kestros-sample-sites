package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.samples.acpl.api.services.PlayerService;
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
 * Player-detail header, keyed on the {@code player} route param. Thin adapter over
 * {@link PlayerService#getPlayerHeader}; one {@link SyntheticPlayerHeaderCard} rendered by the
 * {@code player-header} card layout.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class PlayerHeaderCardListDataSource extends AbstractLeagueCardListDataSource {

  @OSGiService
  @Optional
  private PlayerService playerService;

  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    final List<KestrosCard> cards = new ArrayList<>();
    if (playerService == null) {
      return cards;
    }
    final Map<String, String> header =
        playerService.getPlayerHeader(getParam("player", "harborside-mateo-reyes"), contextPath());
    if (header.isEmpty()) {
      return cards;
    }
    try {
      cards.add(new SyntheticPlayerHeaderCard(header.get("name"), header.get("club"),
          header.get("clubHref"), header.get("number"), header.get("pos"), header.get("meta"),
          header.get("photo"), header.get("base"), this, "playerHeader", "player-header").withClubColor(header.get("clubColor")));
    } catch (final Exception e) {
      // null-safe
    }
    return cards;
  }
}
