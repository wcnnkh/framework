package scw.core;

import java.nio.charset.Charset;

import org.objectweb.asm.Opcodes;

import scw.core.serializer.Serializer;
import scw.core.serializer.support.JavaObjectSerializer;

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
	
	/**
	 * 默认的序列化实现
	 */
	public static final Serializer DEFAULT_SERIALIZER = new JavaObjectSerializer();
}
