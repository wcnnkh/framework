package scw.common;

import java.nio.charset.Charset;

import org.objectweb.asm.Opcodes;

public final class Constants {
	private Constants() {
	};

	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	/**
	 * The ASM version used internally throughout the framework.
	 *
	 * @see Opcodes#ASM4
	 */
	public static final int ASM_VERSION = Opcodes.ASM4;
}
