package org.openntf.domino.nsfdata.structs.cd;

import java.util.EnumSet;
import java.util.Set;

import org.openntf.domino.nsfdata.structs.SIG;
import org.openntf.domino.nsfdata.structs.WSIG;

/**
 * This CD structure defines simple actions, formulas or LotusScript within a given image map. (editods.h)
 * 
 * @since Lotus Notes/Domino 5.0
 *
 */
public class CDEVENT extends CDRecord {
	public static enum Flag {
		EVENT_HAS_LIBRARIES(0x00000001);

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

	public static enum HTMLEvent {
		ONCLICK, ONDBLCLICK, ONMOUSEDOWN, ONMOUSEUP, ONMOUSEOVER, ONMOUSEMOVE, ONMOUSEOUT, ONKEYPRESS, ONKEYDOWN, ONKEYUP, ONFOCUS, ONBLUR,
		ONLOAD, ONUNLOAD, HEADER, ONSUBMIT, ONRESET, ONCHANGE, ONERROR, ONHELP, ONSELECT,
		/**
		 * This isn't really an event
		 */
		LIBRARY
	}

	public static enum Action {
		UNKNOWN, FORMULA, CANNED_ACTION, LOTUS_SCRIPT, JAVASCRIPT
	}

	public final WSIG Header = inner(new WSIG());
	/**
	 * Use getFlags for access.
	 */
	@Deprecated
	public final Unsigned32 Flags = new Unsigned32();
	public final Enum16<HTMLEvent> EventType = new Enum16<HTMLEvent>(HTMLEvent.values());
	public final Enum16<Action> ActionType = new Enum16<Action>(Action.values());
	public final Unsigned32 ActionLength = new Unsigned32();
	public final Unsigned16 SignatureLength = new Unsigned16();
	public final Unsigned8[] Reserved = array(new Unsigned8[14]);

	static {
		addVariableData("Action", "ActionLength");
		addVariableData("Signature", "SignatureLength");
	}

	@Override
	public SIG getHeader() {
		return Header;
	}

	public Set<Flag> getFlags() {
		return Flag.valuesOf((int) Flags.get());
	}

	public byte[] getAction() {
		return (byte[]) getVariableElement("Action");
	}

	public byte[] getEventSignature() {
		return (byte[]) getVariableElement("Signature");
	}

	@Override
	public String toString() {
		// TODO Figure out why getting the variable elements breaks
		return "[" + getClass().getSimpleName() + "]";
	}
}
