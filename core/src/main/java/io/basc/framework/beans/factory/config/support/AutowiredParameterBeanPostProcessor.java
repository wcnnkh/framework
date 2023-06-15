package io.basc.framework.beans.factory.config.support;

import io.basc.framework.execution.parameter.ParameterExtractor;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.MappingFactory;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.Setter;

/**
 * 通过参数解析来完成注入
 * 
 * @author shuchaowen
 *
 */
public class AutowiredParameterBeanPostProcessor extends AutowiredBeanPostProcessor {
	private final ParameterExtractor parameterExtractor;

	public AutowiredParameterBeanPostProcessor(MappingFactory mappingFactory, ParameterExtractor parameterExtractor) {
		super(mappingFactory);
		this.parameterExtractor = parameterExtractor;
	}

	@Override
	protected boolean canAutwired(Object bean, String beanName, Field field) {
		if (!field.isSupportSetter()) {
			return false;
		}

		Setter first = field.getSetters().first();
		for (Setter setter : field.getSetters()) {
			// 不同名，但同类型
			ParameterDescriptor parameterDescriptor = first.rename(setter.getName());
			if (parameterExtractor.canExtractParameter(parameterDescriptor)) {
				return true;
			}

			for (String name : field.getAliasNames()) {
				parameterDescriptor = first.rename(name);
				if (parameterExtractor.canExtractParameter(parameterDescriptor)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected void autowired(Object bean, String beanName, Field field) {
		Setter first = field.getSetters().first();
		for (Setter setter : field.getSetters()) {
			// 不同名，但同类型
			ParameterDescriptor parameterDescriptor = first.rename(setter.getName());
			if (parameterExtractor.canExtractParameter(parameterDescriptor)) {
				Object value = parameterExtractor.extractParameter(parameterDescriptor);
				first.set(bean, value);
				return;
			}

			for (String name : field.getAliasNames()) {
				parameterDescriptor = first.rename(name);
				if (parameterExtractor.canExtractParameter(parameterDescriptor)) {
					Object value = parameterExtractor.extractParameter(parameterDescriptor);
					first.set(bean, value);
					return;
				}
			}
		}
	}
}
