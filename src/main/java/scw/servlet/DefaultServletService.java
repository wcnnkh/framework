package scw.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
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
import scw.core.Destroy;
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
import scw.servlet.http.HttpWrapperFactory;
import scw.servlet.http.filter.NotFoundServiceFilter;
import scw.servlet.http.filter.ParameterActionServiceFilter;
import scw.servlet.http.filter.RPCFilter;
import scw.servlet.http.filter.RestServiceFilter;
import scw.servlet.http.filter.ServletPathServiceFilter;

public class DefaultServletService extends LinkedList<Filter> implements ServletService {
	private static final long serialVersionUID = 1L;
	protected Logger logger = LoggerFactory.getLogger(getClass());

	private final PropertiesFactory propertiesFactory;
	private final BeanFactory beanFactory;
	private final WrapperFactory wrapperFactory;
	private final RequestBeanFactory requestBeanFactory;
	private final Charset charset;

	public DefaultServletService(BeanFactory beanFactory, PropertiesFactory propertiesFactory, String configPath,
			String[] rootBeanFilters) throws Throwable {
		this.beanFactory = beanFactory;
		this.propertiesFactory = propertiesFactory;
		this.requestBeanFactory = beanFactory.get(CommonRequestBeanFactory.class, beanFactory, propertiesFactory,
				configPath, rootBeanFilters);

		JSONParseSupport jsonParseSupport;
		String jsonParseSupportBeanName = propertiesFactory.getValue("servlet.json");
		if (StringUtils.isEmpty(jsonParseSupportBeanName)) {
			jsonParseSupport = JSONUtils.DEFAULT_PARSE_SUPPORT;
		} else {
			jsonParseSupport = beanFactory.get(jsonParseSupportBeanName);
		}

		// 将下面的字符串(如：servlet.debug)设置为常量可以提高代码可读性，但此字符串只使用一次，设置为常量会浪费一部分内存
		// 默认开启日志
		boolean debug = StringParseUtils.parseBoolean(propertiesFactory.getValue("servlet.debug"), true);
		String charsetName = propertiesFactory.getValue("servlet.charsetName");
		this.charset = StringUtils.isEmpty(charsetName) ? Constants.DEFAULT_CHARSET : Charset.forName(charsetName);
		String requestFactoryBeanName = propertiesFactory.getValue("servlet.request-factory");
		if (StringUtils.isEmpty(requestFactoryBeanName)) {
			this.wrapperFactory = beanFactory.get(HttpWrapperFactory.class, requestBeanFactory, debug,
					StringParseUtils.parseBoolean(propertiesFactory.getValue("servlet.parameter.cookie"), false),
					jsonParseSupport);
		} else {
			this.wrapperFactory = beanFactory.get(requestFactoryBeanName);
		}

		String path = propertiesFactory.getValue("servlet.rpc-path");
		String rpcPath = StringUtils.isEmpty(path) ? "/rpc" : path;

		RpcService rpcService;
		String rpcServerBeanName = propertiesFactory.getValue("servlet.rpc");
		if (StringUtils.isEmpty(rpcServerBeanName)) {
			String sign = propertiesFactory.getValue("servlet.rpc-sign");
			boolean enable = StringParseUtils.parseBoolean(propertiesFactory.getValue("servlet.rpc-enable"), false);
			if (enable || !StringUtils.isEmpty(sign)) {// 开启
				logger.info("rpc签名：{}", sign);
				String serializer = propertiesFactory.getValue("servlet.rpc-serializer");
				rpcService = beanFactory.get(DefaultRpcService.class, beanFactory, sign, StringUtils.isEmpty(serializer)
						? Constants.DEFAULT_SERIALIZER : (Serializer) beanFactory.get(serializer));
			} else {
				rpcService = null;
			}
		} else {
			rpcService = beanFactory.get(rpcServerBeanName);
		}

		String filterNames = propertiesFactory.getValue("servlet.filters");
		if (!StringUtils.isEmpty(filterNames)) {
			Collection<Filter> rootFilter = BeanUtils.getBeanList(beanFactory,
					Arrays.asList(StringUtils.commonSplit(filterNames)));
			addAll(rootFilter);
		}

		String actionKey = propertiesFactory.getValue("servlet.actionKey");
		actionKey = StringUtils.isEmpty(actionKey) ? "action" : actionKey;
		String packageName = propertiesFactory.getValue("servlet.scanning");
		packageName = StringUtils.isEmpty(packageName) ? "" : packageName;

		Collection<Class<?>> classes = ClassUtils.getClasses(packageName);
		Filter rpcFilter = beanFactory.get(RPCFilter.class, rpcPath, rpcService);
		add(rpcFilter);
		Filter parameterActionService = beanFactory.get(ParameterActionServiceFilter.class, beanFactory, classes,
				actionKey);
		add(parameterActionService);
		Filter servletPathService = beanFactory.get(ServletPathServiceFilter.class, beanFactory, classes);
		add(servletPathService);
		Filter restService = beanFactory.get(RestServiceFilter.class, beanFactory, classes);
		add(restService);
		Filter notFoundService = beanFactory.get(NotFoundServiceFilter.class);
		add(notFoundService);
	}

	protected WrapperFactory getWrapperFactory() {
		return wrapperFactory;
	}

	public Charset getCharset() {
		return charset;
	}

	public final PropertiesFactory getPropertiesFactory() {
		return propertiesFactory;
	}

	public final BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public RequestBeanFactory getRequestBeanFactory() {
		return requestBeanFactory;
	}

	public void service(ServletRequest req, ServletResponse resp) {
		if (getCharset() != null) {
			try {
				req.setCharacterEncoding(getCharset().name());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			resp.setCharacterEncoding(getCharset().name());
		}

		Request request = null;
		Response response = null;
		FilterChain filterChain = new scw.servlet.DefaultFilterChain(this, null);
		try {
			request = getWrapperFactory().wrapperRequest(req, resp);
			if (request == null) {
				return;
			}

			response = getWrapperFactory().wrapperResponse(request, resp);
			if (response == null) {
				return;
			}

			filterChain.doFilter(request, response);
		} catch (Throwable e) {
			error(request, response, e);
		} finally {
			if (request != null) {
				if (request instanceof Destroy) {
					((Destroy) request).destroy();
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
