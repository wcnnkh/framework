package scw.mvc.page;

import java.util.HashMap;

import scw.mvc.Channel;
import scw.mvc.Request;
import scw.mvc.RequestResponseModelChannel;
import scw.mvc.Response;

public abstract class AbstractPage extends HashMap<String, Object> implements Page {
	private static final long serialVersionUID = 1L;
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

	@SuppressWarnings("unchecked")
	public void render(Channel channel) throws Throwable {
		if (channel instanceof RequestResponseModelChannel) {
			render((RequestResponseModelChannel<Request, Response>) channel);
		}
	}

	protected abstract void render(RequestResponseModelChannel<? extends Request, ? extends Response> channel) throws Throwable;
}
