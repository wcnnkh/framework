package scw.core;

import scw.core.asm.Opcodes;
import scw.core.string.StringCodec;
import scw.core.string.StringCodecUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.core.utils.SystemUtils;
import scw.lang.Ignore;

@Ignore
public interface Constants {

	public static final String DEFAULT_CHARSET_NAME = StringUtils
			.toString(SystemPropertyUtils.getProperty("constants.default.charsetName"), "UTF-8");

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
			SystemPropertyUtils.getProperty("constants.available.processors"), SystemUtils.getAvailableProcessors());

	/**
	 * 注意：可能为空
	 */
	public static final String DEFAULT_PREFIX = SystemPropertyUtils.getProperty("constants.default.prefix");

	public static final StringCodec DEFAULT_STRING_CODEC = StringCodecUtils.getStringCodec(DEFAULT_CHARSET_NAME);
}
