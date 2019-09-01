package scw.mvc.support;

import java.util.HashMap;

public abstract class AbstractPage extends HashMap<String, Object> implements Page {
	private static final long serialVersionUID = 1L;
	private String page;

	public AbstractPage(String page) {
		this.page = page;
	}

	public String getPage() {
		return page;
	}

}