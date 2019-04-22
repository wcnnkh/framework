package scw.servlet;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.property.PropertiesFactory;
import scw.beans.rpc.http.DefaultRpcService;
import scw.beans.rpc.http.RpcService;
import scw.common.Constants;
import scw.common.utils.CollectionUtils;
import scw.common.utils.StringParseUtils;
import scw.common.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.servlet.beans.CommonRequestBeanFactory;
import scw.servlet.beans.RequestBeanFactory;
import scw.servlet.request.DefaultRequestFactory;
import scw.servlet.request.RequestFactory;
import scw.servlet.service.CommonService;
import scw.servlet.service.DefaultServiceChain;
import scw.servlet.service.Service;

public class DefaultServletService implements ServletService {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private static final String CHARSET_NAME = "servlet.charsetName";
	// RPC
	private static final String RPC_SIGN = "servlet.rpc-sign";
	private static final String RPC_PATH = "servlet.rpc-path";
	private static final String RPC_ENABLE = "servlet.rpc-enable";
	private static final String RPC_SERVER = "servlet.rpc";

	private static final String REQUEST_FACTORY = "servlet.request-factory";
	private static final String DEFAULT_ACTION_KEY = "servlet.actionKey";
	private static final String DEFAULT_ACTION_FILTERS = "servlet.filters";
	private static final String DEBUG_KEY = "servlet.debug";
	private static final String SERVLET_SERVICE_NAME = "servlet.services";
	private static final String SERVLET_SCANNING_PACKAGENAME = "servlet.scanning";

	private final PropertiesFactory propertiesFactory;
	private final BeanFactory beanFactory;
	private final RequestFactory requestFactory;
	private final RequestBeanFactory requestBeanFactory;
	private final Charset charset;
	private final boolean debug;
	private final RpcService rpcService;
	private final String rpcPath;
	private final Collection<Service> services;
	private Collection<Filter> rootFilters;

	@SuppressWarnings("unchecked")
	public DefaultServletService(BeanFactory beanFactory, PropertiesFactory propertiesFactory, String configPath,
			String[] rootBeanFilters) throws Throwable {
		this.beanFactory = beanFactory;
		this.propertiesFactory = propertiesFactory;
		this.requestBeanFactory = new CommonRequestBeanFactory(beanFactory, propertiesFactory, configPath,
				rootBeanFilters);

		// 默认开启日志
		this.debug = StringParseUtils.parseBoolean(propertiesFactory.getValue(DEBUG_KEY), true);
		String charsetName = propertiesFactory.getValue(CHARSET_NAME);
		this.charset = StringUtils.isEmpty(charsetName) ? Constants.DEFAULT_CHARSET : Charset.forName(charsetName);
		String requestFactoryBeanName = propertiesFactory.getValue(REQUEST_FACTORY);
		if (StringUtils.isEmpty(requestFactoryBeanName)) {
			this.requestFactory = beanFactory.get(DefaultRequestFactory.class, this.debug);
		} else {
			this.requestFactory = beanFactory.get(requestFactoryBeanName);
		}

		String path = propertiesFactory.getValue(RPC_PATH);
		this.rpcPath = StringUtils.isEmpty(path) ? "/rpc" : path;

		String rpcServerBeanName = propertiesFactory.getValue(RPC_SERVER);
		if (StringUtils.isEmpty(rpcServerBeanName)) {
			String sign = propertiesFactory.getValue(RPC_SIGN);
			boolean enable = StringParseUtils.parseBoolean(propertiesFactory.getValue(RPC_ENABLE), false);
			if (enable || !StringUtils.isEmpty(sign)) {// 开启
				logger.info("rpc签名：{}", sign);
				this.rpcService = beanFactory.get(DefaultRpcService.class, beanFactory, sign);
			} else {
				this.rpcService = null;
			}
		} else {
			this.rpcService = beanFactory.get(rpcServerBeanName);
		}

		String filterNames = propertiesFactory.getValue(DEFAULT_ACTION_FILTERS);
		if (StringUtils.isEmpty(filterNames)) {
			this.rootFilters = Collections.EMPTY_LIST;
		} else {
			this.rootFilters = BeanUtils.getBeanList(beanFactory, Arrays.asList(StringUtils.commonSplit(filterNames)));
		}

		String serviceNames = propertiesFactory.getValue(SERVLET_SERVICE_NAME);
		if (StringUtils.isEmpty(serviceNames)) {
			String actionKey = propertiesFactory.getValue(DEFAULT_ACTION_KEY);
			actionKey = StringUtils.isEmpty(actionKey) ? "action" : actionKey;

			String packageName = propertiesFactory.getValue(SERVLET_SCANNING_PACKAGENAME);
			packageName = StringUtils.isEmpty(packageName) ? "" : packageName;
			CommonService commonService = beanFactory.get(CommonService.class, packageName, actionKey);
			this.services = new ArrayList<Service>(2);
			this.services.add(commonService);
		} else {
			this.services = BeanUtils.getBeanList(beanFactory, Arrays.asList(StringUtils.commonSplit(serviceNames)));
		}
	}

