package io.kestros.samples.acpl.api.services;

import java.util.List;
import java.util.Map;

/** Display-ready story data for the news card lists and story pages. */
public interface StoryService {

  /** Story cards, newest-first order as loaded: headline, excerpt, image, href, category, byline. */
  List<Map<String, String>> getStoryCards(String contextPath, int limit);

  /** Same as {@link #getStoryCards} but omitting the featured story (for grids below a featured hero). */
  List<Map<String, String>> getStoryCardsExcludingFeatured(String contextPath, int limit);

  /** The featured story (falls back to the first story). Same keys as story cards; empty if none. */
  Map<String, String> getFeaturedStoryCard(String contextPath);

  /** Stories related to {@code storySlug} (shared club, else other recent stories). Same keys. */
  List<Map<String, String>> getRelatedStoryCards(String storySlug, String contextPath, int limit);
}
