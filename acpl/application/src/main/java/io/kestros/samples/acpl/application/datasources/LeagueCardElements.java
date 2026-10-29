package io.kestros.samples.acpl.application.datasources;

import io.kestros.cms.components.basic.api.content.AnchorTarget;
import io.kestros.cms.components.basic.api.content.KestrosButton;
import io.kestros.cms.components.basic.api.content.KestrosButtonGroup;
import io.kestros.cms.components.basic.api.content.KestrosHeading;
import io.kestros.cms.components.basic.api.content.KestrosImage;
import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import io.kestros.cms.components.basic.core.content.button.KestrosButtonImpl;
import io.kestros.cms.components.basic.core.content.buttongroup.KestrosButtonGroupImpl;
import io.kestros.cms.components.basic.core.content.heading.KestrosHeadingImpl;
import io.kestros.cms.components.basic.core.content.image.KestrosImageImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builds the title / image / button-group elements that the stock card layout renders.
 *
 * <p>The ACPL cards carry their data as plain strings, because the ACPL card layouts read them as
 * {@code properties.*}. A UI framework that ships no card layout of its own - ACPL Framework 0.0.1,
 * which the p3 home page uses - falls through to the stock card layout instead, and that layout
 * renders {@code getTitle()}, {@code getImage()} and {@code getButtonGroup()}. Those bridge to the
 * three element getters, so a card with the string data but no elements renders as an empty box.
 *
 * <p>Each factory logs and returns null rather than throwing when the element cannot be built, which
 * leaves the card rendering exactly as it did before instead of failing the whole list.
 */
final class LeagueCardElements {

  private static final Logger LOG = LoggerFactory.getLogger(LeagueCardElements.class);

  private LeagueCardElements() {
  }

  /**
   * Card title heading, or null when the card has no title text.
   *
   * @param text heading text
   * @param dataSource data source the card was built by
   * @return Heading element or null.
   */
  @Nullable
  static KestrosHeading heading(@Nullable final String text,
      @Nonnull final BaseSlingModelDataSource dataSource) {
    if (StringUtils.isBlank(text)) {
      return null;
    }
    try {
      return new KestrosHeadingImpl(text, "h3", dataSource, "title", "titleElement");
    } catch (final ComponentConfigurationException e) {
      LOG.error("card heading skipped for text '{}': {}", text, e.getMessage(), e);
      return null;
    }
  }

  /**
   * Card image, or null when the card has no image path.
   *
   * @param imagePath image path or URL
   * @param altText alternative text
   * @param href link the image points at, may be null
   * @param dataSource data source the card was built by
   * @return Image element or null.
   */
  @Nullable
  static KestrosImage image(@Nullable final String imagePath, @Nullable final String altText,
      @Nullable final String href, @Nonnull final BaseSlingModelDataSource dataSource) {
    if (StringUtils.isBlank(imagePath)) {
      return null;
    }
    try {
      return new KestrosImageImpl(imagePath, StringUtils.defaultString(altText), null, null, href,
          null, null, AnchorTarget.SAME_WINDOW, dataSource, "image", "imageElement", null);
    } catch (final ComponentConfigurationException e) {
      LOG.error("card image skipped for path '{}': {}", imagePath, e.getMessage(), e);
      return null;
    }
  }

  /**
   * Single-button group pointing at the card's target, or null when the card has no link.
   *
   * @param text button text
   * @param href button target
   * @param dataSource data source the card was built by
   * @return Button group element or null.
   */
  @Nullable
  static KestrosButtonGroup buttonGroup(@Nullable final String text, @Nullable final String href,
      @Nonnull final BaseSlingModelDataSource dataSource) {
    if (StringUtils.isBlank(href)) {
      return null;
    }
    try {
      final KestrosButton button = new KestrosButtonImpl(
          StringUtils.defaultIfBlank(text, "Read more"), href, null, AnchorTarget.SAME_WINDOW, null,
          null, null, null, false, dataSource, "button", "buttonElement");
      final List<KestrosButton> buttons = Collections.singletonList(button);
      return new KestrosButtonGroupImpl(buttons, dataSource, "buttonGroup", "buttonGroupElement");
    } catch (final ComponentConfigurationException e) {
      LOG.error("card button group skipped for href '{}': {}", href, e.getMessage(), e);
      return null;
    }
  }

  /**
   * One button per match, each labelled with the match and pointing at its match centre, or null
   * when no match has a link. Matches are the row maps of a matchweek group ({@code homeName},
   * {@code awayName}, {@code href}, and {@code score} for a result or {@code time} for a fixture).
   *
   * @param matches matchweek rows
   * @param dataSource data source the card was built by
   * @return Button group element or null.
   */
  @Nullable
  static KestrosButtonGroup matchButtons(@Nullable final List<Map<String, String>> matches,
      @Nonnull final BaseSlingModelDataSource dataSource) {
    if (matches == null) {
      return null;
    }
    try {
      final List<KestrosButton> buttons = new ArrayList<>();
      for (final Map<String, String> m : matches) {
        final String href = m.get("href");
        if (StringUtils.isBlank(href)) {
          continue;
        }
        // Each button needs its own resource name: under one shared name the synthetic children
        // collapse and the group renders only its first match.
        buttons.add(new KestrosButtonImpl(matchLabel(m), href, null, AnchorTarget.SAME_WINDOW,
            null, null, null, null, false, dataSource, "button", "buttonElement-" + buttons.size()));
      }
      if (buttons.isEmpty()) {
        return null;
      }
      return new KestrosButtonGroupImpl(buttons, dataSource, "buttonGroup", "buttonGroupElement");
    } catch (final ComponentConfigurationException e) {
      LOG.error("matchweek button group skipped for {} matches: {}", matches.size(),
          e.getMessage(), e);
      return null;
    }
  }

  /**
   * "Home 2 – 1 Away" for a result, "Home v Away · 15:00" for a fixture.
   *
   * @param m matchweek row
   * @return Match label.
   */
  @Nonnull
  static String matchLabel(@Nonnull final Map<String, String> m) {
    final String home = StringUtils.defaultString(m.get("homeName"));
    final String away = StringUtils.defaultString(m.get("awayName"));
    final String score = m.get("score");
    if (StringUtils.isNotBlank(score)) {
      return home + " " + score + " " + away;
    }
    final String time = m.get("time");
    return home + " v " + away + (StringUtils.isNotBlank(time) ? " · " + time : "");
  }
}
