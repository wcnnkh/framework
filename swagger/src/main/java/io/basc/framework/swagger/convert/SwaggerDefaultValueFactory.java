package io.basc.framework.swagger.convert;

import io.basc.framework.beans.factory.BeanResolver;
import io.basc.framework.beans.factory.BeanResolverExtend;
import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;

@ConditionalOnParameters(order = Ordered.DEFAULT_PRECEDENCE)
public class SwaggerDefaultValueFactory implements BeanResolverExtend {

	@Override
	public Object getDefaultParameter(ParameterDescriptor parameterDescriptor, BeanResolver chain) {
		Schema defaultValue = AnnotatedElementUtils.getMergedAnnotation(parameterDescriptor.getTypeDescriptor(), Schema.class);
		if (defaultValue != null && StringUtils.isNotEmpty(defaultValue.defaultValue())) {
			return defaultValue.defaultValue();
		}
		return BeanResolverExtend.super.getDefaultParameter(parameterDescriptor, chain);
	}

}
