package scw.mvc.page;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import scw.mvc.HttpChannel;

public abstract class AbstractPage extends LinkedHashMap<String, Object> implements Page {
	private static final long serialVersionUID = 1L;
	static final String REQUEST = "_request";

	private String page;

	public AbstractPage(String page) {
		this.page = page;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	@Override
	public String toString() {
		if (super.isEmpty()) {
			return page;
		}

		return page + " --> " + super.toString();
	}

	public void putList(String key, List<?> values) {
		if (values.size() == 1) {
			put(key, values.get(0));
		} else {
			put(key, values);
		}
	}

	public void render(HttpChannel httpChannel) throws IOException {
		if(!containsKey(REQUEST)){
			put(REQUEST, httpChannel.getRequest());
		}
		renderInternal(httpChannel);
	}

	protected abstract void renderInternal(HttpChannel httpChannel) throws IOException;
}
