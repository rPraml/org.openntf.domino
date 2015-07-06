package org.openntf.formula;

import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.openntf.domino.commons.IDateTime;
import org.openntf.formula.impl.DateTimeImpl;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.NumberFormat;

public class Formatter {

	/*----------------------------------------------------------------------------*/
	public static class LotusDateTimeOptions {
		public final static int D_YMD = 0;		// D0
		public final static int D_YMD_YOPT = 1;	// D1
		public final static int D_MD = 2;		// D1
		public final static int D_YM = 3;		// D3
		public final static int T_HMS = 0;		// T0
		public final static int T_HM = 1;		// T1
		public final static int Z_CONV = 0;		// Z0
		public final static int Z_DISP_OPT = 1;	// Z1
		public final static int Z_DISP_ALW = 2;	// Z2
		public final static int S_D_ONLY = 0;	// S0
		public final static int S_T_ONLY = 1;	// S1
		public final static int S_DT = 2;		// S2
		public final static int S_DT_TY = 3;	// S3

		public int dOption = -1;
		public int tOption = -1;
		public int zOption = -1;
		public int sOption = -1;

		public boolean nothingSet() {
			return (dOption + tOption + zOption + sOption == -4);
		}

		public String dOptToStr() {
			return "D" + dOption;
		}

		public String tOptToStr() {
			return "T" + tOption;
		}

		public String zOptToStr() {
			return "Z" + zOption;
		}

		public String sOptToStr() {
			return "S" + sOption;
		}
	}

	/*----------------------------------------------------------------------------*/
	public static class LotusNumberOptions {
		public boolean useGrouping = false;
		public char format = 0;
		public int fractionDigits = -1;
		public boolean negativeAsParentheses = false;

		public void setDefault() {
			format = 'G';
		}
	}

	private Locale locale;

	protected Formatter(final Locale loc) {
		locale = loc;
	}

	/**
	 * Parses the string and returns a {@link IDateTime}. It uses the locale of the Formatter
	 */
	public IDateTime parseDateTime(final String text, final boolean parseLenient) {
		IDateTime ret = new DateTimeImpl();
		ret.parse(text, locale, parseLenient);
		return ret;
	}

	/**
	 * Parses the string and returns a {@link Number}. It uses the locale of the Formatter
	 */
	public Number parseNumber(String image, final boolean lenient) {
		image = image.trim();
		if (!image.isEmpty()) {
			String toParse = image;
			if (toParse.length() > 1 && toParse.charAt(0) == '+')
				toParse = toParse.substring(1);
			NumberFormat nf = NumberFormat.getNumberInstance(locale);
			ParsePosition p = new ParsePosition(0);
			Number ret = nf.parse(toParse, p);
			int errIndex = p.getErrorIndex();
			//System.out.println("Ind=" + index + " ErrInd=" + errIndex);
			if (errIndex == -1) {
				if (p.getIndex() >= toParse.length() || lenient)
					return ret;
			} else if (errIndex != 0 && lenient)
				return ret;
		}
		throw new IllegalArgumentException("Illegal number string '" + image + "'");
	}

	/**
	 * Formats the {@link IDateTime} with the locale of the formatter
	 */
	public String formatDateTime(final IDateTime sdt, final int dateStyle, final int timeStyle) {
		return sdt.toString(locale, dateStyle, timeStyle);
	}

	public String formatDateTime(final IDateTime sdt, final LotusDateTimeOptions ldto) {
		if (ldto.nothingSet())
			return formatDateTime(sdt, DateFormat.MEDIUM, DateFormat.MEDIUM);

		String notSupported = "";
		if (ldto.dOption != -1 && ldto.dOption != LotusDateTimeOptions.D_YMD)
			notSupported += "," + ldto.dOptToStr();
		if (ldto.zOption != -1)
			notSupported += "," + ldto.zOptToStr();
		if (ldto.sOption == LotusDateTimeOptions.S_DT_TY)
			notSupported += "," + ldto.sOptToStr();
		if (!notSupported.isEmpty())
			throw new UnsupportedOperationException("Not yet supported formatting option(s): " + notSupported.substring(1));

		if (ldto.sOption == LotusDateTimeOptions.S_D_ONLY || sdt.isAnyTime())
			return sdt.getDateOnly(locale);

		if (ldto.sOption == LotusDateTimeOptions.S_T_ONLY || sdt.isAnyDate()) {
			if (ldto.tOption == LotusDateTimeOptions.T_HM) {
				return sdt.getTimeOnly(locale, DateFormat.SHORT);
			} else {
				return sdt.getTimeOnly(locale, DateFormat.MEDIUM);
			}
		}
		if (ldto.tOption == LotusDateTimeOptions.T_HM) {
			return sdt.toString(locale, DateFormat.MEDIUM, DateFormat.SHORT);
		} else {
			return sdt.toString(locale, DateFormat.MEDIUM, DateFormat.MEDIUM);
		}
	}

	public String formatNumber(final Number n, final LotusNumberOptions lno) {
		NumberFormat nf;
		/*
		 * It would have been more convenient to use NumberFormat.getInstance(locale, style),
		 * but this method is private in com.ibm.icu_3.8.1.v20120530.jar.
		 * (Seems to be public as of ICU 4.2.)
		 */
		if (lno.format == 'C')
			nf = NumberFormat.getCurrencyInstance(locale);
		else if (lno.format == 'S')
			nf = NumberFormat.getScientificInstance(locale);
		else if (lno.format == '%')
			nf = NumberFormat.getPercentInstance(locale);
		else
			nf = NumberFormat.getNumberInstance(locale);
		nf.setGroupingUsed(lno.useGrouping);
		nf.setMaximumIntegerDigits(1000);
		if (lno.fractionDigits != -1) {
			nf.setMinimumFractionDigits(lno.fractionDigits);
			nf.setMaximumFractionDigits(lno.fractionDigits);
		} else
			nf.setMaximumFractionDigits(1000);
		String ret = nf.format(n);
		do {
			if (lno.format != 'G' || ret.length() <= 15)
				break;
			/*
			 * In this case, Lotus implicitly switches to scientific style.
			 * When useGrouping is in effect, the limit decreases from 15 to 12 in Lotus
			 * (i.e. the grouping bytes are likewise counted), but we are not going to
			 *  imitate this strange behaviour.
			 */
			String tester = ret;
			if (lno.useGrouping) {
				nf.setGroupingUsed(false);
				tester = nf.format(n);
			}
			int minus = (tester.charAt(0) == '-') ? 1 : 0;
			int lh = tester.length();
			if (lh - minus <= 15)
				break;
			int komma = minus;
			for (; komma < lh; komma++)
				if (!Character.isDigit(tester.charAt(komma)))
					break;
			if (komma - minus <= 15)
				break;
			nf = NumberFormat.getScientificInstance(locale);
			nf.setGroupingUsed(lno.useGrouping);
			ret = nf.format(n);
		} while (false);
		if (lno.negativeAsParentheses && ret.charAt(0) == '-')
			ret = '(' + ret.substring(1) + ')';
		return ret;
	}

	public Locale getLocale() {
		return locale;
	}

	private static Map<Locale, Formatter> instances = new HashMap<Locale, Formatter>();

	public static synchronized Formatter getFormatter(Locale loc) {
		if (loc == null)
			loc = Locale.getDefault();
		Formatter ret = instances.get(loc);
		if (ret == null) {
			ret = new Formatter(loc);
			instances.put(loc, ret);
		}
		return ret;
	}
}
