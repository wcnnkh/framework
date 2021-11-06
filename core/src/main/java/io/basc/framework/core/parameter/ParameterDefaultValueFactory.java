package io.basc.framework.core.parameter;

import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.util.StringUtils;

/**
 * 默认的默认值获取
 * 
 * @author shuchaowen
 *
 */
public class ParameterDefaultValueFactory implements ParameterFactory {
	public static final ParameterDefaultValueFactory INSTANCE = new ParameterDefaultValueFactory();

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		return getParameter(parameterDescriptor) != null;
	}

	@Override
	public Object getParameter(ParameterDescriptor parameterDescriptor) {
		DefaultValue value = AnnotatedElementUtils.getMergedAnnotation(parameterDescriptor, DefaultValue.class);
		if (value != null && StringUtils.isNotEmpty(value.value())) {
			return value.value();
		}
		return null;
	}

}
