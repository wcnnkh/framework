package scw.servlet.http;

import javax.servlet.http.HttpServletResponse;

import scw.json.JSONParseSupport;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

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
