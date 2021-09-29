package io.basc.framework.web.model;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModelAndView extends LinkedHashMap<String, Object> implements Cloneable {
	private static final long serialVersionUID = 1L;
	
	public static final TypeDescriptor TYPE_DESCRIPTOR = TypeDescriptor.valueOf(ModelAndView.class);
	
	private final String name;
	private final HttpHeaders headers = new HttpHeaders();
	private HttpStatus httpStatus;

	public ModelAndView(String name) {
		this.name = name;
	}

	public ModelAndView(String name, Map<String, Object> attributes) {
		super(attributes);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

	@Override
	public ModelAndView clone() {
		ModelAndView page = new ModelAndView(name, this);
		page.headers.putAll(this.headers);
		return page;
	}
}
