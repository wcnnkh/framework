package scw.core;

import java.nio.charset.Charset;

import org.objectweb.asm.Opcodes;

import scw.core.serializer.NoTypeSpecifiedSerializer;
import scw.core.serializer.Serializer;
import scw.core.serializer.SpecifiedTypeSerializer;
import scw.core.serializer.support.AutoNoTypeSpecifiedSerializer;
import scw.core.serializer.support.AutoSerializer;
import scw.core.serializer.support.AutoSpecifiedTypeSerializer;
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
	 * 指定类型的序列化器
	 */
	public static final SpecifiedTypeSerializer AUTO_SPECIFIED_TYPE_SERIALIZER = new AutoSpecifiedTypeSerializer();

	/**
	 * 不指定类型的序列化器
	 */
	public static final NoTypeSpecifiedSerializer AUTO_NO_TYPE_SPECIFIED_SERIALIZER = new AutoNoTypeSpecifiedSerializer();

	/**
	 * 序列化器
	 */
	public static final Serializer AUTO_SERIALIZER = new AutoSerializer();
	
	/**
	 * java自身的序列化实现
	 */
	public static final Serializer JAVA_SERIALIZER = new JavaObjectSerializer();
}
