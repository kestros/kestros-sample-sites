package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.cms.components.basic.api.lists.KestrosCardList;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.cms.components.basic.core.content.card.KestrosCardImpl;
import io.kestros.cms.components.basic.core.content.heading.KestrosHeadingImpl;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class VenuesCardListDataSource extends BaseContainerSlingModelDataSource
    implements KestrosCardList {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    List<KestrosCard> cards = new ArrayList<>();
    if (leagueDataService == null) return cards;

    int i = 0;
    for (Team team : leagueDataService.getTeams()) {
      try {
        StringBuilder desc = new StringBuilder();
        desc.append("Home of ").append(team.getName());
        if (team.getStadiumCapacity() > 0) {
          desc.append(" | Capacity: ").append(String.format("%,d", team.getStadiumCapacity()));
        }
        String surface = team.getSurface();
        if (surface != null && !surface.isEmpty()) {
          desc.append(" | Surface: ").append(surface);
        }
        String address = team.getVenueAddress();
        if (address != null && !address.isEmpty()) {
          desc.append(" | ").append(address);
        }
        int opened = team.getVenueOpened();
        if (opened > 0) {
          desc.append(" | Opened: ").append(opened);
        }

        cards.add(new KestrosCardImpl(
            desc.toString(),
            new KestrosHeadingImpl(team.getStadium(),
                "h3", this, "title", "venue-title-" + i),
            null, null, this, "card", "venue-" + team.getId()));
        i++;
      } catch (Exception ignored) {}
    }
    return cards;
  }
}
