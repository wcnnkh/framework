package io.basc.framework.web.servlet.http;

import io.basc.framework.http.HttpHeaders;

import javax.servlet.http.Part;

public class ServletPartHeaders extends HttpHeaders {
	private static final long serialVersionUID = 1L;

	public ServletPartHeaders(Part part) {
		if (part == null) {
			return;
		}

		for (String name : part.getHeaderNames()) {
			for (String value : part.getHeaders(name)) {
				add(name, value);
			}
		}
	}
}
