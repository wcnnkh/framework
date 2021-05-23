package scw.web.model;

import java.util.LinkedHashMap;
import java.util.Map;

import scw.http.HttpHeaders;

public class Page extends LinkedHashMap<String, Object> implements Cloneable {
	private static final long serialVersionUID = 1L;
	private final String name;
	private final HttpHeaders headers = new HttpHeaders();

	public Page(String name) {
		this.name = name;
	}

	public Page(String name, Map<String, Object> attributes) {
		super(attributes);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	@Override
	public Page clone() {
		Page page = new Page(name, this);
		page.headers.putAll(this.headers);
		return page;
	}
}
