package scw.servlet.request;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.servlet.beans.RequestBeanFactory;

public class FormRequest extends AbstractRequest {
	private Logger logger = LoggerFactory.getLogger(getClass());

	public FormRequest(RequestBeanFactory requestBeanFactory, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, boolean isDebug, boolean cookieValue) throws IOException {
		super(requestBeanFactory, httpServletRequest, httpServletResponse, isDebug, cookieValue);
		if (isDebug) {
			StringBuilder sb = new StringBuilder();
			sb.append("servletPath=");
			sb.append(httpServletRequest.getServletPath());
			sb.append(",method=");
			sb.append(httpServletRequest.getMethod());
			sb.append(",");
			sb.append(JSONObject.toJSONString(getParameterMap()));
			logger.debug(sb.toString());
		}
	}
}
