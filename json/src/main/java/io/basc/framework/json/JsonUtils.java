package io.basc.framework.json;

import io.basc.framework.beans.factory.config.InheritableThreadLocalConfigurator;
import io.basc.framework.beans.factory.spi.SPI;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.gson.GsonSupport;

public final class JsonUtils implements JsonSupport {
	private static final InheritableThreadLocalConfigurator<JsonSupport> CONFIGURATOR = new InheritableThreadLocalConfigurator<>(
			JsonSupport.class, SPI.global()).ifAbsentDefaultService(() -> GsonSupport.INSTANCE);;

	public static InheritableThreadLocalConfigurator<JsonSupport> getConfigurator() {
		return CONFIGURATOR;
	}

	private static volatile JsonSupport support;

	public static JsonSupport getSupport() {
		if (support == null) {
			synchronized (JsonUtils.class) {
				if (support == null) {
					support = new JsonUtils();
				}
			}
		}
		return support;
	}

	private JsonUtils() {
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return CONFIGURATOR.get().canConvert(sourceType, targetType);
	}

	@Override
	public String toJsonString(Object obj) throws JsonException {
		return CONFIGURATOR.get().toJsonString(obj);
	}

	@Override
	public JsonElement parseJson(String text) throws JsonException {
		return CONFIGURATOR.get().parseJson(text);
	}
}
