package scw.io;

import scw.core.Bits;
import scw.core.instance.InstanceUtils;
import scw.core.utils.ClassUtils;
import scw.io.serializer.Serializer;
import scw.io.serializer.support.JavaSerializer;
import scw.logger.LoggerUtils;

public final class SerializerUtils {
	static {
		Class<?> serializerClass = null;
		String[] seralizerClassNames = { "scw.io.serializer.support.Hessian2Serializer",
				"scw.io.serializer.support.HessianSerializer" };

		for (String name : seralizerClassNames) {
			try {
				serializerClass = Class.forName(name);
				break;
			} catch (Throwable e) {
			}
		}

		DEFAULT_SERIALIZER = (Serializer) (serializerClass == null ? JavaSerializer.SERIALIZER
				: InstanceUtils.getInstance(serializerClass));
		LoggerUtils.info(SerializerUtils.class, "default serializer："
				+ (serializerClass == null ? JavaSerializer.class.getName() : serializerClass.getName()));
	}

	/**
	 * 默认的序列化实现
	 */
	public static final Serializer DEFAULT_SERIALIZER;

	private SerializerUtils() {
	}

	public static byte[] class2bytes(Class<?> clazz) {
		String name = clazz.getName();
		UnsafeByteArrayOutputStream byteArray = null;
		try {
			byteArray = new UnsafeByteArrayOutputStream();
			for (char c : name.toCharArray()) {
				byte[] bs = { 0, 0 };
				Bits.putChar(bs, 0, c);
				byteArray.write(bs, 0, bs.length);
			}
			return byteArray.toByteArray();
		} finally {
			IOUtils.close(byteArray);
		}
	}

	public static Class<?> bytes2class(byte[] bytes) throws ClassNotFoundException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i += 2) {
			char c = Bits.getChar(bytes, i);
			sb.append(c);
		}
		return ClassUtils.forName(sb.toString());
	}

	/**
	 * 使用序列化来实现对象拷贝
	 * 
	 * @param obj
	 * @return
	 */
	public static <T> T clone(T obj) {
		if (obj == null) {
			return null;
		}

		return DEFAULT_SERIALIZER.deserialize(DEFAULT_SERIALIZER.serialize(obj));
	}
}
