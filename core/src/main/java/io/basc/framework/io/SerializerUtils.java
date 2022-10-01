package io.basc.framework.io;

import io.basc.framework.env.Sys;
import io.basc.framework.json.JsonSerializer;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

public final class SerializerUtils {
	private static Logger logger = LoggerFactory.getLogger(SerializerUtils.class);
	/**
	 * 默认的序列化实现
	 */
	private static final Serializer SERIALIZER;
	private static final CrossLanguageSerializer CROSS_LANGUAGE_SERIALIZER;

	static {
		Serializer serializer = Sys.getEnv().getServiceLoader(Serializer.class).first();
		SERIALIZER = serializer == null ? JavaSerializer.INSTANCE : serializer;
		logger.info("default serializer {}", SERIALIZER);

		CrossLanguageSerializer crossLanguageSerializer = Sys.getEnv().getServiceLoader(CrossLanguageSerializer.class)
				.first();
		CROSS_LANGUAGE_SERIALIZER = crossLanguageSerializer == null ? JsonSerializer.INSTANCE : crossLanguageSerializer;
		logger.info("default cross language serializer {}", CROSS_LANGUAGE_SERIALIZER);
	}

	private SerializerUtils() {
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

		try {
			return SERIALIZER.deserialize(SERIALIZER.serialize(obj));
		} catch (Exception e) {
			// 不可能存在此错误
			throw new RuntimeException(e);
		}
	}

	public static Serializer getSerializer() {
		return SERIALIZER;
	}

	public static CrossLanguageSerializer getCrossLanguageSerializer() {
		return CROSS_LANGUAGE_SERIALIZER;
	}
}
