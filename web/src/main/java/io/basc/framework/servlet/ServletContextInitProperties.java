package io.basc.framework.servlet;

import javax.servlet.ServletContext;

import io.basc.framework.core.convert.service.ConversionService;
import io.basc.framework.core.convert.transform.stereotype.Properties;
import io.basc.framework.core.convert.transform.stereotype.Property;
import io.basc.framework.util.collections.Elements;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ServletContextInitProperties implements Properties {
	@NonNull
	private final ServletContext servletContext;
	private ConversionService conversionService;

	@Override
	public Property get(String key) {
		String value = servletContext.getInitParameter(key);
		if (value == null) {
			return null;
		}

		return createProperty(key);
	}

	private Property createProperty(String key) {
		return new ServletContextInitProperty(servletContext, key, conversionService);
	}

	@Override
	public Elements<Property> getAccessors(@NonNull Object index) {
		if (index instanceof String) {
			return Elements.singleton(createProperty((String) index));
		}
		return Properties.super.getAccessors(index);
	}

	@Override
	public Elements<String> keys() {
		return Elements.of(() -> servletContext.getInitParameterNames());
	}

	@Override
	public Elements<Property> getElements() {
		return keys().map((e) -> createProperty(e));
	}

}
