package org.basex.util.locale;

import static org.basex.util.Token.*;
import org.basex.util.TokenBuilder;

/**
 * English language formatter. Can be instantiated via {@link Formatter#get}.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-10, ISC License
 * @author Christian Gruen
 */
final class FormatterEN extends Formatter {
  /** Written numbers (1-20). */
  private static final byte[][] WORDS = tokens("", "One", "Two", "Three",
      "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven",
      "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
      "Seventeen", "Eighteen", "Nineteen");

  /** Written numbers (20-100). */
  private static final byte[][] WORDS10 = tokens("", "Ten", "Twenty", "Thirty",
      "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety");

  /** Written numbers (100, 1000, ...). */
  private static final byte[][] WORDS100 = tokens("Hundred", "Thousand",
      "Million", "Billion", "Trillion", "Quadrillion", "Quintillion");

  /** Units (100, 1000, ...). */
  private static final long[] UNITS100 = { 100, 1000, 1000000, 1000000000,
    1000000000000L, 1000000000000000L, 1000000000000000000L };

  /** Ordinal Numbers (1-20). */
  private static final byte[][] ORDINALS = tokens("", "First", "Second",
      "Third", "Fourth", "Fifth", "Sixth", "Seventh", "Eighth", "Ninth",
      "Tenth", "Eleventh", "Twelfth", "Thirteenth", "Fourteenth", "Fifteenth",
      "Sixteenth", "Seventeenth", "Eighteenth", "Nineteenth");

  /** Ordinal Numbers (20-100). */
  private static final byte[][] ORDINALS10 = tokens("", "Tenth", "Twentieth",
      "Thirtieth", "Fortieth", "Fiftieth", "Sixtieth", "Seventieth",
      "Eightieth", "Ninetieth");

  /** And. */
  private static final byte[] AND = token("and");

  /** Ordinal suffixes (st, nr, rd, th). */
  private static final byte[][] ORDSUFFIX = tokens("st", "nd", "rd", "th");

  @Override
  public byte[] word(final long n, final byte[] ord) {
    final TokenBuilder tb = new TokenBuilder();
    word(tb, n, ord);
    return tb.finish();
  }

  @Override
  public byte[] ordinal(final long n, final byte[] ord) {
    if(ord == null) return EMPTY;
    final int f = (int) (n % 10);
    return ORDSUFFIX[f > 0 && f < 4 && n % 100 / 10 != 1 ? f - 1 : 3];
  }

  /**
   * Creates a word character sequence for the specified number.
   * @param tb token builder
   * @param n number to be formatted
   * @param ord ordinal suffix
   */
  private void word(final TokenBuilder tb, final long n, final byte[] ord) {
    if(n < 20) {
      tb.add((ord != null ? ORDINALS : WORDS)[(int) n]);
    } else if(n < 100) {
      final int r = (int) (n % 10);
      if(r == 0) {
        tb.add((ord != null ? ORDINALS10 : WORDS10)[(int) n / 10]);
      } else {
        tb.add(WORDS10[(int) n / 10]).add('-');
        tb.add((ord != null ? ORDINALS : WORDS)[r]);
      }
    } else {
      for(int w = WORDS100.length - 1; w >= 0; w--) {
        if(addWord(tb, n, UNITS100[w], WORDS100[w], ord)) break;
      }
    }
  }

  /**
   * Adds a unit if the number is large enough.
   * @param tb token builder
   * @param n number
   * @param f factor
   * @param unit unit
   * @param ord ordinal numbering
   * @return true if word was added
   */
  private boolean addWord(final TokenBuilder tb, final long n, final long f,
      final byte[] unit, final byte[] ord) {

    final boolean ge = n >= f;
    if(ge) {
      word(tb, n / f, null);
      final long r = n % f;
      tb.add(' ').add(unit);
      if(ord != null) tb.add(ORDSUFFIX[3]);
      if(r > 0) {
        tb.add(' ');
        if(r < 100) tb.add(AND).add(' ');
      }
      word(tb, r, ord);
    }
    return ge;
  }
}