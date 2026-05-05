package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.AnchorTarget;
import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.cms.components.basic.api.content.KestrosImage;
import io.kestros.cms.components.basic.api.lists.KestrosCardList;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.cms.components.basic.core.content.card.KestrosCardImpl;
import io.kestros.cms.components.basic.core.content.heading.KestrosHeadingImpl;
import io.kestros.cms.components.basic.core.content.image.KestrosImageImpl;
import io.kestros.samples.league.api.models.Article;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class NewsCardListDataSource extends BaseContainerSlingModelDataSource
    implements KestrosCardList {

  private static final int DEFAULT_MAX_ARTICLES = 4;

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  private int getMaxArticles() {
    return getResource().getValueMap().get("maxRows", DEFAULT_MAX_ARTICLES);
  }

  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    List<KestrosCard> cards = new ArrayList<>();
    if (leagueDataService == null) return cards;

    List<Article> articles = leagueDataService.getArticles();
    int count = Math.min(articles.size(), getMaxArticles());

    for (int i = 0; i < count; i++) {
      Article article = articles.get(i);
      try {
        StringBuilder desc = new StringBuilder();
        if (article.getCategory() != null) {
          desc.append(article.getCategory()).append(" | ").append(article.getDate()).append(" -- ");
        }
        desc.append(article.getSummary());

        KestrosImage image = null;
        if (StringUtils.isNotBlank(article.getImageUrl())) {
          try {
            image = new KestrosImageImpl(
                article.getImageUrl(), article.getTitle(), null, null,
                null, null, null, AnchorTarget.SAME_WINDOW,
                this, "image", "imageElement", null);
          } catch (Exception ignored) {}
        }

        cards.add(new KestrosCardImpl(
            desc.toString(),
            new KestrosHeadingImpl(article.getTitle(),
                "h3", this, "title", "titleElement"),
            image, null, this, "card", article.getId()));
      } catch (Exception ignored) {}
    }
    return cards;
  }
}
