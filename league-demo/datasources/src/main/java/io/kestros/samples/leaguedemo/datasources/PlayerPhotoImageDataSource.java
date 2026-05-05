package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.AnchorTarget;
import io.kestros.cms.components.basic.api.content.KestrosImage;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.samples.league.api.models.Player;
import io.kestros.samples.league.api.services.LeagueDataService;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class PlayerPhotoImageDataSource extends BaseSlingModelDataSource implements KestrosImage {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  private Player getPlayer() {
    if (leagueDataService == null) return null;
    String slug = (String) getRequest().getAttribute("player-slug");
    return slug != null ? leagueDataService.getPlayer(slug) : null;
  }

  @Nullable
  @Override
  public String getImagePath() {
    Player p = getPlayer();
    return p != null ? p.getImageUrl() : null;
  }

  @Nullable
  @Override
  public String getAltText() {
    Player p = getPlayer();
    return p != null ? p.getFirstName() + " " + p.getLastName() : "";
  }

  @Nullable
  @Override
  public String getImageTitle() {
    return getAltText();
  }

  @Nullable @Override public String getHref() { return null; }
  @Nullable @Override public String getAriaLabel() { return null; }
  @Nullable @Override public String getAnchorTitle() { return null; }
  @Nullable @Override public String getRel() { return ""; }
  @Nullable @Override public String getAriaDescribedBy() { return ""; }
  @Nullable @Override public String getLang() { return ""; }
  @Nonnull @Override public AnchorTarget getTarget() { return AnchorTarget.SAME_WINDOW; }
  @Nullable @Override public String getCaption() { return null; }
}
