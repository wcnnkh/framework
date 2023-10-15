package io.basc.framework.web.servlet;

import io.basc.framework.web.servlet.convert.ServletConverters;
import io.basc.framework.web.servlet.http.DefaultHttpServletConverter;

public class DispatchServletConverter extends ServletConverters {

	public DispatchServletConverter() {
		registerLast(new DefaultHttpServletConverter());
	}

}
