package io.basc.framework.core.env;

import io.basc.framework.core.convert.transform.stereotype.Properties;
import io.basc.framework.util.alias.Named;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface PropertySource extends Properties, Named {

	@RequiredArgsConstructor
	@Getter
	public static class NamedProperties<W extends Properties> implements PropertySource, PropertiesWrapper<W> {
		@NonNull
		private final String name;
		@NonNull
		private final W source;
	}

	default PropertySource rename(@NonNull String name) {
		return new NamedProperties<>(name, this);
	}

	public static PropertySource forProperties(@NonNull String name, @NonNull Properties properties) {
		return new NamedProperties<Properties>(name, properties);
	}
}
