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
import scw.beans.annotation.Bean;
import scw.beans.property.ValueWiredManager;
import scw.beans.rpc.http.RpcService;
import scw.core.Destroy;
import scw.core.PropertyFactory;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.servlet.beans.RequestBeanFactory;

@Bean(proxy = false)
public class DefaultServletService implements ServletService {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	private final PropertyFactory propertyFactory;
	private final BeanFactory beanFactory;
	private final WrapperFactory wrapperFactory;
	private final String charsetName;
	private final String rpcPath;
	private final RpcService rpcService;
	private final List<Filter> filters;
	private final int warnExecuteTime;

	public DefaultServletService(ValueWiredManager valueWiredManager, BeanFactory beanFactory, PropertyFactory propertyFactory, String configPath,
			String[] rootBeanFilters) throws Throwable {
		this.beanFactory = beanFactory;
		this.propertyFactory = propertyFactory;
		this.charsetName = ServletUtils.getCharsetName(propertyFactory);
		this.warnExecuteTime = ServletUtils.getWarnExecuteTime(propertyFactory);

		RequestBeanFactory requestBeanFactory = ServletUtils.getRequestBeanFactory(valueWiredManager, beanFactory, propertyFactory,
				configPath, rootBeanFilters);
		this.wrapperFactory = ServletUtils.getWrapperFactory(beanFactory, requestBeanFactory, propertyFactory);

		this.rpcPath = ServletUtils.getRPCPath(propertyFactory);
		this.rpcService = ServletUtils.getRPCService(beanFactory, propertyFactory);

		List<Filter> filters = ServletUtils.getFilters(beanFactory, propertyFactory);
		this.filters = Arrays.asList(filters.toArray(new Filter[filters.size()]));
	}

	public final String getCharsetName() {
		return charsetName;
	}

	public final PropertyFactory getPropertyFactory() {
		return propertyFactory;
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

			ServletUtils.service(request, response, filters);
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
		logger.error(e, "执行{}异常", request == null ? null : request.toString());
		if (response != null && !response.isCommitted() && response instanceof HttpServletResponse) {
			try {
				((HttpServletResponse) response).sendError(500, "system error");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}
	}
}
