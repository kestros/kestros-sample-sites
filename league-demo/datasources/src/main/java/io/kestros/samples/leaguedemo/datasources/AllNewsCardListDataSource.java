package io.kestros.samples.leaguedemo.datasources;

import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.cms.components.basic.api.lists.KestrosCardList;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.cms.components.basic.core.content.card.KestrosCardImpl;
import io.kestros.cms.components.basic.core.content.heading.KestrosHeadingImpl;
import io.kestros.samples.league.api.models.Article;
import io.kestros.samples.league.api.services.LeagueDataService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class AllNewsCardListDataSource extends BaseContainerSlingModelDataSource
    implements KestrosCardList {

  @OSGiService
  @org.apache.sling.models.annotations.Optional
  private LeagueDataService leagueDataService;

  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    List<KestrosCard> cards = new ArrayList<>();
    if (leagueDataService == null) return cards;

    List<Article> articles = leagueDataService.getArticles();

    for (int i = 0; i < articles.size(); i++) {
      Article article = articles.get(i);
      try {
        String description = article.getTitle() + " -- " + article.getCategory()
            + " | " + article.getDate() + " -- " + article.getSummary();
        cards.add(new KestrosCardImpl(
            description,
            new KestrosHeadingImpl(article.getTitle(),
                "h3", this, "title", "news-title-" + i),
            null, null, this, "card", article.getId()));
      } catch (Exception ignored) {}
    }
    return cards;
  }
}
