package io.basc.framework.mvc.message.support;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.HttpMessage;

/**
 * 应该排在最后一个
 * 
 * @author shuchaowen
 *
 */
public class ConversionWebMessageConverter extends AbstractWebMessageConverter {

	@Override
	public boolean isAccept(HttpMessage message, TypeDescriptor typeDescriptor) {
		return true;
	}

}
