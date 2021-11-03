package io.basc.framework.mvc.message.support;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.Ordered;
import io.basc.framework.http.HttpMessage;

/**
 * 应该排在最后一个
 * 
 * @author shuchaowen
 *
 */
class LastWebMessageConverter extends AbstractWebMessageConverter implements Ordered {

	@Override
	public boolean isAccept(HttpMessage message, TypeDescriptor typeDescriptor) {
		return true;
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}
}
