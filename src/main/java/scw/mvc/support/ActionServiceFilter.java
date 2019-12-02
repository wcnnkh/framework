package scw.mvc.support;

import java.util.Collection;

import scw.beans.BeanFactory;
import scw.context.Context;
import scw.core.PropertyFactory;
import scw.core.instance.InstanceFactory;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.Action;
import scw.mvc.Channel;
import scw.mvc.Filter;
import scw.mvc.FilterChain;
import scw.mvc.MVCUtils;
import scw.mvc.SimpleFilterChain;
import scw.mvc.annotation.IPSecurity;
import scw.mvc.annotation.ResponseBody;
import scw.security.ip.IPValidationFailedException;
import scw.security.ip.IPVerification;

public final class ActionServiceFilter implements Filter {
	private static Logger logger = LoggerFactory.getLogger(ActionServiceFilter.class);
	// 默认是否开启对ResultFactory的兼容
	private static final boolean RESPONSE_COMPATIBLE_OPEN = StringUtils
			.parseBoolean(SystemPropertyUtils.getProperty("mvc.response.compatible.result"), true);
	private final ActionFactory actionFactory;
	private final Collection<Filter> notFoundFilters;
	private final InstanceFactory instanceFactory;
	private ResponseBodyService globalResponseWrapperService;

	public ActionServiceFilter(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		this(MVCUtils.getActionFactory(beanFactory, propertyFactory),
				MVCUtils.getNotFoundFilters(beanFactory, propertyFactory), beanFactory,
				initGlobalResponseWrapperService(beanFactory, propertyFactory));
	}

	private static ResponseBodyService initGlobalResponseWrapperService(InstanceFactory instanceFactory,
			PropertyFactory propertyFactory) {
		String beanName = StringUtils.toString(propertyFactory.getProperty("mvc.response.body.globa.service"),
				ResponseBodyService.class.getName());
		return (ResponseBodyService) (instanceFactory.isInstance(beanName) ? instanceFactory.getInstance(beanName)
				: null);
	}

	public ActionServiceFilter(ActionFactory actionFactory, Collection<Filter> notFoundFilter,
			InstanceFactory instanceFactory, ResponseBodyService globalResponseWrapperService) {
		this.actionFactory = actionFactory;
		this.notFoundFilters = notFoundFilter;
		this.instanceFactory = instanceFactory;
		this.globalResponseWrapperService = globalResponseWrapperService;
	}

	public Object doFilter(Channel channel, FilterChain chain) throws Throwable {
		Action action = actionFactory.getAction(channel);
		if (action == null) {
			FilterChain notFoundChain = new SimpleFilterChain(notFoundFilters, chain);
			return notFoundChain.doFilter(channel);
		}

		Context context = MVCUtils.getContext();
		if (context == null) {
			logger.warn("不存在上下文：{}", channel.toString());
		} else {
			context.bindResource(Action.class, action);
		}

		IPSecurity ipSecurity = action.getAnnotation(IPSecurity.class);
		if (ipSecurity != null) {
			boolean b = verificationIP(MVCUtils.getIP(channel), ipSecurity);
			if (!b) {
				throw new IPValidationFailedException("ip验证失败");
			}
		}

		Object value = action.doAction(channel);
		return responseBody(action, channel, value);
	}

	private boolean verificationIP(String ip, IPSecurity ipSecurity) {
		if (!instanceFactory.isInstance(ipSecurity.value())) {
			logger.warn("无法初始化:{}", ipSecurity.value());
			return false;
		}

		IPVerification ipVerification = instanceFactory.getInstance(ipSecurity.value());
		boolean b = ipVerification.verification(ip);
		if (b) {
			if (logger.isDebugEnabled()) {
				logger.debug("verification ip [{}] success", ip);
			}
		} else {
			logger.warn("verification ip [{}] fail", ip);
		}
		return b;
	}

	private Object responseBody(Action action, Channel channel, Object value) throws Throwable {
		if (RESPONSE_COMPATIBLE_OPEN && globalResponseWrapperService != null) {
			return globalResponseWrapperService.responseBody(channel, value);
		}

		ResponseBody responseBody = action.getAnnotation(ResponseBody.class);
		if (responseBody != null && responseBody.value()) {
			ResponseBodyService responseBodyService = instanceFactory.getInstance(responseBody.service());
			return responseBodyService.responseBody(channel, value);
		}

		return value;
	}
}
