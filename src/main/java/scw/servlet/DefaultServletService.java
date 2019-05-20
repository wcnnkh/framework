package scw.servlet;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.property.PropertiesFactory;
import scw.beans.rpc.http.DefaultRpcService;
import scw.beans.rpc.http.RpcService;
import scw.core.Constants;
import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.serializer.Serializer;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringParseUtils;
import scw.core.utils.StringUtils;
import scw.json.JSONParseSupport;
import scw.json.JSONUtils;
import scw.servlet.beans.CommonRequestBeanFactory;
import scw.servlet.beans.RequestBeanFactory;
import scw.servlet.request.DefaultRequestFactory;
import scw.servlet.request.RequestFactory;
import scw.servlet.service.NotFoundService;
import scw.servlet.service.ParameterActionService;
import scw.servlet.service.RestService;
import scw.servlet.service.ServletPathService;

public class DefaultServletService implements ServletService {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private final PropertiesFactory propertiesFactory;
	private final BeanFactory beanFactory;
	private final RequestFactory requestFactory;
	private final JSONParseSupport jsonParseSupport;
	private final RequestBeanFactory requestBeanFactory;
	private final Charset charset;
	private final boolean debug;
	private final RpcService rpcService;
	private final String rpcPath;
	private final Collection<Filter> filters = new LinkedList<Filter>();

	public DefaultServletService(BeanFactory beanFactory, PropertiesFactory propertiesFactory, String configPath,
			String[] rootBeanFilters) throws Throwable {
		this.beanFactory = beanFactory;
		this.propertiesFactory = propertiesFactory;
		this.requestBeanFactory = new CommonRequestBeanFactory(beanFactory, propertiesFactory, configPath,
				rootBeanFilters);

		// 将下面的字符串(如：servlet.debug)设置为常量可以提高代码可读性，但此字符串只使用一次，设置为常量会浪费一部分内存
		// 默认开启日志
		this.debug = StringParseUtils.parseBoolean(propertiesFactory.getValue("servlet.debug"), true);
		String charsetName = propertiesFactory.getValue("servlet.charsetName");
		this.charset = StringUtils.isEmpty(charsetName) ? Constants.DEFAULT_CHARSET : Charset.forName(charsetName);
		String requestFactoryBeanName = propertiesFactory.getValue("servlet.request-factory");
		if (StringUtils.isEmpty(requestFactoryBeanName)) {
			this.requestFactory = beanFactory.get(DefaultRequestFactory.class, this.debug,
					StringParseUtils.parseBoolean(propertiesFactory.getValue("servlet.parameter.cookie"), false));
		} else {
			this.requestFactory = beanFactory.get(requestFactoryBeanName);
		}

		String jsonParseSupportBeanName = propertiesFactory.getValue("servlet.json");
		if (StringUtils.isEmpty(jsonParseSupportBeanName)) {
			this.jsonParseSupport = JSONUtils.DEFAULT_PARSE_SUPPORT;
		} else {
			this.jsonParseSupport = beanFactory.get(jsonParseSupportBeanName);
		}

		String path = propertiesFactory.getValue("servlet.rpc-path");
		this.rpcPath = StringUtils.isEmpty(path) ? "/rpc" : path;

		String rpcServerBeanName = propertiesFactory.getValue("servlet.rpc");
		if (StringUtils.isEmpty(rpcServerBeanName)) {
			String sign = propertiesFactory.getValue("servlet.rpc-sign");
			boolean enable = StringParseUtils.parseBoolean(propertiesFactory.getValue("servlet.rpc-enable"), false);
			if (enable || !StringUtils.isEmpty(sign)) {// 开启
				logger.info("rpc签名：{}", sign);
				String serializer = propertiesFactory.getValue("servlet.rpc-serializer");
				this.rpcService = beanFactory.get(DefaultRpcService.class, beanFactory, sign,
						StringUtils.isEmpty(serializer) ? Constants.DEFAULT_SERIALIZER
								: (Serializer) beanFactory.get(serializer));
			} else {
				this.rpcService = null;
			}
		} else {
			this.rpcService = beanFactory.get(rpcServerBeanName);
		}

		String filterNames = propertiesFactory.getValue("servlet.filters");
		if (!StringUtils.isEmpty(filterNames)) {
			Collection<Filter> rootFilter = BeanUtils.getBeanList(beanFactory,
					Arrays.asList(StringUtils.commonSplit(filterNames)));
			filters.addAll(rootFilter);
		}

		String actionKey = propertiesFactory.getValue("servlet.actionKey");
		actionKey = StringUtils.isEmpty(actionKey) ? "action" : actionKey;
		String packageName = propertiesFactory.getValue("servlet.scanning");
		packageName = StringUtils.isEmpty(packageName) ? "" : packageName;

		Collection<Class<?>> classes = ClassUtils.getClasses(packageName);
		Filter parameterActionService = beanFactory.get(ParameterActionService.class, beanFactory, classes, actionKey);
		Filter servletPathService = beanFactory.get(ServletPathService.class, beanFactory, classes);
		Filter restService = beanFactory.get(RestService.class, beanFactory, classes);
		Filter notFoundService = beanFactory.get(NotFoundService.class);
		filters.add(parameterActionService);
		filters.add(servletPathService);
		filters.add(restService);
		filters.add(notFoundService);
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

	public JSONParseSupport getJsonParseSupport() {
		return jsonParseSupport;
	}

	public void service(ServletRequest req, ServletResponse resp) {
		try {
			if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {
				httpService((HttpServletRequest) req, (HttpServletResponse) resp);
			}
		} catch (Throwable e) {
			sendError(req, resp, e);
		}
	}

	public void sendError(ServletRequest req, ServletResponse resp, Throwable throwable) {
		if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {
			sendError((HttpServletRequest) req, (HttpServletResponse) resp, 500);
		}
		throwable.printStackTrace();
	}

	public void sendError(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, int code) {
		if (!httpServletResponse.isCommitted()) {
			StringBuilder sb = new StringBuilder();
			sb.append("servletPath=").append(httpServletRequest.getServletPath());
			sb.append(",method=").append(httpServletRequest.getMethod());
			sb.append(",status=").append(code);
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

		Request request = requestFactory.format(getJsonParseSupport(), getRequestBeanFactory(), req, resp);
		try {
			doAction(request, request.getResponse());
		} finally {
			request.destroy();
		}
	}

	public void doAction(Request request, Response response) throws Throwable {
		DoAction doAction = new DoAction();
		doAction.doFilter(request, response);
	}

	final class DoAction implements FilterChain {
		private Iterator<Filter> iterator;

		public DoAction() {
			iterator = filters.iterator();
		}

		public void doFilter(Request request, Response response) throws Throwable {
			if (iterator.hasNext()) {
				iterator.next().doFilter(request, response, this);
			}
		}
	}
}
