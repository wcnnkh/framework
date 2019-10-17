package scw.core;

import scw.core.annotation.Ignore;
import scw.core.asm.Opcodes;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;

@Ignore
public interface Constants {

	public static final String DEFAULT_CHARSET_NAME = StringUtils
			.toString(SystemPropertyUtils.getProperty("constants.charsetName"), "UTF-8");

	/**
	 * The ASM version used internally throughout the framework.
	 *
	 * @see Opcodes#ASM4
	 */
	public static final int ASM_VERSION = StringUtils.parseInt(SystemPropertyUtils.getProperty("constants.asm.version"),
			Opcodes.ASM5);

	/**
	 * 可用的处理器数量
	 */
	public static final int AVAILABLE_PROCESSORS = StringUtils.parseInt(
			SystemPropertyUtils.getProperty("constants.available.processors"),
			Runtime.getRuntime().availableProcessors());
}
