package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.cms.components.basic.api.lists.KestrosCardList;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.cms.components.basic.core.content.card.KestrosCardImpl;
import io.kestros.cms.components.basic.core.content.heading.KestrosHeadingImpl;
import io.kestros.samples.league.api.models.Sponsor;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class SponsorsCardListDataSource extends BaseContainerSlingModelDataSource
    implements KestrosCardList {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    List<KestrosCard> cards = new ArrayList<>();
    if (leagueDataService == null) return cards;

    for (int i = 0; i < leagueDataService.getSponsors().size(); i++) {
      Sponsor sponsor = leagueDataService.getSponsors().get(i);
      try {
        // Card titleElement doesn't render through synthetic adaptation;
        // include name in description text.
        cards.add(new KestrosCardImpl(
            sponsor.getName() + " -- " + sponsor.getCategory() + " -- " + sponsor.getDescription(),
            new KestrosHeadingImpl(sponsor.getName(),
                "h4", this, "title", "sponsor-title-" + i),
            null, null, this, "card", sponsor.getId()));
      } catch (Exception ignored) {}
    }
    return cards;
  }
}
