package scw.servlet;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.beans.BeanFactory;
import scw.beans.property.PropertiesFactory;

public class DefaultServletService extends AbstractServletService {
	private static final long serialVersionUID = 1L;

	public DefaultServletService(BeanFactory beanFactory,
			PropertiesFactory propertiesFactory, String configPath,
			String[] rootBeanFilters) throws Throwable {
		super(beanFactory, propertiesFactory, configPath, rootBeanFilters);
	}

	@Override
	protected void error(ServletRequest request, ServletResponse response,
			Throwable e) {
		if (request instanceof HttpServletRequest
				&& response instanceof HttpServletResponse) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;
			if (!httpServletResponse.isCommitted()) {
				StringBuilder sb = new StringBuilder();
				sb.append("servletPath=").append(
						httpServletRequest.getServletPath());
				sb.append(",method=").append(httpServletRequest.getMethod());
				sb.append(",status=").append(500);
				sb.append(",msg=").append("system error");
				String msg = sb.toString();
				try {
					httpServletResponse.sendError(500, msg);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				logger.error(msg, e);
			}
			return;
		}
		e.printStackTrace();
	}
}
