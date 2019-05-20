package scw.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;

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
import scw.servlet.http.HttpWrapperFactory;
import scw.servlet.http.filter.NotFoundService;
import scw.servlet.http.filter.ParameterActionService;
import scw.servlet.http.filter.RPCFilter;
import scw.servlet.http.filter.RestService;
import scw.servlet.http.filter.ServletPathService;

public class DefaultServletService extends AbstractServletService {
	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private final PropertiesFactory propertiesFactory;
	private final BeanFactory beanFactory;
	private final WrapperFactory wrapperFactory;
	private final RequestBeanFactory requestBeanFactory;
	private final Charset charset;
	private final String rpcPath;

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
		this.rpcPath = StringUtils.isEmpty(path) ? "/rpc" : path;

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
		Filter parameterActionService = beanFactory.get(ParameterActionService.class, beanFactory, classes, actionKey);
		add(parameterActionService);
		Filter servletPathService = beanFactory.get(ServletPathService.class, beanFactory, classes);
		add(servletPathService);
		Filter restService = beanFactory.get(RestService.class, beanFactory, classes);
		add(restService);
		Filter notFoundService = beanFactory.get(NotFoundService.class);
		add(notFoundService);
	}

	public PropertiesFactory getPropertiesFactory() {
		return propertiesFactory;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	@Override
	public void service(ServletRequest req, ServletResponse resp) {
		if (charset != null) {
			try {
				req.setCharacterEncoding(charset.name());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			resp.setCharacterEncoding(charset.name());
		}
		super.service(req, resp);
	}

	@Override
	protected void error(ServletRequest request, ServletResponse response, Throwable e) {
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
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

	@Override
	protected WrapperFactory getWrapperFactory() {
		return wrapperFactory;
	}
}
