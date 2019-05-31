package scw.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.beans.BeanFactory;
import scw.beans.rpc.http.RpcService;
import scw.core.Destroy;
import scw.core.PropertiesFactory;
import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.servlet.beans.RequestBeanFactory;

public class DefaultServletService implements ServletService {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	private final PropertiesFactory propertiesFactory;
	private final BeanFactory beanFactory;
	private final WrapperFactory wrapperFactory;
	private final String charsetName;
	private final String rpcPath;
	private final RpcService rpcService;
	private final List<Filter> filters;
	private final int warnExecuteTime;

	public DefaultServletService(BeanFactory beanFactory, PropertiesFactory propertiesFactory, String configPath,
			String[] rootBeanFilters) throws Throwable {
		this.beanFactory = beanFactory;
		this.propertiesFactory = propertiesFactory;
		this.charsetName = ServletUtils.getCharsetName(propertiesFactory);
		this.warnExecuteTime = ServletUtils.getWarnExecuteTime(propertiesFactory);

		RequestBeanFactory requestBeanFactory = ServletUtils.getRequestBeanFactory(beanFactory, propertiesFactory,
				configPath, rootBeanFilters);
		this.wrapperFactory = ServletUtils.getWrapperFactory(beanFactory, requestBeanFactory, propertiesFactory);

		this.rpcPath = ServletUtils.getRPCPath(propertiesFactory);
		this.rpcService = ServletUtils.getRPCService(beanFactory, propertiesFactory);

		List<Filter> filters = ServletUtils.getFilters(beanFactory, propertiesFactory);
		this.filters = Arrays.asList(filters.toArray(new Filter[filters.size()]));
	}

	public final String getCharsetName() {
		return charsetName;
	}

	public final PropertiesFactory getPropertiesFactory() {
		return propertiesFactory;
	}

	public final BeanFactory getBeanFactory() {
		return beanFactory;
	}

	protected final boolean checkRPCEnable(HttpServletRequest request) {
		if (rpcService == null) {
			return false;
		}

		if (!"POST".equals(request.getMethod())) {
			return false;
		}

		if (!request.getServletPath().equals(rpcPath)) {
			return false;
		}
		return true;
	}

	public void service(ServletRequest req, ServletResponse resp) {
		if (getCharsetName() != null) {
			try {
				req.setCharacterEncoding(getCharsetName());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			resp.setCharacterEncoding(getCharsetName());
		}

		if (req instanceof HttpServletRequest) {
			if (checkRPCEnable((HttpServletRequest) req)) {
				try {
					rpcService.service(req.getInputStream(), resp.getOutputStream());
				} catch (Throwable e) {
					error(req, resp, e);
				}
				return;
			}
		}

		Request request = null;
		Response response = null;
		FilterChain filterChain = new scw.servlet.IteratorFilterChain(filters, null);
		long t = System.currentTimeMillis();
		try {
			request = wrapperFactory.wrapperRequest(req, resp);
			if (request == null) {
				return;
			}

			response = wrapperFactory.wrapperResponse(request, resp);
			if (response == null) {
				return;
			}

			filterChain.doFilter(request, response);
		} catch (Throwable e) {
			error(request, response, e);
		} finally {
			try {
				if (request != null) {
					if (request instanceof Destroy) {
						((Destroy) request).destroy();
					}
				}

				if (response != null) {
					if (response instanceof Destroy) {
						((Destroy) response).destroy();
					}
				}
			} finally {
				t = System.currentTimeMillis() - t;
				if (t > warnExecuteTime) {
					logger.warn("执行{}超时，用时{}ms", request.toString(), t);
				}
			}
		}
	}

	protected void error(ServletRequest request, ServletResponse response, Throwable e) {
		if (!response.isCommitted() && request instanceof HttpServletRequest
				&& response instanceof HttpServletResponse) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;
			if (!httpServletResponse.isCommitted()) {
				StringBuilder sb = new StringBuilder();
				sb.append("servletPath=").append(httpServletRequest.getServletPath());
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
