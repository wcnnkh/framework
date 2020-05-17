package scw.net.http.server.mvc;

import scw.beans.BeanFactory;
import scw.json.JSONSupport;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;

public class FormHttpChannel<R extends ServerHttpRequest, P extends ServerHttpResponse>
		extends AbstractHttpChannel<ServerHttpRequest, ServerHttpResponse> {
	private static Logger logger = LoggerFactory.getLogger(FormHttpChannel.class);

	public FormHttpChannel(BeanFactory beanFactory, JSONSupport jsonParseSupport,
			R request, P response) {
		super(beanFactory, jsonParseSupport, request, response);
		if (isLogEnabled()) {
			log("controller={},method={},{}", getRequest().getPath(), getRequest().getMethod(),
					JSONUtils.toJSONString(getRequest().getParameterMap()));
		}
	}

	public Logger getLogger() {
		return logger;
	}
}
