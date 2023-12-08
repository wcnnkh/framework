package io.basc.framework.json;

import io.basc.framework.beans.factory.config.InheritableThreadLocalConfigurator;
import io.basc.framework.beans.factory.spi.SPI;
import io.basc.framework.gson.GsonSupport;

public final class JsonUtils {
	private static final InheritableThreadLocalConfigurator<JsonSupport> CONFIGURATOR = new InheritableThreadLocalConfigurator<>(
			JsonSupport.class, SPI.global()).ifAbsentDefaultService(() -> GsonSupport.INSTANCE);;

	public static InheritableThreadLocalConfigurator<JsonSupport> getConfigurator() {
		return CONFIGURATOR;
	}

	public static JsonSupport getSupport() {
		return CONFIGURATOR.get();
	}

	private JsonUtils() {
	}
}
