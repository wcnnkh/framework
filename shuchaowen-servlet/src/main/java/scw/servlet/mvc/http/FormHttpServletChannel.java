package scw.servlet.mvc.http;

import scw.beans.BeanFactory;
import scw.json.JSONSupport;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.AsyncControl;

@SuppressWarnings("unchecked")
public class FormHttpServletChannel extends HttpServletChannel {
	private static Logger logger = LoggerFactory.getLogger(FormHttpServletChannel.class);

	public FormHttpServletChannel(BeanFactory beanFactory, JSONSupport jsonParseSupport, boolean cookieValue,
			MyHttpServletRequest request, MyHttpServletResponse response) {
		super(beanFactory, jsonParseSupport, cookieValue, request, response);
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

	public boolean isSupportAsyncControl() {
		return super.isSupportAsyncControl();
	}

	public AsyncControl getAsyncControl() {
		return super.getAsyncControl();
	}
	
	@Override
	public String[] getStringArray(String key) {
		return getRequest().getParameterValues(key);
	}
}
