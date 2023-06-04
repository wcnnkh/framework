package io.basc.framework.io;

import io.basc.framework.beans.factory.config.InheritableThreadLocalConfigurator;
import io.basc.framework.env.Sys;
import io.basc.framework.json.JsonSerializer;

public final class SerializerUtils {
	private static final InheritableThreadLocalConfigurator<CrossLanguageSerializer> CROSS_LANGUAGE_SERIALIZER_CONFIGURATOR = new InheritableThreadLocalConfigurator<>(
			CrossLanguageSerializer.class, Sys.getEnv()).ifAbsentDefaultService(() -> JsonSerializer.INSTANCE);
	private static final InheritableThreadLocalConfigurator<Serializer> SERIALIZER_CONFIGURATOR = new InheritableThreadLocalConfigurator<>(Serializer.class,
			Sys.getEnv()).ifAbsentDefaultService(() -> JavaSerializer.INSTANCE);

	public static <T> T clone(Serializer serializer, T source) {
		if (source == null) {
			return null;
		}

		try {
			byte[] data = serializer.serialize(source);
			return serializer.deserialize(data);
		} catch (Exception e) {
			// 不可能存在此错误
			throw new SerializerException("This error is not possible", e);
		}
	}

	public static <T> T clone(T source) {
		return clone(getSerializer(), source);
	}

	public static CrossLanguageSerializer getCrossLanguageSerializer() {
		return CROSS_LANGUAGE_SERIALIZER_CONFIGURATOR.get();
	}

	public static InheritableThreadLocalConfigurator<CrossLanguageSerializer> getCrossLanguageSerializerConfigurator() {
		return CROSS_LANGUAGE_SERIALIZER_CONFIGURATOR;
	}

	public static Serializer getSerializer() {
		return SERIALIZER_CONFIGURATOR.get();
	}

	public static InheritableThreadLocalConfigurator<Serializer> getSerializerConfigurator() {
		return SERIALIZER_CONFIGURATOR;
	}

	private SerializerUtils() {
	}
}
