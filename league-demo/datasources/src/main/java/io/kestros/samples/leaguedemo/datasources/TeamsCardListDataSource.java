package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.AnchorTarget;
import io.kestros.cms.components.basic.api.content.KestrosButton;
import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.cms.components.basic.api.content.KestrosImage;
import io.kestros.cms.components.basic.api.lists.KestrosCardList;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.cms.components.basic.core.content.button.KestrosButtonImpl;
import io.kestros.cms.components.basic.core.content.buttongroup.KestrosButtonGroupImpl;
import io.kestros.cms.components.basic.core.content.card.KestrosCardImpl;
import io.kestros.cms.components.basic.core.content.heading.KestrosHeadingImpl;
import io.kestros.cms.components.basic.core.content.image.KestrosImageImpl;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class TeamsCardListDataSource extends BaseContainerSlingModelDataSource
    implements KestrosCardList {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    List<KestrosCard> cards = new ArrayList<>();
    if (leagueDataService == null) return cards;

    // Resolve the site path from the current resource
    String sitePath = "";
    Resource r = getResource();
    while (r != null) {
      if (r.getPath().startsWith("/content/sites/") && r.getPath().split("/").length == 4) {
        sitePath = r.getPath();
        break;
      }
      r = r.getParent();
    }

    int i = 0;
    for (Team team : leagueDataService.getTeams()) {
      try {
        // Image
        KestrosImage image = null;
        if (StringUtils.isNotBlank(team.getLogoUrl())) {
          try {
            image = new KestrosImageImpl(
                team.getLogoUrl(), team.getName(), null, null,
                null, null, null, AnchorTarget.SAME_WINDOW,
                this, "image", "imageElement", null);
          } catch (Exception ignored) {}
        }

        // Button linking to team page
        KestrosButtonGroupImpl buttonGroup = null;
        if (StringUtils.isNotBlank(sitePath)) {
          try {
            String teamHref = sitePath + "/team/" + team.getId() + ".html";
            List<KestrosButton> buttons = Arrays.asList(
                new KestrosButtonImpl("View team", teamHref, null,
                    AnchorTarget.SAME_WINDOW, null, null, null, null, false,
                    this, "button", "buttonElement"));
            buttonGroup = new KestrosButtonGroupImpl(buttons,
                this, "buttonGroup", "buttonGroupElement");
          } catch (Exception ignored) {}
        }

        StringBuilder desc = new StringBuilder();
        if (team.getNickname() != null) {
          desc.append("\"").append(team.getNickname()).append("\" | ");
        }
        desc.append(team.getStadium());
        if (team.getStadiumCapacity() > 0) {
          desc.append(" (").append(String.format("%,d", team.getStadiumCapacity())).append(")");
        }
        desc.append(" | Est. ").append(team.getFounded());
        if (team.getManager() != null) {
          desc.append(" | Manager: ").append(team.getManager());
        }

        cards.add(new KestrosCardImpl(
            desc.toString(),
            new KestrosHeadingImpl(team.getName(), "h3", this, "title", "titleElement"),
            image, buttonGroup,
            this, "card", team.getId()));
        i++;
      } catch (Exception e) {
        // skip on error
      }
    }
    return cards;
  }
}
