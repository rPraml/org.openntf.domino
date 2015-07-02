package org.openntf.domino.nsfdata.structs.cd;

import java.util.EnumSet;
import java.util.Set;

import org.openntf.domino.nsfdata.structs.SIG;
import org.openntf.domino.nsfdata.structs.WSIG;

/**
 * This CD Record gives information pertaining to shared resources and/or shared code in a form. A CDINLINE record may be preceded by a
 * CDBEGINRECORD and followed by a CDRESOURCE and then a CDENDRECORD. (editods.h)
 * 
 * @since Lotus Notes/Domino 6.0
 *
 */
public class CDINLINE extends CDRecord {

	/**
	 * These flags are values for the dwFlags member of CDINLINE. A CDINLINE record may be preceded by a CDBEGINRECORD and followed by a
	 * CDRESOURCE and then a CDENDRECORD. (editods.h)
	 * 
	 * @since Lotus Notes/Domino 6.0
	 *
	 */
	public static enum Flag {
		UNKNOWN(0x00000000), SCRIPT_LIB(0x00000001), STYLE_SHEET(0x00000002), HTML(0x00000004), HTMLFILERES(0x00000008);

		private final int value_;

		private Flag(final int value) {
			value_ = value;
		}

		public int getValue() {
			return value_;
		}

		public static Set<Flag> valuesOf(final int flags) {
			Set<Flag> result = EnumSet.noneOf(Flag.class);
			for (Flag flag : values()) {
				if ((flag.getValue() & flags) > 0) {
					result.add(flag);
				}
			}
			return result;
		}
	}

	public final WSIG Header = inner(new WSIG());
	public final Unsigned16 wDatalength = new Unsigned16();
	/**
	 * Use getFlags for access.
	 */
	@Deprecated
	public final Unsigned32 dwFlags = new Unsigned32();
	public final Unsigned32[] dwReserved = array(new Unsigned32[4]);

	@Override
	public SIG getHeader() {
		return Header;
	}

	public Set<Flag> getFlags() {
		return Flag.valuesOf((int) dwFlags.get());
	}
}
