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
public final class DefaultActionFilter extends MethodActionFilter {
	private static Logger logger = LoggerFactory.getLogger(DefaultActionFilter.class);
	
	// 默认是否开启对ResultFactory的兼容
	private static final boolean RESPONSE_COMPATIBLE_OPEN = StringUtils
			.parseBoolean(SystemPropertyUtils.getProperty("mvc.response.compatible.result"), true);
	private ResponseWrapperService responseWrapperService;
	private final InstanceFactory instanceFactory;

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
				throw new IPValidationFailedException("ip验证失败");
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

	private boolean verificationIP(IP ip, IPSecurity ipSecurity) {
		if (!instanceFactory.isInstance(ipSecurity.value())) {
			logger.warn("无法初始化:{}", ipSecurity.value());
			return false;
		}

		String ipValue = ip.getIP();
		IPVerification ipVerification = instanceFactory.getInstance(ipSecurity.value());
		boolean b = ipVerification.verification(ipValue);
		if(b){
			if (logger.isDebugEnabled()) {
				logger.debug("verification ip [{}] success", ipValue);
			}
		}else{
			logger.warn("verification ip [{}] fail", ipValue);
		}
		return b;
	}
}
