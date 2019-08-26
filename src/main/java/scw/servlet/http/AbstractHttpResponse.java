package scw.servlet.http;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import scw.json.JSONParseSupport;
import scw.servlet.ServletUtils;

public abstract class AbstractHttpResponse extends HttpServletResponseWrapper implements HttpResponse {
	private HttpRequest httpRequest;
	private JSONParseSupport jsonParseSupport;
	private boolean debug;
	private boolean jsonp;

	public AbstractHttpResponse(JSONParseSupport jsonParseSupport, HttpRequest httpRequest,
			HttpServletResponse httpServletResponse, boolean jsonp, boolean debug) {
		super(httpServletResponse);
		this.jsonParseSupport = jsonParseSupport;
		this.httpRequest = httpRequest;
		this.debug = debug;
		this.jsonp = jsonp;
	}

	public HttpRequest getHttpRequest() {
		return httpRequest;
	}

	public void write(Object obj) throws Exception {
		ServletUtils.defaultResponse(httpRequest, this, jsonParseSupport, obj, jsonp);
	}
	
	public boolean isLogEnabled() {
		return debug;
	}

	public void log(String format, Object... args) {
		if (isLogEnabled()) {
			getLogger().info(format, args);
		}
	}

}
