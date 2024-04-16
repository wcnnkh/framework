package io.basc.framework.autoconfigure.beans.factory;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.execution.ParameterExtractor;
import io.basc.framework.util.StringUtils;
import io.basc.framework.value.ParameterDescriptor;
import lombok.Data;

@Data
public class AutowriedParameterExtractor implements ParameterExtractor<BeanFactory> {

	@Override
	public boolean canExtractParameter(BeanFactory source, ParameterDescriptor parameterDescriptor) {
		return parameterDescriptor.getTypeDescriptor().isAnnotationPresent(Autowired.class);
	}

	@Override
	public Object extractParameter(BeanFactory source, ParameterDescriptor parameterDescriptor) {
		Autowired autowired = parameterDescriptor.getTypeDescriptor().getAnnotation(Autowired.class);
		String name = autowired.value();
		if (StringUtils.isNotEmpty(name)) {
			return source.getBean(name, parameterDescriptor.getTypeDescriptor().getType());
		}

		// 使用类型或名称
		return source.getBeanProvider(parameterDescriptor.getTypeDescriptor().getResolvableType()).getUnique()
				.orElseGet(() -> {
					return source.getBean(parameterDescriptor.getName(),
							parameterDescriptor.getTypeDescriptor().getType());
				});
	}
}
