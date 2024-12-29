package io.basc.framework.core.env;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.config.ConversionService;
import io.basc.framework.core.mapping.Property;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SystemProperty implements Property {
	@NonNull
	private final String name;
	@NonNull
	private final ConversionService conversionService;

	@Override
	public void set(Object source) throws UnsupportedOperationException {
		String value = (String) conversionService.convert(Value.of(source), TypeDescriptor.valueOf(String.class));
		System.setProperty(name, value);
	}

	@Override
	public Object get() {
		String value = System.getProperty(name);
		if (value == null) {
			value = System.getenv(value);
		}
		return value;
	}

	@Override
	public String getName() {
		return name;
	}
}
