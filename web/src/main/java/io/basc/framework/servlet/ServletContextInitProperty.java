package io.basc.framework.servlet;

import javax.servlet.ServletContext;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.config.ConversionService;
import io.basc.framework.core.convert.transform.stereotype.Property;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ServletContextInitProperty implements Property {
	@NonNull
	private final ServletContext servletContext;
	@NonNull
	private final String name;
	private ConversionService conversionService;

	@Override
	public void set(Object value) throws UnsupportedOperationException {
		String v = (String) conversionService.convert(value, TypeDescriptor.forObject(value),
				TypeDescriptor.valueOf(String.class));
		servletContext.setInitParameter(name, v);
	}

	@Override
	public Object get() throws ConversionException {
		return servletContext.getInitParameter(name);
	}
}
