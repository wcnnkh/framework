package scw.mvc.action;

import scw.core.instance.InstanceFactory;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.mvc.Channel;
import scw.mvc.annotation.ResponseWrapper;

/**
 * 为了兼容ResultFactory
 * 
 * @author shuchaowen
 *
 */
public final class ResponseWrapperFilter extends MethodActionFilter {
	// 默认是否开启对ResultFactory的兼容
	private static final boolean DEFAULT_OPEN = StringUtils
			.parseBoolean(SystemPropertyUtils.getProperty("mvc.response.compatible.result"), true);
	private InstanceFactory instanceFactory;
	private ResponseWrapperService responseWrapperService;

	public ResponseWrapperFilter(InstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
		this.responseWrapperService = instanceFactory.isInstance(ResponseWrapperService.class)
				? instanceFactory.getInstance(ResponseWrapperService.class) : null;
	}

	@Override
	protected Object filter(MethodAction action, Channel channel, ActionFilterChain chain) throws Throwable {
		Object value = chain.doFilter(action, channel);
		if (DEFAULT_OPEN && responseWrapperService != null) {
			return responseWrapperService.wrapper(channel, value);
		}

		ResponseWrapper responseWrapper = action.getAnnotation(ResponseWrapper.class);
		if (responseWrapper != null && responseWrapper.value()) {
			ResponseWrapperService responseWrapperService = instanceFactory.getInstance(responseWrapper.service());
			return responseWrapperService.wrapper(channel, value);
		}
		return value;
	}
}
