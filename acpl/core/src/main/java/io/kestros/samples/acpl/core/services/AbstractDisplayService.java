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

  /** Club brand color, gated for light backgrounds: too-light brand hues darken toward legibility. */
  protected String clubColorOnLight(final String hexInput) {
    final String hex = hexInput == null ? "" : hexInput;
    if (hex.length() != 7) {
      return "";
    }
    final int r = Integer.parseInt(hex.substring(1, 3), 16);
    final int g = Integer.parseInt(hex.substring(3, 5), 16);
    final int b = Integer.parseInt(hex.substring(5, 7), 16);
    final double lum = (0.2126 * r + 0.7152 * g + 0.0722 * b) / 255d;
    if (lum <= 0.55) {
      return hex;
    }
    // darken proportionally so light brands (sky blue, yellow) stay on-brand but legible
    final double f = 0.55 / lum;
    return String.format("#%02x%02x%02x", (int) (r * f), (int) (g * f), (int) (b * f));
  }

  /** Rough hue distance in degrees between two hex colors (for clash resolution). */
  protected double hueDistance(final String hexA, final String hexB) {
    final double ha = hue(hexA);
    final double hb = hue(hexB);
    final double d = Math.abs(ha - hb);
    return Math.min(d, 360 - d);
  }

  private double hue(final String hex) {
    final double r = Integer.parseInt(hex.substring(1, 3), 16) / 255d;
    final double g = Integer.parseInt(hex.substring(3, 5), 16) / 255d;
    final double b = Integer.parseInt(hex.substring(5, 7), 16) / 255d;
    final double max = Math.max(r, Math.max(g, b));
    final double min = Math.min(r, Math.min(g, b));
    if (max == min) {
      return 0;
    }
    final double d = max - min;
    double h;
    if (max == r) {
      h = ((g - b) / d) % 6;
    } else if (max == g) {
      h = (b - r) / d + 2;
    } else {
      h = (r - g) / d + 4;
    }
    return ((h * 60) + 360) % 360;
  }

  /**
   * Hero keyline color: the club primary unless it sits in the band's navy family (indistinct
   * against the hero), in which case the club secondary carries the identity.
   */
  protected String clubKeyline(final String primary, final String secondary) {
    final String bandNavy = "#193557";
    if (primary != null && primary.length() == 7
        && hueDistance(primary, bandNavy) < 35 && !clubColorOnLight(primary).isEmpty()) {
      final double lum = relativeLuminance(primary);
      if (lum < 0.35) {
        return secondary != null && secondary.length() == 7 ? clubColorOnLight(secondary) : "";
      }
    }
    return clubColorOnLight(primary == null ? "" : primary);
  }

  private double relativeLuminance(final String hex) {
    final int r = Integer.parseInt(hex.substring(1, 3), 16);
    final int g = Integer.parseInt(hex.substring(3, 5), 16);
    final int b = Integer.parseInt(hex.substring(5, 7), 16);
    return (0.2126 * r + 0.7152 * g + 0.0722 * b) / 255d;
  }
}