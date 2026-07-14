package io.kestros.samples.acpl.core.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Shared display helpers for the ACPL domain services — site-root derivation, date formatting,
 * ordinals, portrait paths. Keeps the datasources free of computation.
 */
abstract class AbstractDisplayService {

  private static final Pattern SITE_ROOT = Pattern.compile("^(/content/sites/[^/]+)");

  /** League-site root (e.g. {@code /content/sites/acpl}) from any content path within the site. */
  protected String siteRoot(final String contextPath) {
    final Matcher m = SITE_ROOT.matcher(contextPath == null ? "" : contextPath);
    return m.find() ? m.group(1) : (contextPath == null ? "" : contextPath);
  }

  /** {@code 2026-02-08} → {@code 8 February 2026}; input unchanged if unparseable. */
  protected static String formatDateLong(final String iso) {
    return formatDate(iso, "d MMMM yyyy");
  }

  /** {@code 2026-02-19} → {@code 19 Feb}; input unchanged if unparseable. */
  protected static String formatDateShort(final String iso) {
    return formatDate(iso, "d MMM");
  }

  /** {@code 2026-02-07} → {@code Sat, 7 Feb 2026}; input unchanged if unparseable. */
  protected static String formatDateResult(final String iso) {
    return formatDate(iso, "EEE, d MMM yyyy");
  }

  private static String formatDate(final String iso, final String pattern) {
    try {
      return LocalDate.parse(iso).format(DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH));
    } catch (final Exception e) {
      return iso;
    }
  }

  /** {@code 1 → 1st}, {@code 2 → 2nd}, {@code 11 → 11th}, etc. */
  protected static String ordinal(final int n) {
    if (n <= 0) {
      return String.valueOf(n);
    }
    if (n % 100 >= 11 && n % 100 <= 13) {
      return n + "th";
    }
    switch (n % 10) {
      case 1:
        return n + "st";
      case 2:
        return n + "nd";
      case 3:
        return n + "rd";
      default:
        return n + "th";
    }
  }

  /** Deterministic placeholder portrait (1 of 20 uniform avatars), keyed by player name. */
  protected static String portraitFor(final String siteRoot, final String name) {
    final int idx = Math.abs(name.hashCode()) % 20 + 1;
    return siteRoot + "/assets/portraits/p" + idx + ".svg";
  }

  protected static String str(final Object o) {
    return o == null ? "" : String.valueOf(o);
  }

  protected static int asInt(final Object o) {
    if (o instanceof Number) {
      return ((Number) o).intValue();
    }
    try {
      return Integer.parseInt(String.valueOf(o));
    } catch (final NumberFormatException e) {
      return 0;
    }
  }
}
