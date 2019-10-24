package scw.mvc.servlet.http;

import java.util.Collection;

import scw.beans.BeanFactory;
import scw.json.JSONParseSupport;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.mvc.parameter.ParameterFilter;

@SuppressWarnings("unchecked")
public class FormHttpServletChannel extends HttpServletChannel{
	private static Logger logger = LoggerFactory.getLogger(FormHttpServletChannel.class);

	public FormHttpServletChannel(BeanFactory beanFactory,
			Collection<ParameterFilter> parameterFilters, JSONParseSupport jsonParseSupport, boolean cookieValue,
			HttpRequest request, HttpResponse response, String jsonp) {
		super(beanFactory, parameterFilters, jsonParseSupport, cookieValue, request, response, jsonp);
		if (isLogEnabled()) {
			log("requestPath={},method={},{}", getRequest().getRequestPath(), getRequest().getMethod(),
					JSONUtils.toJSONString(getRequest().getParameterMap()));
		}
	}

	public Logger getLogger() {
		return logger;
	}
	
	@Override
	public MyHttpServletRequest getRequest() {
		return super.getRequest();
	}
	
	@Override
	public MyHttpServletResponse getResponse() {
		return super.getResponse();
	}
}
