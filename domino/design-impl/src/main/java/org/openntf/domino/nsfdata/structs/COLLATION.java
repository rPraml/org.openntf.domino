package org.openntf.domino.nsfdata.structs;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * This structure is used to specify the manner in which data in a view is sorted. The COLLATION structure, along with its associated
 * COLLATE_DESCRIPTOR structures and character strings, comprise the $Collation item in a view note. (nifcoll.h)
 * 
 * @since forever
 */
public class COLLATION extends AbstractStruct {
	/**
	 * COLLATION_FLAG_xxx
	 *
	 */
	public static enum Flag {
		UNIQUE((byte) 0x01), BUILD_ON_DEMAND((byte) 0x02);

		private final byte value_;

		private Flag(final byte value) {
			value_ = value;
		}

		public byte getValue() {
			return value_;
		}

		public static Set<Flag> valuesOf(final byte flags) {
			Set<Flag> result = EnumSet.noneOf(Flag.class);
			for (Flag flag : values()) {
				if ((flag.getValue() & flags) > 0) {
					result.add(flag);
				}
			}
			return result;
		}
	}

	public static final byte COLLATION_SIGNATURE = 0x44;

	public final Unsigned16 BufferSize = new Unsigned16();
	public final Unsigned16 Items = new Unsigned16();
	/**
	 * Use getFlags()
	 */
	@Deprecated
	public final Unsigned8 Flags = new Unsigned8();
	public final Unsigned8 signature = new Unsigned8();

	static {
		addVariableArray("Descriptors", "Items", COLLATE_DESCRIPTOR.class);
		addVariableString("ItemNames", "getItemNamesLength");
	}

	public Set<Flag> getFlags() {
		return Flag.valuesOf((byte) Flags.get());
	}

	public int getItemNamesLength() {
		// This is the total size of the item minus the size used by this struct and the descriptor structs
		int total = BufferSize.get();
		int thisSize = size();
		COLLATE_DESCRIPTOR tempDesc = new COLLATE_DESCRIPTOR();
		tempDesc.init();
		return total - thisSize - (Items.get() * tempDesc.size());
	}

	public COLLATE_DESCRIPTOR[] getDescriptors() {
		return (COLLATE_DESCRIPTOR[]) getVariableElement("Descriptors");
	}

	public String[] getItemNames() {
		COLLATE_DESCRIPTOR[] descriptors = (COLLATE_DESCRIPTOR[]) getVariableElement("Descriptors");
		String itemNameData = (String) getVariableElement("ItemNames");
		String[] result = new String[descriptors.length];
		for (int i = 0; i < descriptors.length; i++) {
			int offset = descriptors[i].NameOffset.get();
			int length = descriptors[i].NameLength.get();
			result[i] = itemNameData.substring(offset, offset + length);
		}
		return result;
	}

	@Override
	public String toString() {
		return "[" + getClass().getSimpleName() + ": BufferSize=" + BufferSize.get() + ", Items=" + Items.get() + ", Flags=" + getFlags()
				+ ", signature=" + signature.get() + ", Descriptors=" + Arrays.asList(getDescriptors()) + ", ItemNames="
				+ Arrays.asList(getItemNames()) + "]";
	}
}
