/*
 * © Copyright FOCONIS AG, 2014
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 */
package org.openntf.formula;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.openntf.domino.commons.IDateTime;
import org.openntf.formula.impl.FormatterImpl;

import com.ibm.icu.util.Calendar;

// TODO RPr Needs a lot of comments
public abstract class Formatter {

	public abstract Locale getLocale();

	/*----------------------------------------------------------------------------*/
	public abstract IDateTime getNewSDTInstance();

	public abstract IDateTime getNewInitializedSDTInstance(Date date, boolean noDate, boolean noTime);

	public abstract IDateTime getCopyOfSDTInstance(IDateTime sdt);

	/*----------------------------------------------------------------------------*/
	public abstract IDateTime parseDate(String image);

	public abstract IDateTime parseDate(String image, boolean parseLenient);

	public abstract Calendar parseDateToCal(String image, boolean[] noDT, boolean parseLenient);

	public abstract IDateTime parseDateWithFormat(String image, String format, boolean parseLenient);

	public abstract Calendar parseDateToCalWithFormat(String image, String format, boolean[] noDT, boolean parseLenient);

	/*----------------------------------------------------------------------------*/
	public abstract Number parseNumber(String image);

	public abstract Number parseNumber(String image, boolean lenient);

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

	public static final int TIMEFORMAT_MEDIUM = 1;
	public static final int TIMEFORMAT_SHORT = 2;
	public static final int TIMEFORMAT_LONG = 3;

	public abstract String formatDateTime(IDateTime sdt);

	public abstract String formatDateTime(IDateTime sdt, LotusDateTimeOptions ldto);

	public abstract String formatCalDateTime(Calendar cal);

	public abstract String formatCalDateTime(Calendar cal, int timeFormat);

	public abstract String formatCalDateOnly(Calendar cal);

	public abstract String formatCalTimeOnly(Calendar cal);

	public abstract String formatCalTimeOnly(Calendar cal, int timeFormat);

	public abstract String formatDateTimeWithFormat(IDateTime sdt, String format);

	public abstract String formatCalWithFormat(Calendar cal, String format);

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

	public abstract String formatNumber(Number n);

	public abstract String formatNumber(Number n, LotusNumberOptions lno);

	/*----------------------------------------------------------------------------*/
	private static Map<Locale, Formatter> instances = new HashMap<Locale, Formatter>();

	public static synchronized Formatter getFormatter(Locale loc) {
		if (loc == null)
			loc = Locale.getDefault();
		Formatter ret = instances.get(loc);
		if (ret == null)
			instances.put(loc, ret = new FormatterImpl(loc));
		return ret;
	}

	public static Formatter getFormatter() {
		return getFormatter(null);
	} /*----------------------------------------------------------------------------*/
}
