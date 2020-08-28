package scw.core;

import java.nio.charset.Charset;

import scw.asm.Opcodes;
import scw.core.utils.StringUtils;

public class Constants {
	public static final Charset ISO_8859_1 = Charset.forName("iso-8859-1");

	public static final Charset UTF_8 = Charset.forName("UTF-8");

	public static final Charset US_ASCII = Charset.forName("US-ASCII");

	/**
	 * 系统换行符
	 */
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	/**
	 * 系统包名
	 */
	public static final String SYSTEM_PACKAGE_NAME = StringUtils.split(Constants.class.getPackage().getName(), '.')[0];

	/**
	 * 默认的字符集
	 */
	public static final String DEFAULT_CHARSET_NAME = GlobalPropertyFactory.getInstance()
			.getValue("constants.default.charsetName", String.class, UTF_8.name());

	public static final Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_CHARSET_NAME);

	/**
	 * The ASM version used internally throughout the framework.<br/>
	 * 默认的asm版本
	 * 
	 * @see Opcodes#ASM4
	 */
	public static final int ASM_VERSION = StringUtils
			.parseInt(GlobalPropertyFactory.getInstance().getString("constants.asm.version"), Opcodes.ASM7);

}
