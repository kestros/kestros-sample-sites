package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.AnchorTarget;
import io.kestros.cms.components.basic.api.content.KestrosImage;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.samples.league.api.models.Team;
import io.kestros.samples.league.api.services.LeagueDataService;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class TeamCrestImageDataSource extends BaseSlingModelDataSource implements KestrosImage {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  private Team getTeam() {
    if (leagueDataService == null) return null;
    String slug = (String) getRequest().getAttribute("team-slug");
    return slug != null ? leagueDataService.getTeam(slug) : null;
  }

  @Nullable
  @Override
  public String getImagePath() {
    Team t = getTeam();
    return t != null ? t.getLogoUrl() : null;
  }

  @Nullable
  @Override
  public String getAltText() {
    Team t = getTeam();
    return t != null ? t.getName() + " crest" : "";
  }

  @Nullable
  @Override
  public String getImageTitle() {
    Team t = getTeam();
    return t != null ? t.getName() : "";
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
