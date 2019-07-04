package scw.servlet.http;

import javax.servlet.http.HttpServletResponse;

import scw.core.json.JSONParseSupport;
import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;

public final class DefaultHttpResponse extends AbstractHttpResponse {
	private static Logger logger = LoggerFactory.getLogger(DefaultHttpResponse.class);

	public DefaultHttpResponse(JSONParseSupport jsonParseSupport, HttpRequest httpRequest,
			HttpServletResponse httpServletResponse, boolean jsonp, boolean debug) {
		super(jsonParseSupport, httpRequest, httpServletResponse, jsonp, debug);
	}

	public Logger getLogger() {
		return logger;
	}

}
