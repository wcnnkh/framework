package scw.servlet.page;

import java.util.HashMap;

import scw.servlet.View;

public abstract class AbstractPage extends HashMap<String, Object> implements Page, View {
	private static final long serialVersionUID = 1L;
	private String page;

	public AbstractPage(String page) {
		this.page = page;
	}

	public String getPage() {
		return page;
	}

}