	public PropertiesFactory getPropertiesFactory() {
		return propertiesFactory;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public RequestFactory getRequestFactory() {
		return requestFactory;
	}

	public RequestBeanFactory getRequestBeanFactory() {
		return requestBeanFactory;
	}

	public Charset getCharset() {
		return charset;
	}

	public boolean isDebug() {
		return debug;
	}

	public RpcService getRpcService() {
		return rpcService;
	}

	public String getRpcPath() {
		return rpcPath;
	}

	public void service(ServletRequest req, ServletResponse resp) {
		if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {
			try {
				httpService((HttpServletRequest) req, (HttpServletResponse) resp);
			} catch (Throwable e) {
				sendError(req, resp, e);
			}
		}
	}

	public void sendError(ServletRequest req, ServletResponse resp, Throwable throwable) {
		if (req instanceof HttpServletRequest && req instanceof HttpServletResponse) {
			sendError((HttpServletRequest) req, (HttpServletResponse) resp, 500);
		}
		throwable.printStackTrace();
	}

	public void sendError(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, int code) {
		if (!httpServletResponse.isCommitted()) {
			StringBuilder sb = new StringBuilder();
			sb.append("servletPath=").append(httpServletRequest.getServletPath());
			sb.append(",method=").append(httpServletRequest.getMethod());
			sb.append(",status=").append(500);
			sb.append(",msg=").append("system error");
			String msg = sb.toString();
			try {
				httpServletResponse.sendError(code, msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
			logger.error(msg);
		}
	}

	public boolean rpc(HttpServletRequest req, HttpServletResponse resp) throws Throwable {
		if (rpcService == null) {
			return false;
		}

		if (!"POST".equals(req.getMethod())) {
			return false;
		}

		if (!rpcPath.equals(req.getServletPath())) {
			return false;
		}

		rpcService.service(req.getInputStream(), resp.getOutputStream());
		return true;
	}

	public void httpService(HttpServletRequest req, HttpServletResponse resp) throws Throwable {
		if (rpc(req, resp)) {
			return;
		}

		req.setCharacterEncoding(getCharset().name());
		resp.setCharacterEncoding(getCharset().name());
		
		Request request = requestFactory.format(getRequestBeanFactory(), req, resp);
		doAction(request, request.getResponse());
	}

	public void doAction(Request request, Response response) throws Throwable {
		DoAction doAction = new DoAction();
		doAction.doFilter(request, response);
	}

	final class DoAction implements FilterChain {
		private Iterator<Filter> iterator;

		public DoAction() {
			if (!CollectionUtils.isEmpty(rootFilters)) {
				iterator = rootFilters.iterator();
			}
		}

		public void doFilter(Request request, Response response) throws Throwable {
			if (iterator == null) {
				service(request, response);
				return;
			}

			if (iterator.hasNext()) {
				iterator.next().doFilter(request, response, this);
			} else {
				service(request, response);
			}
		}

		public void service(Request request, Response response) throws Throwable {
			DefaultServiceChain defaultServiceChain = new DefaultServiceChain(services);
			defaultServiceChain.service(request, response);
		}
	}
}
