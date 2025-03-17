package run.soeasy.framework.core.env;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.transform.stereotype.Properties;
import run.soeasy.framework.util.alias.Named;

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
