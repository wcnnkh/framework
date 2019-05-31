package scw.servlet.http;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import scw.core.logger.DebugLogger;
import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.logger.WarnLogger;
import scw.json.JSONParseSupport;
import scw.servlet.ServletUtils;

public class DefaultHttpResponse extends HttpServletResponseWrapper implements HttpResponse, DebugLogger, WarnLogger {
	private static Logger logger = LoggerFactory.getLogger(DefaultHttpResponse.class);
	private HttpRequest httpRequest;
	private JSONParseSupport jsonParseSupport;
	private boolean debug;
	private boolean jsonp;

	public DefaultHttpResponse(JSONParseSupport jsonParseSupport, HttpRequest httpRequest,
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

	protected Logger getLogger() {
		return logger;
	}

	public boolean isDebugEnabled() {
		return debug;
	}

	public void debug(String msg) {
		if (isDebugEnabled()) {
			getLogger().debug(msg);
		}

	}

	public void debug(String format, Object... args) {
		if (isDebugEnabled()) {
			getLogger().debug(format, args);
		}
	}

	public void debug(String msg, Throwable t) {
		if (isDebugEnabled()) {
			getLogger().debug(msg, t);
		}
	}

	public boolean isWarnEnabled() {
		return getLogger().isWarnEnabled();
	}

	public void warn(String msg) {
		if (isWarnEnabled()) {
			getLogger().warn(msg);
		}
	}

	public void warn(String format, Object... args) {
		if (isWarnEnabled()) {
			getLogger().warn(format, args);
		}
	}

	public void warn(String msg, Throwable t) {
		if (isWarnEnabled()) {
			getLogger().warn(msg, t);
		}
	}
}
