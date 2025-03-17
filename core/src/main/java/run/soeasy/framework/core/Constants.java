package run.soeasy.framework.core;

import java.nio.charset.Charset;

import org.objectweb.asm.Opcodes;

public class Constants {
	public static final String ISO_8859_1_NAME = "ISO-8859-1";

	public static final Charset ISO_8859_1 = Charset.forName(ISO_8859_1_NAME);

	public static final String UTF_8_NAME = "UTF-8";

	public static final Charset UTF_8 = Charset.forName(UTF_8_NAME);

	public static final String US_ASCII_NAME = "US-ASCII";

	public static final Charset US_ASCII = Charset.forName(US_ASCII_NAME);

	public static final String ASM_VERSION_NAME = "io.basc.framework.asm.version";

	/**
	 * The ASM version used internally throughout the framework.
	 */
	public static final int ASM_VERSION = Integer.getInteger(ASM_VERSION_NAME, Opcodes.ASM7);
}
