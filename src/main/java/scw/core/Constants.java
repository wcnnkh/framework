package scw.core;

import scw.core.asm.Opcodes;

public final class Constants {
	private Constants() {
	};

	public static final String DEFAULT_CHARSET_NAME = "UTF-8";

	/**
	 * The ASM version used internally throughout the framework.
	 *
	 * @see Opcodes#ASM4
	 */
	public static final int ASM_VERSION = Opcodes.ASM5;

	/**
	 * 可用的处理器数量
	 */
	public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
}
