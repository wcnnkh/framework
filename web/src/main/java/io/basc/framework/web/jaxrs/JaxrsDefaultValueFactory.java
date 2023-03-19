package io.basc.framework.web.jaxrs;

import javax.ws.rs.DefaultValue;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.factory.BeanResolver;
import io.basc.framework.factory.BeanResolverExtend;
import io.basc.framework.mapper.ParameterDescriptor;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class JaxrsDefaultValueFactory implements BeanResolverExtend {

	@Override
	public Object getDefaultParameter(ParameterDescriptor parameterDescriptor, BeanResolver chain) {
		DefaultValue defaultValue = AnnotatedElementUtils.getMergedAnnotation(parameterDescriptor, DefaultValue.class);
		if (defaultValue != null) {
			return defaultValue.value();
		}
		return BeanResolverExtend.super.getDefaultParameter(parameterDescriptor, chain);
	}
}
