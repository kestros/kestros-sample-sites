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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
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

    // Visually-rich articles (with imageUrl) first, then most recent. Same ordering rule
    // as NewsCardListDataSource so the homepage previews and full /news.html listing agree.
    List<Article> articles = leagueDataService.getArticles().stream()
        .sorted(Comparator
            .comparing((Article a) -> StringUtils.isNotBlank(a.getImageUrl()))
            .reversed()
            .thenComparing(Article::getDate, Comparator.nullsLast(Comparator.reverseOrder())))
        .collect(Collectors.toList());

    for (int i = 0; i < articles.size(); i++) {
      Article article = articles.get(i);
      try {
        String description = article.getCategory() + " | " + article.getDate()
            + " -- " + article.getSummary();

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
            description,
            new KestrosHeadingImpl(article.getTitle(),
                "h3", this, "title", "titleElement"),
            image, null, this, "card", article.getId()));
      } catch (Exception ignored) {}
    }
    return cards;
  }
}
