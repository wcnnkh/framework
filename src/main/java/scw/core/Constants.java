package scw.core;

import scw.asm.Opcodes;
import scw.core.reflect.ReflectUtils;
import scw.logger.LoggerUtils;
import scw.serializer.Serializer;
import scw.serializer.support.JavaSerializer;

public final class Constants {
	static {
		Class<?> serializerClass = null;
		String[] seralizerClassNames = { "scw.core.serializer.support.Hessian2Serializer",
				"scw.core.serializer.support.DubboHessian2Serializer", "scw.core.serializer.support.HessianSerializer",
				"scw.core.serializer.support.DubboHessianSerializer" };

		for (String name : seralizerClassNames) {
			try {
				serializerClass = Class.forName(name);
				break;
			} catch (Throwable e) {
			}
		}

		if (serializerClass == null) {
			serializerClass = JavaSerializer.class;
		}

		DEFAULT_SERIALIZER = (Serializer) ReflectUtils.newInstance(serializerClass);
		LoggerUtils.info(Constants.class, "default serializer：" + serializerClass.getName());
	}

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
	 * 默认的序列化实现
	 */
	public static final Serializer DEFAULT_SERIALIZER;
}
