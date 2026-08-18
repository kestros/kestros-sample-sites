package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The featured (hero) story card — same data as {@link SyntheticStoryCard} but rendered by the larger
 * {@code featured-story} card layout instead of the compact {@code story-card}.
 */
public class SyntheticFeaturedStoryCard extends SyntheticStoryCard {

  public SyntheticFeaturedStoryCard(@Nonnull final String title, @Nonnull final String excerpt,
      @Nonnull final String image, @Nonnull final String href, @Nonnull final String category,
      @Nonnull final String byline, @Nonnull final BaseSlingModelDataSource dataSource,
      @Nonnull final String resourcePrefix, @Nullable final String forcedResourceName)
      throws ComponentConfigurationException {
    super(title, excerpt, image, href, category, byline, dataSource, resourcePrefix,
        forcedResourceName);
  }

  @Override
  public String getLayout() {
    return "featured-story";
  }
}
