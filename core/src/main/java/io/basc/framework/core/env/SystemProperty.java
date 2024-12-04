package io.basc.framework.core.env;

import io.basc.framework.core.convert.ConversionService;
import io.basc.framework.core.convert.transform.Property;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SystemProperty implements Property {
	@NonNull
	private final String name;
	@NonNull
	private final ConversionService conversionService;

	@Override
	public void setSource(Object source) throws UnsupportedOperationException {
		String value = conversionService.convert(source, String.class);
		System.setProperty(name, value);
	}

	@Override
	public Object getSource() {
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
