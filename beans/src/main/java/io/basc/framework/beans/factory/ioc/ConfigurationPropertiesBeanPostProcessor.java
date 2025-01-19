package io.basc.framework.beans.factory.ioc;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.config.BeanPostProcessor;
import io.basc.framework.core.mapping.PropertyFactory;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ConfigurationPropertiesBeanPostProcessor implements BeanPostProcessor {
	@NonNull
	private final PropertyFactory propertyFactory;

	@Override
	public void postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if(bean == null) {
			return ;
		}
		
	}
}
