package io.kestros.samples.leaguedemo.datasources;

/**
 * Small helper for rendering a count as an English word for readability — used by the
 * dynamic intro / hero copy that previously hand-spelt these out (e.g. "Seven seasons.").
 * Falls back to the numeric form for values outside the spelt range so larger counts
 * still render correctly.
 */
final class NumberWords {

  private NumberWords() {}

  static String words(int n) {
    switch (n) {
      case 0: return "Zero";
      case 1: return "One";
      case 2: return "Two";
      case 3: return "Three";
      case 4: return "Four";
      case 5: return "Five";
      case 6: return "Six";
      case 7: return "Seven";
      case 8: return "Eight";
      case 9: return "Nine";
      case 10: return "Ten";
      case 11: return "Eleven";
      case 12: return "Twelve";
      default: return String.valueOf(n);
    }
  }

  static String wordsLower(int n) {
    String w = words(n);
    return w.substring(0, 1).toLowerCase() + w.substring(1);
  }
}
