package io.basc.framework.core.env;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class NamedProperties<W extends Properties> implements PropertySource, PropertiesWrapper<W> {
	@NonNull
	private final String name;
	@NonNull
	private final W source;
}
