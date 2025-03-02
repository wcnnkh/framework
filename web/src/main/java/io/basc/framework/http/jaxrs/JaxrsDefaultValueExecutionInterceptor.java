package io.basc.framework.http.jaxrs;

import javax.ws.rs.DefaultValue;

import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.Executor;
import io.basc.framework.core.execution.aop.support.DefaultValueExecutionInterceptor;

public class JaxrsDefaultValueExecutionInterceptor extends DefaultValueExecutionInterceptor {

	@Override
	protected Source getDefaultValue(Executor executor, TypeDescriptor typeDescriptor) {
		DefaultValue defaultValue = AnnotatedElementUtils.getMergedAnnotation(typeDescriptor, DefaultValue.class);
		if (defaultValue != null) {
			return Source.of(defaultValue.value());
		}
		return null;
	}

}
