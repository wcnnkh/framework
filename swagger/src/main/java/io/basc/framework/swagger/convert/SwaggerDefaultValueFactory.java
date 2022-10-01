package io.basc.framework.swagger.convert;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.factory.BeanResolver;
import io.basc.framework.factory.BeanResolverExtend;
import io.basc.framework.util.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;

@Provider(order = Ordered.DEFAULT_PRECEDENCE)
public class SwaggerDefaultValueFactory implements BeanResolverExtend {

	@Override
	public Object getDefaultParameter(ParameterDescriptor parameterDescriptor, BeanResolver chain) {
		Schema defaultValue = AnnotatedElementUtils.getMergedAnnotation(parameterDescriptor, Schema.class);
		if (defaultValue != null && StringUtils.isNotEmpty(defaultValue.defaultValue())) {
			return defaultValue.defaultValue();
		}
		return BeanResolverExtend.super.getDefaultParameter(parameterDescriptor, chain);
	}

}
