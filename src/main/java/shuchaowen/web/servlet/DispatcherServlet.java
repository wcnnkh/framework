package shuchaowen.web.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import shuchaowen.common.utils.ConfigUtils;

public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = -8268337109249457358L;
	private HttpServerApplication httpServerApplication;

	public HttpServerApplication getHttpServerApplication() {
		return httpServerApplication;
	}

	@Override
	public final void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
		if (httpServerApplication.getCharset() != null) {
			req.setCharacterEncoding(httpServerApplication.getCharset().name());
			res.setCharacterEncoding(httpServerApplication.getCharset().name());
		}
		super.service(req, res);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		myService(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		myService(req, resp);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		myService(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		myService(req, resp);
	}

	@Override
	public final void init(ServletConfig servletConfig) throws ServletException {
		httpServerApplication = new HttpServerApplication(
				new ServletConfigFactory(servletConfig));
		httpServerApplication.getBeanFactory().registerNameMapping(
				servletConfig.getServletName(), this.getClass().getName());
		try {
			httpServerApplication.getBeanFactory().registerSingleton(
					this.getClass(), this);
		} catch (Exception e) {
			throw new ServletException(e);
		}

		super.init(servletConfig);
		httpServerApplication.init();
	}

	@Override
	public void destroy() {
		super.destroy();
		httpServerApplication.destroy();
	}

	public final String getConfig(String key) {
		return getConfig(key, null);
	}

	public final String getConfig(String key, String defaultValue) {
		String value = getServletConfig().getInitParameter(key);
		value = value == null ? defaultValue : value;
		return value == null ? value : ConfigUtils.format(value);
	}

	protected void myService(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		try {
			getHttpServerApplication().service(httpServletRequest,
					httpServletResponse);
		} catch (Throwable e) {
			e.printStackTrace();
			httpServerApplication.sendError(httpServletRequest,
					httpServletResponse, 500, "system error");
		}
	}
}
