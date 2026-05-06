package io.kestros.samples.leaguedemo.datasources;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Small helper for formatting dates supplied as ISO strings (yyyy-MM-dd) by the data
 * model into the human-readable forms used across the site. Falls back to the raw input
 * if parsing fails so callers can pass through cells safely.
 */
final class Dates {

  private Dates() {}

  private static final DateTimeFormatter MEDIUM =
      DateTimeFormatter.ofPattern("d MMM yyyy");

  private static final DateTimeFormatter SHORT =
      DateTimeFormatter.ofPattern("d MMM");

  /** "2025-12-23" -> "23 Dec 2025". Returns the raw input when not parseable. */
  static String medium(String iso) {
    if (iso == null || iso.isEmpty()) return iso;
    try {
      return LocalDate.parse(iso).format(MEDIUM);
    } catch (DateTimeParseException e) {
      return iso;
    }
  }

  /** "2025-12-23" -> "23 Dec". Returns the raw input when not parseable. */
  static String shortMonth(String iso) {
    if (iso == null || iso.isEmpty()) return iso;
    try {
      return LocalDate.parse(iso).format(SHORT);
    } catch (DateTimeParseException e) {
      return iso;
    }
  }
}
