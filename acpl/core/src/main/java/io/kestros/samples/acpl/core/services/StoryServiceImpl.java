package io.kestros.samples.acpl.core.services;

import io.kestros.samples.acpl.api.services.LeagueDataService;
import io.kestros.samples.acpl.api.services.StoryService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/** Display-ready story data, backed by {@link LeagueDataService}. */
@Component(service = StoryService.class, immediate = true)
public class StoryServiceImpl extends AbstractDisplayService implements StoryService {

  @Reference
  private LeagueDataService leagueDataService;

  private Map<String, String> card(final Map<String, Object> s, final String base) {
    final Map<String, String> card = new LinkedHashMap<>();
    card.put("headline", str(s.get("headline")));
    card.put("excerpt", str(s.get("dek")));
    card.put("image", base + "/assets/" + str(s.get("image")));
    card.put("href", base + "/stories/" + str(s.get("slug")) + ".html");
    card.put("category", str(s.get("category")));
    card.put("byline", str(s.get("author")) + " · " + formatDateLong(str(s.get("date"))));
    return card;
  }

  /** Stories sorted newest-first by publish date (source JSON is grouped by category, not date). */
  private List<Map<String, Object>> storiesNewestFirst() {
    final List<Map<String, Object>> stories = new ArrayList<>(leagueDataService.getStories());
    stories.sort((a, b) -> str(b.get("date")).compareTo(str(a.get("date"))));
    return stories;
  }

  @Nonnull
  @Override
  public List<Map<String, String>> getStoryCards(final String contextPath, final int limit) {
    final List<Map<String, String>> cards = new ArrayList<>();
    final String base = siteRoot(contextPath);
    for (final Map<String, Object> s : storiesNewestFirst()) {
      if (limit > 0 && cards.size() >= limit) {
        break;
      }
      cards.add(card(s, base));
    }
    return cards;
  }

  @Nonnull
  @Override
  public List<Map<String, String>> getStoryCardsExcludingFeatured(final String contextPath,
      final int limit) {
    final List<Map<String, String>> cards = new ArrayList<>();
    final String base = siteRoot(contextPath);
    final String featuredSlug = featuredStory() == null ? "" : str(featuredStory().get("slug"));
    for (final Map<String, Object> s : storiesNewestFirst()) {
      if (limit > 0 && cards.size() >= limit) {
        break;
      }
      if (str(s.get("slug")).equals(featuredSlug)) {
        continue;
      }
      cards.add(card(s, base));
    }
    return cards;
  }

  private Map<String, Object> featuredStory() {
    final List<Map<String, Object>> stories = leagueDataService.getStories();
    if (stories.isEmpty()) {
      return null;
    }
    return stories.stream()
        .filter(s -> Boolean.TRUE.equals(s.get("featured")))
        .findFirst()
        .orElse(stories.get(0));
  }

  @Nonnull
  @Override
  public Map<String, String> getFeaturedStoryCard(final String contextPath) {
    final List<Map<String, Object>> stories = leagueDataService.getStories();
    if (stories.isEmpty()) {
      return new LinkedHashMap<>();
    }
    final Map<String, Object> story = stories.stream()
        .filter(s -> Boolean.TRUE.equals(s.get("featured")))
        .findFirst()
        .orElse(stories.get(0));
    return card(story, siteRoot(contextPath));
  }

  @Nonnull
  @Override
  public List<Map<String, String>> getRelatedStoryCards(final String storySlug,
      final String contextPath, final int limit) {
    final String base = siteRoot(contextPath);
    final Set<String> clubs = new LinkedHashSet<>();
    for (final Map<String, Object> r : leagueDataService.getStandings()) {
      clubs.add(str(r.get("club")));
    }
    final Set<String> currentTeams = teamsIn(storySlug, clubs);

    // Prefer stories sharing a club with the current one, then fill with other recent stories, so
    // the Related Stories section is never empty (e.g. features/analysis with no club in the slug).
    final List<Map<String, Object>> matched = new ArrayList<>();
    final List<Map<String, Object>> others = new ArrayList<>();
    for (final Map<String, Object> s : leagueDataService.getStories()) {
      final String slug = str(s.get("slug"));
      if (slug.equals(storySlug)) {
        continue;
      }
      final Set<String> t = teamsIn(slug, clubs);
      t.retainAll(currentTeams);
      if (!currentTeams.isEmpty() && !t.isEmpty()) {
        matched.add(s);
      } else {
        others.add(s);
      }
    }
    final List<Map<String, Object>> selected = new ArrayList<>(matched);
    selected.addAll(others);

    final List<Map<String, String>> cards = new ArrayList<>();
    for (final Map<String, Object> s : selected) {
      if (limit > 0 && cards.size() >= limit) {
        break;
      }
      cards.add(card(s, base));
    }
    return cards;
  }

  /** Club slugs mentioned in a story slug (e.g. {@code mw17-bayview-harborside}). */
  private static Set<String> teamsIn(final String storySlug, final Set<String> clubs) {
    final Set<String> teams = new LinkedHashSet<>();
    final String slug = storySlug == null ? "" : storySlug;
    final String rest = slug.contains("-") ? slug.substring(slug.indexOf('-') + 1) : slug;
    for (final String c : clubs) {
      if (rest.contains(c)) {
        teams.add(c);
      }
    }
    return teams;
  }
}
