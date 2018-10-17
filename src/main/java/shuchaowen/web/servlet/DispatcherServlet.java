package shuchaowen.web.servlet;

import java.io.IOException;
import java.nio.charset.Charset;

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
import shuchaowen.core.util.StringUtils;
import shuchaowen.web.servlet.request.FormRequest;
import shuchaowen.web.servlet.request.JsonRequest;

public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = -8268337109249457358L;
	private static final String SHUCHAOWEN_CONFIG = "shuchaowen";
	private boolean debug = false;
	private String rpcServletPath;// 远程代理调用的servletpath，只使用post方法
	private HttpServerApplication httpServerApplication;
	
	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setRpcServletPath(String rpcServletPath) {
		this.rpcServletPath = rpcServletPath;
	}

	public String getRpcServletPath() {
		return rpcServletPath;
	}

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
		if (!StringUtils.isNull(httpServerApplication.getRpcSignStr()) && req.getServletPath().equals(rpcServletPath)) {
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
		httpServerApplication = new HttpServerApplication(servletConfig.getInitParameter(SHUCHAOWEN_CONFIG));
		httpServerApplication.registerSingleton(servletConfig.getServletName(), this);
		httpServerApplication.registerSingleton(this.getClass(), this);
		
		if(httpServerApplication.getCharset() == null){
			String charsetName = servletConfig.getInitParameter("charsetName");
			if (StringUtils.isNull(charsetName)) {
				charsetName = "UTF-8";
			}
			
			httpServerApplication.setCharset(Charset.forName(charsetName));
		}
		
		String rpcSignStr = servletConfig.getInitParameter("rpc-sign");
		if(StringUtils.isNull(rpcSignStr)){
			httpServerApplication.setRpcSignStr(rpcSignStr);
		}
		
		if (!debug) {
			String d = servletConfig.getInitParameter("debug");
			if (StringUtils.isNull(d)) {
				debug = false;
			} else {
				setDebug(StringUtils.conversion(d, boolean.class));
			}
		}

		if (StringUtils.isNull(rpcServletPath)) {
			rpcServletPath = servletConfig.getInitParameter("rpc-path");
			if(StringUtils.isNull(rpcServletPath)){
				rpcServletPath = "/rpc";
			}
		}
		
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
			return new FormRequest(httpServerApplication.getBeanFactory(), httpServletRequest, httpServletResponse, debug);
		}else if(httpServletRequest.getContentType().startsWith(ContentType.JSON.getValue())){
			return new JsonRequest(httpServerApplication.getBeanFactory(), httpServletRequest, httpServletResponse, debug);
		}else{
			return new FormRequest(httpServerApplication.getBeanFactory(), httpServletRequest, httpServletResponse, debug);
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
