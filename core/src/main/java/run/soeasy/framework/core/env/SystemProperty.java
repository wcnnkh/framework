package run.soeasy.framework.core.env;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.transform.stereotype.Property;

@RequiredArgsConstructor
public class SystemProperty implements Property {
	@NonNull
	private final String name;
	@NonNull
	private final ConversionService conversionService;

	@Override
	public void set(Object source) throws UnsupportedOperationException {
		String value = (String) conversionService.convert(Source.of(source), TypeDescriptor.valueOf(String.class));
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
