package io.basc.framework.beans.factory.config.support;

import io.basc.framework.execution.param.ParameterExtractor;
import io.basc.framework.mapper.Element;
import io.basc.framework.mapper.MappingRegistry;
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

	public AutowiredParameterBeanPostProcessor(MappingRegistry mappingFactory, ParameterExtractor parameterExtractor) {
		super(mappingFactory);
		this.parameterExtractor = parameterExtractor;
	}

	@Override
	protected boolean canAutwired(Object bean, String beanName, Element field) {
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
	protected void autowired(Object bean, String beanName, Element field) {
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
