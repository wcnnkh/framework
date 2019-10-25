package scw.mvc.action;

import scw.core.instance.InstanceFactory;
import scw.core.ip.IP;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.Channel;
import scw.mvc.Request;
import scw.mvc.RequestResponseModel;
import scw.mvc.annotation.CountLimitSecurity;
import scw.mvc.annotation.IPSecurity;
import scw.mvc.annotation.ResponseWrapper;
import scw.mvc.limit.CountLimitConfigFactory;
import scw.mvc.wrapper.ResponseWrapperService;
import scw.result.exception.AuthorizationFailureException;
import scw.security.ip.IPVerification;
import scw.security.limit.CountLimit;
import scw.security.limit.CountLimitConfig;
import scw.security.limit.CountLimitFactory;

/**
 * 默认的action-filter 实现对内置注解的支持和一些默认的实现
 * 
 * @author shuchaowen
 *
 */
public final class DefaultActionFilter extends MethodActionFilter {
	private static Logger logger = LoggerFactory.getLogger(DefaultActionFilter.class);
	private static final boolean RESPONSE_COMPATIBLE_OPEN = StringUtils
			.parseBoolean(SystemPropertyUtils.getProperty("mvc.response.compatible.result"), true);
	private ResponseWrapperService responseWrapperService;
	private InstanceFactory instanceFactory;
	// 默认是否开启对ResultFactory的兼容

	public DefaultActionFilter(InstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
		this.responseWrapperService = instanceFactory.isInstance(ResponseWrapperService.class)
				? instanceFactory.getInstance(ResponseWrapperService.class) : null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Object filter(MethodAction action, Channel channel, ActionFilterChain chain) throws Throwable {
		IPSecurity ipSecurity = action.getAnnotation(IPSecurity.class);
		if (ipSecurity != null) {
			boolean b = true;
			if (channel instanceof IP) {
				b = verificationIP((IP) channel, ipSecurity);
			} else if (channel instanceof RequestResponseModel) {
				Request request = ((RequestResponseModel) channel).getRequest();
				if (request instanceof IP) {
					b = verificationIP((IP) request, ipSecurity);
				}
			}

			if (!b) {
				throw new AuthorizationFailureException("ip验证失败");
			}
		}

		CountLimitSecurity countLimitSecurity = action.getAnnotation(CountLimitSecurity.class);
		if (countLimitSecurity != null) {
			boolean b = countLimitSecurity(countLimitSecurity, action, channel);
			if (!b) {
				throw new AuthorizationFailureException("访问过于频繁");
			}
		}

		Object value = chain.doFilter(action, channel);
		return responseSupport(action, channel, value);
	}

	private Object responseSupport(MethodAction action, Channel channel, Object value) throws Throwable {
		if (RESPONSE_COMPATIBLE_OPEN && responseWrapperService != null) {
			return responseWrapperService.wrapper(channel, value);
		}

		ResponseWrapper responseWrapper = action.getAnnotation(ResponseWrapper.class);
		if (responseWrapper != null && responseWrapper.value()) {
			ResponseWrapperService responseWrapperService = instanceFactory.getInstance(responseWrapper.service());
			return responseWrapperService.wrapper(channel, value);
		}

		return value;
	}

	private boolean countLimitSecurity(CountLimitSecurity countLimitSecurity, MethodAction action, Channel channel) {
		boolean instance = instanceFactory.isInstance(countLimitSecurity.value());
		if (instance) {
			instance = instanceFactory.isInstance(countLimitSecurity.factory());
		}

		if (!instance) {
			logger.warn("无法实例化：", countLimitSecurity.value());
			return false;
		}

		if (instance) {
			CountLimitConfigFactory configFactory = instanceFactory.getInstance(countLimitSecurity.value());
			CountLimitConfig countLimitConfig = configFactory.getCountLimitConfig(action, channel);
			if (countLimitConfig != null) {
				CountLimitFactory countLimitFactory = instanceFactory.getInstance(CountLimitFactory.class);
				CountLimit countLimit = countLimitFactory.getCountLimit(countLimitConfig);
				if (countLimit != null && !countLimit.incr()) {
					if (logger.isDebugEnabled()) {
						logger.debug("The number of visits has exceeded the limit, max={}, count={}",
								countLimitConfig.getMaxCount(), countLimit.getCount());
					}
					return false;
				}
			}
		}

		return true;
	}

	private boolean verificationIP(IP ip, IPSecurity ipSecurity) {
		if (!instanceFactory.isInstance(ipSecurity.value())) {
			logger.warn("无法初始化:{}", ipSecurity.value());
			return false;
		}

		String ipValue = ip.getIP();
		IPVerification ipVerification = instanceFactory.getInstance(ipSecurity.value());
		boolean b = ipVerification.verification(ipValue);
		if (logger.isDebugEnabled()) {
			logger.debug("verification ip [{}] {}", ipValue, b ? "success" : "fail");
		}
		return b;
	}
}
