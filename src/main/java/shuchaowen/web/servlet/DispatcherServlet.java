package shuchaowen.web.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import shuchaowen.core.application.HttpServerApplication;
import shuchaowen.core.http.enums.ContentType;
import shuchaowen.core.util.ConfigUtils;
import shuchaowen.web.servlet.request.FormRequest;
import shuchaowen.web.servlet.request.JsonRequest;

public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = -8268337109249457358L;
	private HttpServerApplication httpServerApplication;

	public HttpServerApplication getHttpServerApplication() {
		return httpServerApplication;
	}
	
	@Override
	public final void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		super.service(req, res);
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (httpServerApplication.getCharset() != null) {
			req.setCharacterEncoding(httpServerApplication.getCharset().name());
			resp.setCharacterEncoding(httpServerApplication.getCharset().name());
		}
		super.service(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (httpServerApplication.isRpcEnabled() && req.getServletPath().equals(httpServerApplication.getRpcServletPath())) {
			rpc(req, resp);
		} else {
			controller(req, resp);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		controller(req, resp);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		controller(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		controller(req, resp);
	}

	public void rpc(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws IOException, ServletException {
		try {
			httpServerApplication.rpc(httpServletRequest.getInputStream(), httpServletResponse.getOutputStream());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public final void init(ServletConfig servletConfig) throws ServletException {
		httpServerApplication = new HttpServerApplication(new ServletConfigFactory(servletConfig));
		httpServerApplication.init();
		super.init(servletConfig);
	}
	
	@Override
	public void destroy() {
		httpServerApplication.destroy();
		super.destroy();
	}
	
	public final String getConfig(String key) {
		return getConfig(key, null);
	}

	public final String getConfig(String key, String defaultValue) {
		String value = getServletConfig().getInitParameter(key);
		value = value == null ? defaultValue : value;
		return value == null ? value : ConfigUtils.format(value);
	}
	
	/**
	 * request封装
	 * 
	 * @param httpServletRequest
	 * @return
	 * @throws IOException
	 */
	public WebRequest wrapperRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
		if(httpServletRequest.getContentType() == null || httpServletRequest.getContentType().startsWith(ContentType.FORM.getValue())){
			return new FormRequest(httpServerApplication.getBeanFactory(), httpServletRequest, httpServletResponse, httpServerApplication.isDebug());
		}else if(httpServletRequest.getContentType().startsWith(ContentType.JSON.getValue())){
			return new JsonRequest(httpServerApplication.getBeanFactory(), httpServletRequest, httpServletResponse, httpServerApplication.isDebug());
		}else{
			return new FormRequest(httpServerApplication.getBeanFactory(), httpServletRequest, httpServletResponse, httpServerApplication.isDebug());
		} 
	}
	
	public void controller(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws IOException, ServletException {
		WebRequest request = wrapperRequest(httpServletRequest, httpServletResponse);
		try {
			if(!httpServerApplication.service(request, new WebResponse(request, httpServletResponse))){
				if(!httpServletResponse.isCommitted()){
					httpServletResponse.sendError(404, request.getServletPath());
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			if(!httpServletResponse.isCommitted()){
				httpServletResponse.sendError(500, request.getServletPath());
			}
		}
	}
}
