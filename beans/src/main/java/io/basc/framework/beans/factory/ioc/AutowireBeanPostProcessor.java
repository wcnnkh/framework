package io.basc.framework.beans.factory.ioc;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.config.AutowireCapableBeanFactory;
import io.basc.framework.beans.factory.config.BeanPostProcessor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class AutowireBeanPostProcessor implements BeanPostProcessor {
	@NonNull
	private final AutowireCapableBeanFactory autowireCapableBeanFactory;

	@Override
	public void postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if(bean == null) {
			return ;
		}
		
		
	}
}
