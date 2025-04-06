package run.soeasy.framework.core.transform.stereotype.collection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.support.DefaultConversionService;
import run.soeasy.framework.core.transform.stereotype.Property;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SystemProperty implements Property {
	@NonNull
	private final String name;
	@NonNull
	private ConversionService conversionService = DefaultConversionService.getInstance();

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
