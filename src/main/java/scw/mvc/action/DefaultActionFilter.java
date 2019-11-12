package scw.mvc.action;

import scw.core.instance.InstanceFactory;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.Channel;
import scw.mvc.FilterChain;
import scw.mvc.MVCUtils;
import scw.mvc.annotation.IPSecurity;
import scw.mvc.annotation.ResponseWrapper;
import scw.mvc.wrapper.ResponseWrapperService;
import scw.security.ip.IPValidationFailedException;
import scw.security.ip.IPVerification;

/**
 * 默认的action-filter 实现对内置注解的支持和一些默认的实现
 * 
 * @author shuchaowen
 *
 */
public final class DefaultActionFilter extends ActionFilter {
	private static Logger logger = LoggerFactory.getLogger(DefaultActionFilter.class);

	// 默认是否开启对ResultFactory的兼容
	private static final boolean RESPONSE_COMPATIBLE_OPEN = StringUtils
			.parseBoolean(SystemPropertyUtils.getProperty("mvc.response.compatible.result"), true);
	private ResponseWrapperService responseWrapperService;
	private final InstanceFactory instanceFactory;

	public DefaultActionFilter(InstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
		if(RESPONSE_COMPATIBLE_OPEN){
			this.responseWrapperService = instanceFactory.isInstance(ResponseWrapperService.class)
					? instanceFactory.getInstance(ResponseWrapperService.class) : null;
		}
	}

	@Override
	protected Object filter(Action action, Channel channel, FilterChain chain) throws Throwable {
		IPSecurity ipSecurity = action.getAnnotation(IPSecurity.class);
		if (ipSecurity != null) {
			boolean b = verificationIP(MVCUtils.getIP(channel), ipSecurity);
			if (!b) {
				throw new IPValidationFailedException("ip验证失败");
			}
		}

		Object value = chain.doFilter(channel);
		return responseSupport(action, channel, value);
	}

	private Object responseSupport(Action action, Channel channel, Object value) throws Throwable {
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
}
