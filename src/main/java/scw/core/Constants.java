package scw.core;

import org.objectweb.asm.Opcodes;

import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.reflect.ReflectUtils;
import scw.core.serializer.Serializer;
import scw.core.serializer.support.JavaSerializer;

public final class Constants {
	private static Logger logger = LoggerFactory.getLogger(Constants.class);

	static {
		Class<?> serializerClass = null;
		String[] seralizerClassNames = {
				"scw.core.serializer.support.Hessian2Serializer",
				"scw.core.serializer.support.DubboHessian2Serializer",
				"scw.core.serializer.support.HessianSerializer",
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

		DEFAULT_SERIALIZER = (Serializer) ReflectUtils
				.newInstance(serializerClass);
		logger.info("default serializer：" + serializerClass.getName());
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
